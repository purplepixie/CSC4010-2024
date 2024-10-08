#include <stdio.h>
#include <omp.h>
#include <time.h>
#include <stdlib.h>

int main()
{
    #pragma omp parallel
    {
        printf("Hello from thread %d\n",omp_get_thread_num());
       
        #pragma omp single nowait // optional nowait
        {
            printf("In single from thread %d sleeping 2s\n",omp_get_thread_num());
            nanosleep((const struct timespec[]){{2,0}},NULL);
            printf("Thread in single has finished sleeping.\n");
        }

        printf("Goodbye from thread %d\n",omp_get_thread_num());

    }

    printf("Parallel region finished.\n");
}