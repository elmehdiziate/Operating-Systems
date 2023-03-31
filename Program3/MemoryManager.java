
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.ArrayList;

class MemoryManager {
    private static int memorySize;
    private static ArrayList<Block> memoryBlocks;

    class Block {
        private int id;
        private int start;
        private int size;
        private boolean free;

        public Block(int start, int size) {
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
        memoryBlocks.add(new Block(1, size));
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter command (a/dd, d/isplay, f/ree, q/uit):");
            String command = sc.next();

            if (command.equals("a") || command.equals("dd")) {
                int id = sc.nextInt();
                int size = sc.nextInt();
                String Method = sc.next();
                System.out.println(Method + size + id);
                addBlock(id, size, Method);
            } else if (command.equals("d") || command.equals("isplay")) {
                displayBlocks();
            } else if (command.equals("f") || command.equals("ree")) {
                int id = sc.nextInt();
                FreeBlocks(id);
            } else if (command.equals("q") || command.equals("uit")) {
                sc.close();
                return;
            } else {
                System.out.println("The command entered is not available !!!");
            }
        }
    }

    public static void main(String args[]) {
        int memsize = Integer.parseInt(args[0]);

        MemoryManager M = new MemoryManager(memsize);

        M.run();
    }

    private void addBlock(int id, int size, String method) {
        int index = -1;
        int start = -1;
    
        if (method.equals("f")) {
            int i = 0;
            for (Block b : memoryBlocks) {
                if (b.isFree() && b.getSize() >= size) {
                    start = b.getStart();
                    index = i;
                    break;
                }
                i++;
            }
        } else if (method.equals("b")) {
            int bestfit = Integer.MAX_VALUE;
            int i = 0;
            for (Block b : memoryBlocks) {
                if (b.isFree() && b.getSize() >= size) {
                    int fit = b.getSize() - size;
                    if (bestfit > fit) {
                        bestfit = fit;
                        start = b.getStart();
                        index = i;
                    }
                }
                i++;
            }
        } else if (method.equals("w")) {
            int worstfit = Integer.MIN_VALUE;
            int i = 0;
            for (Block b : memoryBlocks) {
                if (b.isFree() && b.getSize() >= size) {
                    int fit = b.getSize() - size;
                    if (fit > worstfit) {
                        worstfit = fit;
                        start = b.getStart();
                        index = i;
                    }
                }
                i++;
            }
        } else {
            System.out.println("The method selected is not available !!!!");
            return;
        }
    
        if (index != -1 && start != -1) {
            Block selectedBlock = memoryBlocks.get(index);
            int selectedBlockSize = selectedBlock.getSize();
            Block newBlock = new Block(start, size);
            newBlock.setBusy(id);
            memoryBlocks.remove(index);
            memoryBlocks.add(index , newBlock);
            int remainingSize = selectedBlockSize - size;
            if (remainingSize > 0) {
                // Split the block into two blocks
                Block remainingBlock = new Block(start + size, remainingSize);
                memoryBlocks.add(index+1,remainingBlock);
            }
        } else {
            System.out.println("No block available to allocate!");
        }
    }
    

    private void displayBlocks() {
        for (Block block : memoryBlocks) {
            if (block.isFree()) {
                System.out.println(block.getStart() + "-" + (block.getStart() + block.getSize() - 1) + "\tFree");
            } else {
                System.out.println(
                        block.getStart() + "-" + (block.getStart() + block.getSize() - 1) + "\tItem " + block.getId());
            }
        }
    }
    private void FreeBlocks(int id) {
        boolean foundBlock = false;
        for(int i =0; i<memoryBlocks.size(); i++){
            Block block = memoryBlocks.get(i);
            if(!block.isFree() && block.getId() == id){
                memoryBlocks.get(i).setFree();
                foundBlock = true;

                // Coalesce adjacent free blocks

                if(i>0 && memoryBlocks.get(i-1).isFree()){
                    Block previous = memoryBlocks.get(i-1);
                    memoryBlocks.get(i-1).setSize(previous.getSize()+block.getSize());
                    memoryBlocks.remove(i);
                    i--;
                    block = previous;
                }
                if(i<memoryBlocks.size() && memoryBlocks.get(i+1).isFree()){
                    Block next = memoryBlocks.get(i+1);
                    memoryBlocks.get(i).setSize(next.getSize()+block.getSize());
                    memoryBlocks.remove(i+1);
                    i++;
                }
                System.out.println("Item " + id + " has been successfully freed.");
                break;
            }
        }
        if (!foundBlock) {
            System.out.println("Item " + id + " is not in memory. Failed to free the block.");
        }
    }
}