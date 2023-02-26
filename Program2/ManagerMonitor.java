import java.util.Date;
import java.util.Random;
import java.util.ArrayList;

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

                // Take picture with
                synchronized (line) {
                    if (numFansInLine < MIN_FANS) {
                        // If there are not enough fans in line, the celebrity waits
                        try {
                            line.wait();
                            checkCelebrityOK(); // Check if the celebrity was awakened at the right time
                        } catch (InterruptedException e) {
                            e.getCause();
                            System.exit(1);
                        }
                    }
                }

                System.out.println("Celebrity takes a picture with fans");
                synchronized (line) {
                    // Remove the fans from the line
                    for (int i = 0; i < MIN_FANS; i++) {
                        System.out.println(line.remove(0).getName() + ": OMG! Thank you!");
                    }
                    // Adjust the numFans variable
                    numFansInLine -= MIN_FANS;
                    // notify all fan threads that there are free spots so they can enter the line
                    line.notifyAll();
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

    public synchronized void checkCelebrityOK() {

        if (numFansInLine > MAX_ALLOWED_IN_QUEUE) {
            System.err.println("Celebrity becomes claustrophobic and flips out");
            System.exit(1);
        }

        if (numFansInLine < MIN_FANS) {
            System.err.println("Celebrity becomes enraged that he was woken from nap for too few fans");
            System.exit(1);
        }

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
            synchronized (line) {
                // Check if the line is full
                if (numFansInLine == MAX_ALLOWED_IN_QUEUE) {
                    try {
                        // The Fan thread should wait to enter the thread
                        line.wait();
                    } catch (InterruptedException e) {
                        e.getCause();
                        System.exit(1);
                    }

                }
                // get in line
                System.out.println(Thread.currentThread() + ": gets in line");
                line.add(0, this);
                numFansInLine++;
                // if the number of fans exceed the minimum number call the celebrity thread
                if (numFansInLine >= MIN_FANS) {
                    line.notify();
                }

            }

        }

    }
}
