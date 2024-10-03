#include <stdio.h>
#include <omp.h>

int main()
{
    int i=10, j=10;

    #pragma omp parallel default(none) shared(i) firstprivate(j)
    {
        int tmpj=j;
        ++i;
        ++j;
        printf("i=%d, j=%d in thread %d\n",i,j,omp_get_thread_num());
    }

    printf("Final i=%d, j=%d\n",i,j);
}