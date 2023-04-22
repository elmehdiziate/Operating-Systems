import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
public class AsynchIO {
    private static final Logger logger = Logger.getLogger(AsynchIO.class.getName());

    public static void main(String args[]){

        if(args.length != 1){
            System.out.println("Usage: java AsynchIO <input filename>");
            System.exit(1);
        }

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

        Path inputPath = Paths.get(args[0]);
        
        try(BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.US_ASCII)){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts.length != 4){
                    logger.severe("Parsing input file failure.");
                    System.exit(1);
                }
                int opid = Integer.parseInt(parts[0]);
                String filePath = parts[1];
                int byteOffset = Integer.parseInt(parts[2]);
                int byteCount = Integer.parseInt(parts[3]);

                if(opid < 0){
                    logger.warning("Invalid read operation: operation ID.");
                    continue;
                }

                if (byteCount < 0 || byteOffset < 0){
                    logger.warning("Invalid read operation: bad byte offset or count.");
                    continue;
                }

                try{
                    AsynchronousFileChannel afc = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
                    ByteBuffer buffer = ByteBuffer.allocate(byteCount);

                    afc.read(buffer, byteOffset, null, new CompletionHandler<Integer, Void>() {
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

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
