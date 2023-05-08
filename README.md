# Operating-Systems

## Program1

### Overall:
- The Monte Carlo method of approximating π is as follows: Given a circle centered at (0,0) with radius 1 and a square bounding the circle (with side length 2), generate random points that fall within the square.  π is approximately (4 x number of points in circle / total number of points), as the number of points approaches infinity.
### Montecarlo.C:
- This a C program that uses multiprocessing (using the system call fork()) appraoch to implement the Montecarlo method. The program split up the work of calculating the Monte Carlo approximation evenly among the processes. Eventually, we need communication between the processes and the parent process to pass the number of points falling in the circle (interprocess communication). This could have been done through message passing or shared memory. In this program an unamed pipe (message passing) is been used to allow the communication. 

### ThreadedMonteCarlo.java
- This is a java program that uses multithreading (using the Thread class in Java that is inherited by all objects by default). Since threads are sharing the same process (parent) they are by default sharing the memory, which will allow the threads to access the points list and number of points variable in an asynchronized way which causes race conditions. To solve that, a synchronized keyword was added to the block incrementing then number of points variable to allow mutual exclusion.

## Program2

### Overall
- This is a simulation of celebrity manager using Java, where fans arrive, view an exhibit, wait in line for a picture, and then leave. The celebrity, Chuck Hardabs, has specific restrictions: he naps between pictures and needs a minimum number of fans in line before waking up. If too many fans are in line, he gets claustrophobic. Fans should wait in the exhibit area if the line is full.
### ManagerSemaphore.java
- A program that uses Java Semaphores for synchronization. 
### ManagerMonitor.java
- a program that uses Java Monitors for synchronization
### Tools to check race conditions existence:
- For both programs, you can use DRD to test for race conditions. It is available at https://opensource.devexperts.com/display/DRD/Links to an external site.


## Program3

### MemoryManager.java

- A variable-partition memory management system called MemoryManager.java. 

- It takes as a command line argument an int, which is the size of your memory in (imaginary) bytes.  Users can then use the console to add items to memory with the following command: a(dd), where id is an int for the id of the item, size is an int for the size of the item, and method is one of f(irst fit), b(est fit), or w(orst fit). 
- For example, a 2 10 b adds item 2 of size 10 using best fit.   For uniformity, start your byte range at 1, not 0.
- The user can also press q(uit) or d(isplay). 
- Display output the contents of your memory in the following format:
Bytes:

                      1-3       Free

                      4-10      Item 3

                      11-12      Free

                      13-20      Item 1

                      21        Free

                      22-23      Item 4

                      24-30      Item 2

                      31-100     Free

 

- Finally, the user can f(ree) an item with the following command: f(ree). This will return a message for success if the block is freed and a message for failure if the item was not in memory. 

### VirtualMemory.c

#### Overall
- The program will take four ints as command line parameters:
      - A number of frames f
      - A range of a reference string x
      - NUM_ACCESSES- the length of the reference string
      - NUM_ITERATIONS
- The frames will be the number of frames in the virtual memory and the reference string will generate values from 1 to x to put in your frames.
- For some NUM_ACCESSES and NUM_ITERATIONS, a random int value from 1 to x is generated to be placed in the virtual memory frames. 
- this is repeated NUM_ACCESSES times. 
- FIFO is used on virtual memory of frames size f, f + 1, f + 2, and LRU is used with virtual memory of frames size f. 
- Then  the virtual memory is cleared and the same process is repeated NUM_ITERATIONS times. 
- Over the course of the iterations,  the average number of page faults os tracked for each of these four methods as well as a minimum and maximum number of page faults over all iterations for each method. 
- It may occur that for a particular iteration, having more frames actually increases the number of pages faults.  This is called Belady’s Anomaly. The number of times Belady’s Anomaly appears for frames size f + 1 and f + 2 is tracked too.  F +1 is compared to F, and F + 2 is compared to F.
- When all iterations are done,  a report the following stats using this header format:
                      number of frames, numbe of accesses, number of iterations
                      FIFO f:	Average faults	Min faults	Max faults		
                      FIFO f + 1:	Average faults	Min faults	Max faults	# of Belady	% of Belady
                      FIFO f + 2: 	Average faults	Min faults	Max faults	# of Belady	% of Belady
                      LRU:	Average faults	Min faults	Max faults	
