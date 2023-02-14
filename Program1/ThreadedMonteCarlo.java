import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class ThreadedMonteCarlo {
    private static int NUM_THREADS; // number of threads
    private static int numPoints;  // number of points (first line of the file)
    private static float[][] points; // 2D array to store the cordinates (x,y) of the points
    private static int numInCircle = 0; // number of points that fall inside the circle
    private static final int Default_Num_Threads = 4;
    private static final String Default_fileName = "samplePoint.txt";
    public static void main(String[] args) throws IOException, InterruptedException {
        // check if the number of argumnets passed 
        String fileName = "" ;
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[1]);
            fileName = args[0];
        }
        else if (args.length == 1){
            NUM_THREADS = Default_Num_Threads;
            fileName = args[0];
        }
        else{
            NUM_THREADS = Default_Num_Threads;
            fileName = Default_fileName;
        }
        
        
        points = readFile(fileName);

        // if the file has an invalid format (number of lines not equal to number of lines)
        if(points == null){
            System.out.println("Invalid File Format");
            System.exit(0);
        }

        int pointsPerThread = numPoints / NUM_THREADS;
        int startIndex = 0;

        // Create an array of threads
        Thread[] threads = new Thread[NUM_THREADS];

        // Create and start each thread
        for (int i = 0; i < NUM_THREADS; i++) {
            int endIndex = startIndex + pointsPerThread;

            // If this is the last thread, make it process all remaining points
            if (i == NUM_THREADS - 1) {
                endIndex = numPoints;
            }

            // Create a new thread
            threads[i] = new MonteCarloThread(startIndex, endIndex);
            threads[i].start();

            // Update the start index for the next thread
            startIndex = endIndex;
        }

        // Wait for all threads to complete
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
        }

        // Calculate the estimated value of pi
        float piEstimate = 4.0f * numInCircle / numPoints;
        System.out.println("Estimated value of pi: " + piEstimate);
    }

    private static float[][] readFile(String fileName) throws IOException {

        // Create a BufferedReader to read the input file
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        numPoints = Integer.parseInt(line);
        int i=0;
        float[][] points = new float[numPoints][2];
        line = br.readLine();
        while(line != null) {
            // Split the line by spaces to get the x and y coordinates
            String[] parts = line.split(" ");

            // Store the x and y coordinates in the 2D array
            points[i][0] = Float.parseFloat(parts[0]);
            points[i][1] = Float.parseFloat(parts[1]);
            i++;
            line = br.readLine();
        }
        br.close();

        // If the number of points read from the file doesn't match the expected number, return null
        if(i != numPoints){
            return null;
        }
        
        return points;
    }

    // Nested class to represent a Monte Carlo thread
    private static class MonteCarloThread extends Thread {
        
        private int startIndex;
        private int endIndex;

        // Constructor to store the start and end index for this thread
        public MonteCarloThread(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {
            // Loop through the points assigned to this thread
            for (int i = startIndex; i < endIndex; i++) {

                // Store the x and y coordinate of the current point
                float x = points[i][0];
                float y = points[i][1];
                //System.out.println(x+y);
                // Check if the point lies within the unit circle
                if (x * x + y * y <= 1.0f) {
                    synchronized (ThreadedMonteCarlo.class) {
                        numInCircle++;
                    }
                }
            }
        }
    }
}