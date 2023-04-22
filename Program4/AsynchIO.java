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
                System.out.println(line);
            }
        }catch(IOException e){

        }

    }
}
