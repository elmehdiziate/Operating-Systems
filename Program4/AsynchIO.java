// Import necessary libraries
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
public class AsynchIO {
    // Set up a logger for logging events and errors
    private static final Logger logger = Logger.getLogger(AsynchIO.class.getName());

    public static void main(String args[]){
        // Check the command line arguments and show usage message if incorrect
        if(args.length != 1){
            System.out.println("Usage: java AsynchIO <input filename>");
            System.exit(1);
        }

        // Set up a file handler for logging events and errors
        FileHandler Fh;
        try{
            Fh = new FileHandler("aio.log");
            logger.addHandler(Fh);
            SimpleFormatter formatter = new SimpleFormatter();
            Fh.setFormatter(formatter);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        // Read the input file and parse its content
        Path inputPath = Paths.get(args[0]);
        
        try(BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.US_ASCII)){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts.length != 4){
                    logger.severe("Parsing input file failure.");
                    System.exit(1);
                }
                // Extract operation parameters from the input file
                int opid = Integer.parseInt(parts[0]);
                String filePath = parts[1];
                int byteOffset = Integer.parseInt(parts[2]);
                int byteCount = Integer.parseInt(parts[3]);
                // Validate the operation parameters
                if(opid < 0){
                    logger.warning("Invalid read operation: operation ID.");
                    continue;
                }
                
                if (byteCount < 0 || byteOffset < 0){
                    logger.warning("Invalid read operation: bad byte offset or count.");
                    continue;
                }
                // Perform the asynchronous read operation
                try{
                    AsynchronousFileChannel afc = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
                    ByteBuffer buffer = ByteBuffer.allocate(byteCount);

                    afc.read(buffer, byteOffset, null, new CompletionHandler<Integer, Void>() {
                        // Handle the completion of the read operation
                        @Override
                        public void completed(Integer bytesRead, Void attachment) {
                            if (bytesRead < 0) {
                                logger.warning("Specified portion of the file does not exist.");
                                return;
                            }
                            buffer.flip();
                            String data = StandardCharsets.US_ASCII.decode(buffer).toString();
                            System.out.println(opid + " " + data);
                            try {
                                afc.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // Handle the failure of the read operation
                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            logger.severe("Asynchronous read failed: " + exc.getMessage());
                            try {
                                afc.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch(IOException e){
                    System.out.println(inputPath + "\tdoes not exist");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        // Give some time for the asynchronous operations to complete before exiting the program
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
