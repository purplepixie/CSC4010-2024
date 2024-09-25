#include <stdio.h>
#include <omp.h>

int main(void)
{
    #pragma omp parallel
    {
        printf("Hello\n");
        #pragma omp parallel
        printf("World\n");
    }

    #pragma omp parallel
    printf("Goodbye World\n");

    return 0;
}