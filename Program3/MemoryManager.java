package Program3;

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
            // Assumption that ID= -1 means that the block is free
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

}