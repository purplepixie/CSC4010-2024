#include <stdio.h>
#include <omp.h>
#include <time.h>
#include <stdlib.h>

int main()
{
    #pragma omp parallel
    {
        printf("Hello from thread %d\n",omp_get_thread_num());
       
        #pragma omp sections
        {
            #pragma omp section
            {
                printf("In first section from thread %d sleeping 2s\n",omp_get_thread_num());
                nanosleep((const struct timespec[]){{2,0}},NULL);
                printf("Thread in first section has finished sleeping.\n");
            }

            #pragma omp section
            {
                printf("In second section from thread %d\n",omp_get_thread_num());
            }
        }

        printf("Goodbye from thread %d\n",omp_get_thread_num());

    }

    printf("Parallel region finished.\n");
}