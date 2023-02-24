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

    // Monitor object for accessing shared variables
    private static final Object monitor = new Object();

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
                    synchronized (monitor) {
                        while (numFansInLine < MIN_FANS) {
                            monitor.wait();
                        }
                    }

                    // Check to see if celebrity flips out
                    checkCelebrityOK();

                    // Take picture with fans
                    System.out.println("Celebrity takes a picture with fans");

                    // Remove the fans from the line
                    synchronized (monitor) {
                        for (int i = 0; i < MIN_FANS; i++) {
                            Fan fan = line.remove(0);
                            System.out.println(fan.getName() + ": OMG! Thank you!");
                        }
                        numFansInLine -= MIN_FANS;
                        monitor.notifyAll();
                    }

                    // Take a break
                    try {
                        synchronized (rndLock) {
                            int randomTime = rndGen.nextInt(MAX_BREAK_TIME);
                            Thread.sleep(randomTime);
                        };
                    } catch (InterruptedException e) {
                        System.err.println(e.toString());
                        System.exit(1);
                    }
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                    System.exit(1);
                }

            }

        }

    }

    public void checkCelebrityOK()
	{
		if (numFansInLine > MAX_ALLOWED_IN_QUEUE)
		{
			System.err.println("Celebrity becomes claustrophobic and flips out");
			System.exit(1);
		}

		if (numFansInLine < MIN_FANS)
		{
			System.err.println("Celebrity becomes enraged that he was woken from nap for too few fans");
			System.exit(1);
		}
	}
  class Fan implements Runnable {
    @Override
    public void run() {
        try {
            // Random amount of time the fan will spend in the exhibit
            int exhibitTime = rndGen.nextInt(MAX_EXHIBIT_TIME);
            System.out.println(Thread.currentThread().getName() + " enters exhibit");

            // Wait for a spot in the photo line
            synchronized (monitor) {
                while (numFansInLine == MAX_ALLOWED_IN_QUEUE) {
                    monitor.wait();
                }
                line.add(this);
                numFansInLine++;
                System.out.println(Thread.currentThread().getName() + " is now in line");

                monitor.notifyAll();
            }

            // Wait for the celebrity to take a photo
            synchronized (monitor) {
                while (!line.contains(this)) {
                    monitor.wait();
                }

                while (numFansInLine < MIN_FANS) {
                    monitor.wait();
                }

                // Take photo
                monitor.notifyAll();
            }

            // Leave exhibit
            System.out.println(Thread.currentThread().getName() + " leaves exhibit after " + exhibitTime + "ms");
        } catch (InterruptedException e) {
            System.err.println(e.toString());
            System.exit(1);
        }

    }

    public String getName() {
        return Thread.currentThread().getName();
    }

}
}
