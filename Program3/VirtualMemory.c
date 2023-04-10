#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>

int *generate_reference_string(int range, int accesses)
{
    int *reference_string = malloc(sizeof(int) * accesses);

    for (int i = 0; i < accesses; i++)
    {
        reference_string[i] = (rand() % range) + 1;
    }
    return reference_string;
}

int fifo(int *reference_string, int accesses, int num_frames)
{

    // initialize memory and page table
    int *memory = (int *)calloc(num_frames, sizeof(int));
    bool *page_table = (bool *)calloc(accesses, sizeof(bool));

    // initialize variables for tracking stats
    int num_faults = 0;
    int oldest_page_index = 0;

    for (int i = 0; i < accesses; i++)
    {
        int page_number = reference_string[i];

        // check if page is already in memory
        if (!page_table[page_number])
        {
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
int lru(int *reference_string, int accesses, int num_frames)
{
    int *memory = (int *)calloc(num_frames, sizeof(int));
    int *last_used = (int *)calloc(num_frames, sizeof(int));
    // initialize variables for tracking stats
    int num_faults = 0;
    for (int j = 0; j < num_frames; j++)
    {
        memory[j] = -1;
        last_used[j] = -1;
    }

    for (int i = 0; i < accesses; i++)
    {
        int page_number = reference_string[i];
        // check if page is already in memory
        bool page_found = false;
        for (int j = 0; j < num_frames; j++)
        {
            if (memory[j] == page_number)
            {
                page_found = true;
                last_used[j] = i;
                break;
            }
        }
        if (!page_found)
        {
            int least_recent_page_index = 0;
            int least_recent_page_last_used = last_used[0];
            for (int j = 1; j < num_frames; j++)
            {
                if (last_used[j] < least_recent_page_last_used)
                {
                    least_recent_page_index = j;
                    least_recent_page_last_used = last_used[j];
                }
            }
            memory[least_recent_page_index] = page_number;
            last_used[least_recent_page_index] = i;
            num_faults++;
        }
    }
    free(memory);
    free(last_used);
    return num_faults;
}

int main(int argc, char *argv[])
{
    int f, x, NUM_ACCESSES, NUM_ITERATIONS;
    if (argc != 5)
    {
        printf("Usage: %s <num_frames> <range_of_reference_string> <num_accesses> <num_iterations>\n", argv[0]);
        return 1;
    }

    // parse command line arguments
    f = atoi(argv[1]);
    x = atoi(argv[2]);
    NUM_ACCESSES = atoi(argv[3]);
    NUM_ITERATIONS = atoi(argv[4]);
    srand(time(NULL));

    int *reference_string;
    int FIFO_total_faults = 0, FIFO_min_faults = NUM_ACCESSES, FIFO_max_faults = 0;
    int FIFO_plus_one_total_faults = 0, FIFO_plus_one_min_faults = NUM_ACCESSES, FIFO_plus_one_max_faults = 0, FIFO_plus_one_belady_count = 0;
    int FIFO_plus_two_total_faults = 0, FIFO_plus_two_min_faults = NUM_ACCESSES, FIFO_plus_two_max_faults = 0, FIFO_plus_two_belady_count = 0;
    int LRU_total_faults = 0, LRU_min_faults = NUM_ACCESSES, LRU_max_faults = 0;
    // generate a random reference string
    for (int i = 0; i < NUM_ITERATIONS; i++)
    {
        reference_string = generate_reference_string(x, NUM_ACCESSES);
        
        // run fifo for number of frames
        int fifo_faults = fifo(reference_string, NUM_ACCESSES, f);
        FIFO_total_faults += fifo_faults;
        if (FIFO_min_faults > fifo_faults)
        {
            FIFO_min_faults = fifo_faults;
        }
        if (FIFO_max_faults < fifo_faults)
        {
            FIFO_max_faults = fifo_faults;
        }

        // run fifo for number of frames + 1
        int fifo_faults_1 = fifo(reference_string, NUM_ACCESSES, f + 1);
        FIFO_plus_one_total_faults += fifo_faults_1;
        if (FIFO_plus_one_min_faults > fifo_faults_1)
        {
            FIFO_plus_one_min_faults = fifo_faults_1;
        }
        if (FIFO_plus_one_max_faults < fifo_faults_1)
        {
            FIFO_plus_one_min_faults = fifo_faults_1;
        }

        // check for Belady's Anomaly
        if (fifo_faults < fifo_faults_1)
        {
            FIFO_plus_one_belady_count++;
        }

        // run fifo for number of frames + 2
        int fifo_faults_2 = fifo(reference_string, NUM_ACCESSES, f + 2);
        FIFO_plus_two_total_faults += fifo_faults_2;
        if (FIFO_plus_two_min_faults > fifo_faults_2)
        {
            FIFO_plus_two_min_faults = fifo_faults_2;
        }
        if (FIFO_plus_two_max_faults < fifo_faults_1)
        {
            FIFO_plus_two_max_faults = fifo_faults_2;
        }

        // check for Belady's Anomaly
        if (fifo_faults < fifo_faults_2)
        {
            FIFO_plus_two_belady_count++;
        }

        // run lru for number of frames
        int LRU_faults = lru(reference_string, NUM_ACCESSES, f);
        LRU_total_faults += LRU_faults;
        if (LRU_min_faults > LRU_faults)
        {
            LRU_min_faults = LRU_faults;
        }
        if (LRU_max_faults < LRU_faults)
        {
            LRU_max_faults = LRU_faults;
        }
    }
    // Calculate averages and percentages
    double FIFO_avg_faults = (double)FIFO_total_faults / NUM_ITERATIONS;
    double FIFO_plus_one_avg_faults = (double)FIFO_plus_one_total_faults / NUM_ITERATIONS;
    double FIFO_plus_two_avg_faults = (double)FIFO_plus_two_total_faults / NUM_ITERATIONS;
    double LRU_avg_faults = (double)LRU_total_faults / NUM_ITERATIONS;

    double FIFO_plus_one_belady_percentage = (double)FIFO_plus_one_belady_count / NUM_ITERATIONS * 100;
    double FIFO_plus_two_belady_percentage = (double)FIFO_plus_two_belady_count / NUM_ITERATIONS * 100;

    // Print the stats
    printf("%d frames, %d of accesses, %d of iterations\n",f,NUM_ACCESSES,NUM_ITERATIONS);
    printf("FIFO :\tAverage faults\tMin faults\tMax faults\n", f);
    printf("\t\t%.2f\t\t%d\t\t%d\n", FIFO_avg_faults, FIFO_min_faults, FIFO_max_faults);

    printf("FIFO + 1:\tAverage faults\tMin faults\tMax faults\t# of Belady\t%% of Belady\n");
    printf("\t\t%.2f\t\t%d\t\t%d\t\t%d\t\t%.5f\n", FIFO_plus_one_avg_faults, FIFO_plus_one_min_faults, FIFO_plus_one_max_faults, FIFO_plus_one_belady_count, FIFO_plus_one_belady_percentage);

    printf("FIFO + 2:\tAverage faults\tMin faults\tMax faults\t# of Belady\t%% of Belady\n", f);
    printf("\t\t%.2f\t\t%d\t\t%d\t\t%d\t\t%.5f\n", FIFO_plus_two_avg_faults, FIFO_plus_two_min_faults, FIFO_plus_two_max_faults, FIFO_plus_two_belady_count, FIFO_plus_two_belady_percentage);

    printf("LRU:\t\tAverage faults\tMin faults\tMax faults\n");
    printf("\t\t%.2f\t\t%d\t\t%d\n", LRU_avg_faults, LRU_min_faults, LRU_max_faults);
}