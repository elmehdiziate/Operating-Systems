# Operating-Systems

## Program1

### Overall:
- The Monte Carlo method of approximating π is as follows: Given a circle centered at (0,0) with radius 1 and a square bounding the circle (with side length 2), generate random points that fall within the square.  π is approximately (4 x number of points in circle / total number of points), as the number of points approaches infinity.
### Montecarlo.C:
- This a C program that usse multiprocessing (using the system call fork()) appraoch to implement the Montecarlo method. The program split up the work of calculating the Monte Carlo approximation evenly among the processes. Eventually, we need communication between the processes and the parent process to pass the number of points falling in the circle (interprocess communication). This could have been done through message passing or shared memory. In this program an unamed pipe (message passing) is been used to allow the communication. 

### ThreadedMonteCarlo.java
- This is a java program that uses multithreading (using the Thread class in Java that is inherited by all objects by default). Since threads are sharing the same process (parent) they are by default sharing the memory, which will allow the threads to access the points list and number of points variable in an asynchronized way which causes race conditions. To solve that, a synchronized keyword was added to the block incrementing then number of points variable to allow mutual exclusion.
