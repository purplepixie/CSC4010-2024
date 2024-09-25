#include <stdio.h>
#include <omp.h>

int main(void)
{
    int counter = 0;
    #pragma omp parallel
    {
        int tc = counter;
        printf("Hello from %d, my tc=%d\n",omp_get_thread_num(),tc);
        tc++;
        counter = tc;
    }

    printf("The final counter is %d\n",counter);

    

    return 0;
}