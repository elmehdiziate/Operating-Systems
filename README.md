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
