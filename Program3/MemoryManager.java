
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.ArrayList;

class MemoryManager{
    private static int memorySize;
    private static ArrayList<Block> memoryBlocks;

    class Block{
        private int id;
        private int start;
        private int size;
        private boolean free;

        public Block(int start,int size){
            // Assumption ID= -1 means that the block is free
            this.id = -1;
            this.start = start; 
            this.size = size;
            this.free = true; 
        }

        public int getId() {
            return id;
        }
    
        public void setId(int id) {
            this.id = id;
        }
    
        public int getStart() {
            return start;
        }
    
        public void setStart(int start) {
            this.start = start;
        }
    
        public int getSize() {
            return size;
        }
    
        public void setSize(int size) {
            this.size = size;
        }
    
        public boolean isFree() {
            return free;
        }
    
        public void setFree() {
            free = true;
            id = -1;
        }
    
        public void setBusy(int id) {
            free = false;
            this.id = id;
        }
    }

    public MemoryManager(int size) {
        memorySize = size;
        memoryBlocks = new ArrayList<Block>();
        memoryBlocks.add(new Block(1 , size));
    }

    public void run(){
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Enter command (a/dd, d/isplay, f/ree, q/uit):");
            String command = sc.next();

            if(command.equals("a") || command.equals("dd")){
                int id = sc.nextInt();
                int size = sc.nextInt();
                String Method = sc.next();
                System.out.println(Method + size + id);
                addBlock(id, size, Method);
            }
            else if(command.equals("d") || command.equals("isplay")){
                displayBlocks();
            }
            else if(command.equals("f") || command.equals("ree")){
                int id = sc.nextInt();
                FreeBlocks(id);
            }
            else if(command.equals("q") || command.equals("uit")){
                sc.close();
                return;
            }
            else{
                System.out.println("The command entered is not available !!!");
            }
        }
    }

    public static void main(String args[]){
        int memsize = Integer.parseInt(args[0]);

        MemoryManager M = new MemoryManager(memsize);

        M.run();
    }

    private void addBlock(int id, int size, String Method){
        int index = -1;
        int start = -1;

        if(Method.equals("f")){
            int i = 0;
            for(Block b : memoryBlocks){
                if(b.isFree() && b.getSize() > size){
                    start = b.getStart();
                    index = i;
                    break;
                }
                i++;
            }
        }
        else if(Method.equals("b")){
            int bestfit = Integer.MAX_VALUE;
            int i = 0;
            for(Block b : memoryBlocks){
                if(b.isFree() && b.getSize() > size){
                    int fit = b.getSize() - size;
                    if(bestfit > fit){
                        bestfit = fit;
                        index = i;
                    }
                }
                i++;
            }
        }
        else if (Method.equals("w")){
            int worstfit = Integer.MAX_VALUE;
            int i = 0;
            for(Block b : memoryBlocks){
                if(b.isFree() && b.getSize()> size){
                    int fit = b.getSize() - size;
                    if(fit > worstfit){
                        worstfit = fit;
                        index = i;
                    }
                }
                i++;
            }
        }

        if(index != -1){
            memoryBlocks.add(index , new Block(start, size));
            memoryBlocks.get(index).setId(id);
        }
    }


    private void displayBlocks(){

    }

    private void FreeBlocks(int id){

    }
}