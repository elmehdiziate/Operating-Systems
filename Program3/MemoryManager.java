import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.ArrayList;

class MemoryManager {
    private static int memorySize; // The size of the memory in bytes
    private static ArrayList<Block> memoryBlocks; // An ArrayList to hold the memory blocks

    // Inner class representing a memory block
    class Block {
        private int id;  // The id of the item stored in the block (-1 means the block is free)
        private int start; // The starting address of the block in memory
        private int size; // The size of the block in bytes
        private boolean free; // True if the block is free, false otherwise

        // Constructor for a new free block
        public Block(int start, int size) {
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

        // Set the block to be free and clear its id
        public void setFree() {
            free = true;
            id = -1;
        }

        // Set the block to be busy (i.e., not free) and assign it an id
        public void setBusy(int id) {
            free = false;
            this.id = id;
        }
    }

    public MemoryManager(int size) {
        memorySize = size;
        memoryBlocks = new ArrayList<Block>();
        memoryBlocks.add(new Block(1, size)); // Initialize memory with one large free block
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
        if(args.length != 1){
            System.out.println("Usage: <file> <Memoery_size>");
            System.exit(1);
        }
        int memsize = Integer.parseInt(args[0]);

        MemoryManager M = new MemoryManager(memsize);

        M.run();
    }
    
    // add a new block to the memory with the specified item ID, size and method
    private void addBlock(int id, int size, String method) {
        int index = -1;
        int start = -1;

        // First-fit allocation strategy
        if (method.equals("f")) {
            int i = 0;
            for (Block b : memoryBlocks) {
                if (b.isFree() && b.getSize() >= size) { // If the block is free and large enough to fit the item
                    start = b.getStart();
                    index = i;
                    break;
                }
                i++;
            }
        }
        // Best-fit allocation strategy
        else if (method.equals("b")) {
            int bestfit = Integer.MAX_VALUE;
            int i = 0;
            for (Block b : memoryBlocks) {
                if (b.isFree() && b.getSize() >= size) { // If the block is free and large enough to fit the item
                    int fit = b.getSize() - size; // Calculate the amount of space left over after allocation
                    if (bestfit > fit) { // If this block has less space left over than the previous best fit
                        bestfit = fit;
                        start = b.getStart();
                        index = i;
                    }
                }
                i++;
            }
        } 
        // Worst-fit allocation strategy
        else if (method.equals("w")) {
            int worstfit = Integer.MIN_VALUE;
            int i = 0;
            for (Block b : memoryBlocks) {
                if (b.isFree() && b.getSize() >= size) { // If the block is free and large enough to fit the item
                    int fit = b.getSize() - size; // Calculate the amount of space left over after allocation
                    if (fit > worstfit) { // If this block has more space left over than the previous worst fit
                        worstfit = fit;
                        start = b.getStart();
                        index = i;
                    }
                }
                i++;
            }
        } 
        // If an invalid allocation method was specified
        else {
            System.out.println("The method selected is not available !!!!");
            return;
        }
    
        if (index != -1 && start != -1) { // If a suitable block was found
            Block selectedBlock = memoryBlocks.get(index);
            int selectedBlockSize = selectedBlock.getSize();
            Block newBlock = new Block(start, size); // Create a new block to store the allocated item
            newBlock.setBusy(id);
            memoryBlocks.remove(index); // Remove the selected block from the list
            memoryBlocks.add(index , newBlock); // Add the new block to the list in its place
            int remainingSize = selectedBlockSize - size; // Calculate the size of any remaining free space in the selected block
            if (remainingSize > 0) {
                // If there is remaining free space in the block, split it into two blocks
                Block remainingBlock = new Block(start + size, remainingSize);
                memoryBlocks.add(index+1,remainingBlock);
            }
        } else {
            System.out.println("No block available to allocate!");
        }
    }

    // Display the blocks of the memory
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
    // Free a memory block with the specified id
    private void FreeBlocks(int id) {
        boolean foundBlock = false;
        for(int i =0; i<memoryBlocks.size(); i++){
            Block block = memoryBlocks.get(i);

            if(!block.isFree() && block.getId() == id){ // If the block is not free and has the specified id
                memoryBlocks.get(i).setFree(); // Free the block and clear its id
                foundBlock = true;

                // Coalesce adjacent free blocks
                if(i>0 && memoryBlocks.get(i-1).isFree()){ // If there is a free block immediately before this one
                    Block previous = memoryBlocks.get(i-1);
                    memoryBlocks.get(i-1).setSize(previous.getSize()+block.getSize()); // Merge the two blocks
                    memoryBlocks.remove(i);
                    i--;
                    block = previous;
                }
                if(i<memoryBlocks.size() && memoryBlocks.get(i+1).isFree()){ // If there is a free block immediately after this one
                    Block next = memoryBlocks.get(i+1);
                    memoryBlocks.get(i).setSize(next.getSize()+block.getSize()); // Merge the two blocks
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