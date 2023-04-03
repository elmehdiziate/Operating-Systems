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

    // generate a random reference string
    int* reference_string = generate_reference_string(x, NUM_ACCESSES);

    for(int i=0; i<NUM_ACCESSES; i++){
        printf("%d",reference_string[i]);
    }
}