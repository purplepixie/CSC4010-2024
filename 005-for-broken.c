#include <stdio.h>
#include <omp.h>

int main(void)
{
    
    #pragma omp parallel
    {
        for(int i=0; i<20; ++i)
        {
            printf("i=%d in thread %d\n",i,omp_get_thread_num());
        }
    }

    

    return 0;
}