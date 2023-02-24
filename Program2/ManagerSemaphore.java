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

    // Semaphore to signal when the line is empty (i.e., less than MIN_FANS)
    private static Semaphore lineIsEmpty = new Semaphore(0);

    // Semaphore to signal when the line is full (i.e., MAX_ALLOWED_IN_QUEUE or more)
    private static Semaphore lineIsFull = new Semaphore(MAX_ALLOWED_IN_QUEUE);

    // Mutex semaphore for accessing shared variables
    private static Semaphore mutex = new Semaphore(1);



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

	class Celebrity implements Runnable
	{
		@Override
		public void run() {
			while (true)
			{
                try {
                    lineIsEmpty.acquire(MIN_FANS);
                }catch(InterruptedException e){
                    System.exit(1);
                }
				// Check to see if celebrity flips out
				checkCelebrityOK();

				// Take picture with fans

				System.out.println("Celebrity takes a picture with fans");

				// Remove the fans from the line
				try {
                    mutex.acquire();
                    for (int i = 0; i < MIN_FANS; i++) {
                        Fan fan = line.remove(0);
                        System.out.println(fan.getName() + ": OMG! Thank you!");
                    }
                    numFansInLine -= MIN_FANS; 
                    mutex.release();
                    lineIsFull.release(MIN_FANS);
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                    System.exit(1);
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
            System.out.println(Thread.currentThread() + ": gets in line");
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                System.exit(1);
            }
    
            numFansInLine++;
            line.add(this);
            mutex.release();
    
            // Wait in line until there are enough fans for a photo
            if (numFansInLine >= MIN_FANS) {
                lineIsEmpty.release(MIN_FANS);
            } else {
                System.out.println(name + " waits in line for more fans to arrive");
            }
    
            // Wait until there's room in the line if necessary
            try {
                lineIsFull.acquire();
            } catch (InterruptedException e) {
                System.exit(1);
            }
    
        }
    }
    
    
}
