#include <stdio.h>
#include <omp.h>

int main()
{
    #pragma omp parallel
    {
        printf("Hello from thread %d\n",omp_get_thread_num());
       
        #pragma omp master
        {
            printf("Hello from thread %d\n",omp_get_thread_num());
        }

        if (omp_get_thread_num()==0)
        {
            printf("Hello from thread %d\n",omp_get_thread_num());
        }

    }
}