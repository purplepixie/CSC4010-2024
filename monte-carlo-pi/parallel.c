//
// Calculate Pi Estimate using the Monte Carlo Method 
// David Cutting, d.cutting@qub.ac.uk, http://davecutting.uk
// 
// Parallel Execution
//
// Note: a lot of commented out printfs are debug outputs, uncomment
// to help see the program in operation.
//
// Monte carlo Pi visualisation: https://academo.org/demos/estimating-pi-monte-carlo/

#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <omp.h>

int main(int ac, char **av)
{
    long executions = 10000;        // number of executions
    long square=100000;             // width/height of square used
    long radius=square/2;           // calculate radius of circle

    // if a parameter is provided then use that as number of executions
    if (ac>1)
        executions = atol(av[1]);

    long incount = 0;               // how many are inside the circle
    srand(time(NULL));              // seed the randomiser

    #pragma omp parallel shared(executions,radius,square) reduction(+:incount)
    {
        unsigned seed = 345834 + (rand()%100)*omp_get_thread_num();
        #pragma omp for 
        for(long i=0; i<executions; ++i) // loop through each execution
        {
            //printf("%li ",i);            // output iteration count

            // generate random x and y positions in the square
            double x = (double)(rand_r(&seed) % square) - radius;
            double y = (double)(rand_r(&seed) % square) - radius;

            // use Pythagoras to calculate the hypotenuse
            double h = sqrt((x*x)+(y*y));

            // inside indicator for this loop, defaults to 0
            int inside = 0;
            // set to 1 if hypotenuse is <= diameter of circle
            if (h <= radius) inside = 1;

            // if it's inside the circle increment the inside counter
            if (inside>0)
            {
                incount++;
            }

            // print some data to the screen
            //printf("x: %.0f y: %.0f h: %.2f in: %d\n",x,y,h,inside);
        }


    } // end of parallel region

    // estimate Pi 
    double pi = (4 * (float)incount)/(float)executions;
    // output Pi for this run
    printf("END: %li / %li = %.10f\n",incount,executions,pi);
    // success and exit (0)
    return 0;
}