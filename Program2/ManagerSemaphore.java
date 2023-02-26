import java.util.Date;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.*;

class Manager {

    // Maximum time in between fan arrivals
    private static final int MAX_TIME_IN_BETWEEN_ARRIVALS = 3000;

    // Maximum amount of break time in between celebrity photos
    private static final int MAX_BREAK_TIME = 10000;

    // Maximum amount of time a fan spends in the exhibit
    private static final int MAX_EXHIBIT_TIME = 10000;

    // Minimum number of fans for a photo
    private static final int MIN_FANS = 3;

    // Maximum number of fans allowed in queue
    private static final int MAX_ALLOWED_IN_QUEUE = 10;

    // Holds the queue of fans
    private static ArrayList<Fan> line = new ArrayList<Fan>();

    // The current number of fans in line
    private static int numFansInLine = 0;

    // Semaphore to Keep track of empty spots
    private static Semaphore lineEmpty = new Semaphore(MAX_ALLOWED_IN_QUEUE);

    // Semaphore to keep track of full slots
    private static Semaphore lineFull = new Semaphore(0);

    // Mutex semaphore for accessing shared variables
    private static Semaphore mutex = new Semaphore(1);

    // For generating random times
    private Random rndGen = new Random(new Date().getTime());
    private Object rndLock = new Object();

    public static void main(String[] args) {
        new Manager().go();

    }

    private void go() {
        // Create the celebrity thread
        Celebrity c = new Celebrity();
        new Thread(c, "Celebrity").start();

        // Continually generate new fans
        int i = 0;
        while (true) {
            new Thread(new Fan(), "Fan " + i++).start();
            try {
                synchronized (rndLock) {
                    int randomTime = rndGen.nextInt(MAX_TIME_IN_BETWEEN_ARRIVALS);
                    Thread.sleep(randomTime);
                }
            } catch (InterruptedException e) {
                System.err.println(e.toString());
                System.exit(1);
            }
        }

    }

    class Celebrity implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // Wait for enough fans to be in line before taking a picture (this will require
                    // 3 full spots at least)
                    lineFull.acquire(MIN_FANS);
                } catch (InterruptedException e) {
                    e.getCause();
                    System.exit(1);
                }
                // Check to see if celebrity flips out
                checkCelebrityOK();

                // Take picture with fans
                try {
                    // Acquire the mutex to access the shared variable line and numFanInLine
                    mutex.acquire();
                    System.out.println("Celebrity takes a picture with fans");

                    // Remove the fans from the line
                    for (int i = 0; i < MIN_FANS; i++) {
                        System.out.println(line.remove(0).getName() + ": OMG! Thank you!");
                    }
                    // Adjust the numFans variable
                    numFansInLine -= MIN_FANS;
                } catch (InterruptedException e) {
                    e.getCause();
                    System.exit(1);
                } finally {
                    // Release the mutex and lineEmpty semaphores (it releases 3 spots since it
                    // romoved 3 fans)
                    mutex.release();
                    lineEmpty.release(MIN_FANS);
                }

                // Take a break
                try {
                    synchronized (rndLock) {
                        int randomTime = rndGen.nextInt(MAX_BREAK_TIME);
                        Thread.sleep(randomTime);
                    }
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                    System.exit(1);
                }
            }

        }

    }

    public void checkCelebrityOK() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.getCause();
            System.exit(1);
        }
        if (numFansInLine > MAX_ALLOWED_IN_QUEUE) {
            System.err.println("Celebrity becomes claustrophobic and flips out");
            System.exit(1);
        }

        if (numFansInLine < MIN_FANS) {
            System.err.println("Celebrity becomes enraged that he was woken from nap for too few fans");
            System.exit(1);
        }
        mutex.release();
    }

    class Fan implements Runnable {
        String name;

        public String getName() {
            return name;
        }

        @Override
        public void run() {
            // Set the thread name
            name = Thread.currentThread().toString();

            System.out.println(Thread.currentThread() + ": arrives");

            // Look in the exhibit for a little while
            try {
                synchronized (rndLock) {
                    int randomTime = rndGen.nextInt(MAX_EXHIBIT_TIME);
                    Thread.sleep(randomTime);
                }
            } catch (InterruptedException e) {
                System.err.println(e.toString());
                System.exit(1);
            }

            // Get in line
            try {
                // Acquire the lineEmpty semaphore or wait for an empty spot in the line
                lineEmpty.acquire();
            } catch (InterruptedException e) {
                e.getCause();
                System.exit(1);
            }
            try {
                // Acquire the mutex to access the shared variable line and numFansInLine
                mutex.acquire();
                System.out.println(Thread.currentThread() + ": gets in line");
                line.add(0, this);
                numFansInLine++;
            } catch (InterruptedException e) {
                e.getCause();
                System.exit(1);
            } finally {
                // Release the mutex and lineFull semaphores
                mutex.release();
                lineFull.release();
            }

        }

    }
}
