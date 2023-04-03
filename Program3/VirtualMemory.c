#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>

int * generate_reference_string(int range, int accesses){
    int * reference_string =  malloc(sizeof(int) * accesses);
    srand(time(NULL));

    for(int i=0; i< accesses; i++){
        reference_string[i] = (rand() % range) + 1;
    }
    return reference_string;
}

int fifo(int* reference_string, int accesses, int num_frames){

    // initialize memory and page table
    int *memory = (int *) calloc(num_frames, sizeof(int));
    bool *page_table = (bool *) calloc(accesses, sizeof(bool));

    // initialize variables for tracking stats
    int num_faults = 0;
    int oldest_page_index = 0;

    for(int i=0; i<accesses ; i++){
        int page_number = reference_string[i];

        // check if page is already in memory
        if(!page_table[page_number]){
            // page fault, replace oldest page
            int oldest_page_number = memory[oldest_page_index];
            page_table[oldest_page_number] = false;
            memory[oldest_page_index] = page_number;
            page_table[page_number] = true;
            num_faults++;
            // update oldest page index
            oldest_page_index = (oldest_page_index + 1) % num_frames;
        }
        

    }
    // free memory and page table
    free(memory);
    free(page_table);
    return num_faults;
}

int main(int argc, char *argv[]){
    int f, x, NUM_ACCESSES, NUM_ITERATIONS;
    if(argc != 5){
        printf("Usage: %s <num_frames> <range_of_reference_string> <num_accesses> <num_iterations>\n", argv[0]);
        return 1;
    }
    
    // parse command line arguments
    f = atoi(argv[1]);
    x = atoi(argv[2]);
    NUM_ACCESSES = atoi(argv[3]);
    NUM_ITERATIONS = atoi(argv[4]);

    int* reference_string;
    int FIFO_total_faults = 0, FIFO_min_faults = 0, FIFO_max_faults = 0;
    int FIFO_plus_one_total_faults = 0, FIFO_plus_one_min_faults = 0, FIFO_plus_one_max_faults = 0, FIFO_plus_one_belady_count = 0;
    int FIFO_plus_two_total_faults = 0, FIFO_plus_two_min_faults = 0, FIFO_plus_two_max_faults = 0, FIFO_plus_two_belady_count = 0;
    // generate a random reference string
    for(int i=0; i<NUM_ITERATIONS; i++ ){
        reference_string = generate_reference_string(x, NUM_ACCESSES);
        for(int i=0; i<NUM_ACCESSES; i++){
            printf("%d-%d\t",i+1,reference_string[i]);
        }
        printf("\n");
        int fifo_faults = fifo(reference_string, NUM_ACCESSES, f);
        printf("%d",fifo_faults);
        
        FIFO_total_faults += fifo_faults;
        printf("\n");
        printf("%d",FIFO_total_faults);
    }
    

}