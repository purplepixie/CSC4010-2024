#include <stdio.h>
#include <omp.h>
#include <time.h>
#include <stdlib.h>

int main()
{
    srand(time(NULL));

    #pragma omp parallel
    {
        int sleep = rand() % 5;
        printf("Hello from thread %d, I will sleep for %d seconds\n",
            omp_get_thread_num(),sleep);
        
        nanosleep((const struct timespec[]){{sleep,0}},NULL);
        #pragma omp barrier
        printf("Thread %d at POINT X\n",omp_get_thread_num());
    }

    printf("Parallel region finished\n");

    return 0;
}