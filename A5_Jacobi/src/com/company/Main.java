package com.company;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException, InterruptedException {

        //parameters, set according to the test needs
        long average = 0; //average processing time, units: ns
        int test = 1000; // number of test
        int matrix_size = 512;// matrix size
        int thread_num = 3;// thread number

        for (int i = 0; i < test; i++) {//run jacobi for number of times
            Jacobi j = new Jacobi(matrix_size,thread_num);
            average += j.processTime;//accumulate the total processing time
        }
        average = average/test;//get the average processing time of each jacobi, units: ns

        System.out.printf("Average time for %d matrix, %d threads\n ",matrix_size,thread_num);
        System.out.printf("after %d test = %d\n", test, average);

    }
}