#### Analysis
- To answer the questions regarding which algorithm gives the best performance and how FIFO compares to LRU, I runed my code using different ranges for the reference string, different numbers of page accesses, and different numbers of frames: 
    -	Low range, low accesses, low frames 
      ![image](https://user-images.githubusercontent.com/109172506/236867554-c295f64a-27a1-45e4-92bf-c371ed83ecfc.png)

    -	Low range, high accesses, low frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867594-7628d84a-d060-4816-be81-eaa870c75fbb.png)

    -	High range, low accesses, low frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867625-798cc20e-fcfd-416b-85e0-07a0810464a0.png)

    -	High range, high accesses, low frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867716-95e2d291-a3e4-4342-9374-2311cd1d172d.png)

    -	Low range, low accesses, high frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867763-4c275c05-6e18-414e-8e0c-35298510c5c1.png)

    -	Low range, high accesses, high frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867789-5ece4eff-44c3-4fc7-b0d3-cf680d7b60ea.png)

    -	High range, low accesses, high frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867823-0025b549-4f28-48d6-ae31-d2abe1a8f40b.png)

    -	High range, high accesses, high frames: 
      ![image](https://user-images.githubusercontent.com/109172506/236867858-9ae4879e-3a51-4af2-a243-92e322353bea.png)


    -	Medium accesses, high range, high number of iterations, medium frames 
      ![image](https://user-images.githubusercontent.com/109172506/236867889-10fd62df-85d5-4a43-8c39-86cf61cba1e8.png)

- In the provided examples, LRU (Least Recently Used) generally performs slightly better than or equal to FIFO (First In First Out). The primary metric used to make this determination is the average number of page faults. A lower number of page faults indicates better performance. Here's a summary of the average faults for FIFO and LRU in each scenario: 
 
      2 frames, 50 accesses, 1000 iterations: 
          FIFO: 30.54 
          LRU: 30.52 (slightly better performance) 
      2 frames, 500 accesses, 1000 iterations: 
          FIFO: 301.01 
          LRU: 300.80 (slightly better performance) 
      2 frames, 500 accesses, 1000 iterations (high range, high accesses, low frames): 
          FIFO: 480.20 
          LRU: 480.20 (equal performance) 
      2 frames, 50 accesses, 1000 iterations (low range, low accesses, high frames): 
          FIFO: 48.06 
          LRU: 48.05 (slightly better performance) 
      10 frames, 500 accesses, 1000 iterations (high range, high accesses, high frames): 
          FIFO: 5.00 
          LRU: 5.00 (equal performance) 
      10 frames, 50 accesses, 1000 iterations (low range, low accesses, medium frames): 
          FIFO: 41.18 
          LRU: 41.16 (slightly better performance) 
      10 frames, 500 accesses, 1000 iterations (high range, high accesses, medium frames): 
          FIFO: 401.43 
          LRU: 401.62 (slightly worse performance) 
         
     ![image](https://user-images.githubusercontent.com/109172506/236868464-a5f57125-3d45-4709-a884-ba1601d4ae0c.png)
      This is a small graph plotted using R. As we can see Fifo and LRU lines are Coincident Lines which show the small difference in this examples. 
- In most cases, LRU performs slightly better or equal to FIFO. LRU works better because it keeps track of the most recently used pages and prioritizes replacing the least recently used pages. This strategy generally reduces the number of page faults as it is more likely that the least recently used pages are not needed again in the near future. On the other hand, FIFO simply replaces the oldest page without considering its recent usage, which can sometimes lead to more page faults if the oldest page is still in use. 
- The performance of these algorithms can also be influenced by the nature of the reference strings. In the provided example, the reference strings are generated randomly, which may not produce more diverse or complex access patterns. In a real-world scenario, access patterns might be more varied and exhibit locality of reference, which could affect the performance of the algorithms differently. 
Locality of reference is a property of certain access patterns where memory accesses are clustered around specific locations. When there is a strong locality of reference, LRU usually performs better than FIFO because LRU can capture the working set of recently used pages more effectively. However, in the case of random reference strings, the access patterns might not show such locality, making the performance of LRU and FIFO more similar. 
- In brief, increasing the number of iterations in a simulation tends to have little direct impact on the occurrence of Belady's Anomaly or the performance of the FIFO algorithm. This is because the number of iterations mostly affects the statistical confidence of the results rather than the behavior of the algorithms themselves. 
- Increasing the number of iterations helps in obtaining a more accurate and consistent understanding of the performance of the FIFO algorithm and the occurrence of Belady's Anomaly under the given workload and access patterns. However, the actual performance of FIFO and the occurrence of Belady's Anomaly are more influenced by the number of frames, the access patterns, and the size of the working set. 
- Finally, while both algorithms have their advantages and disadvantages, LRU typically provides better performance in terms of minimizing page faults, which translates to improved average access times in memory management systems. However, it's important to note that the specific workload and access patterns can significantly impact the performance of both algorithms, and in some cases, FIFO might perform better. 

## Program 4

- We all grew up with our mother assuring us that a file is just a long sequence of contiguous bytes. We were assured this was true because the file I/O we were taught as children presented files as byte streams. Santa, the tooth fairy, Cupid, and now file streams are all exposed as frauds. Now we know the truth: files are really just blocks, and the blocks aren’t even necessarily adjacent. Innocence lost.
- An approach to I/O that more closely matches the actual system: Asynchronous I/O. In this approach, I/O operations are initiated with a request that is queued until it can be processed, at which time the data is read/written to/from a buffer, and a completion signal is sent.
- The system should take as input a series of read operations from an input file (see below) and execute the reads using Asynchronous I/O.
- Each valid read operation results in a record written to the console, giving the result of the operation (or an error log entry).
- Input file is encoded in ASCII as follows:

                <opid><sp><file path><sp><byte offset><sp><byte count>\n

- ASCII, Encoding, and Meaning

                Encoding	Meaning
                <opid>	[0-9]+	Numeric (non-negative, integer) operation ID
                <sp>	Single space	Single space
                <file path>	Any legal file path	Path to file to read
                <byte offset>	[0-9]+	Numeric (non-negative, integer) offset from file beginning to first byte to read
                <byte count>	[0-9]+	Numeric (non-negative, integer) number of bytes to read from offset
                Note: The operation IDs in a file need not be unique.

- Example Input File
The file consists only of printable characters that are encoded in ASCII.

                0 data1 0 4

                1 dir1/data2 5 2

- Console Output
                <opid><sp><char1><char 2>…<char N><eoln>
                 …

                where <char i> is the ith read byte encoded as an ASCII character and <eoln> is the default end-of-line sequence.

- Example console output
The console output consists only of only printable characters encoded in the default character encoding.

                1 45<eoln>
                0 CDEF<eoln>
                      
