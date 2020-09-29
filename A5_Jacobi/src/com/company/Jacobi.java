package com.company;

import java.io.*;
import java.util.*;
 
public class Jacobi {

    public long processTime;

    public Jacobi(int mSize, int tNumber) throws IOException, InterruptedException {

        int matrixSize = mSize;   //matrix size
        int numThreads = tNumber;      //Thread number
        double epsilon = 0.0001;

        double[][] Matrix = new double[matrixSize][matrixSize];
        double[][] MatrixCopy;
        //create a matrix
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                Matrix[i][j] = 1+Math.random()*100;
            }
        }
        MatrixCopy = Matrix;

        double[] maxDiff = new double[numThreads];
        int threadID = 1;
        double realMaxDiff = 0.001;

        Barrier barrier = new Barrier(numThreads);

        for(double max : maxDiff){
            max = 0.0;
        }

        ArrayList<Thread> ThreadsArray = new ArrayList<Thread>(numThreads);
        long start = System.nanoTime();//start jacobi function, start timing
        for (int x = 0; x < numThreads; x++) {
            Thread thread = new Thread(new JacobiFunction(matrixSize, Matrix, MatrixCopy, numThreads, maxDiff, epsilon, barrier, threadID, realMaxDiff));
            thread.start();
            ThreadsArray.add(thread);
            threadID++;
        }
        for (Thread thread : ThreadsArray) {
            thread.join();
        }
        long end = System.nanoTime();//all thread finished

        processTime = end - start;

        //print the information
        System.out.printf("Completed Successfully!\n");
        System.out.printf("Matrix size = %d\n", matrixSize);
        System.out.printf("Thread num = %d\n", numThreads);
        System.out.printf("time = %d\n", processTime);
    }
}

//Jacobi iteration
class JacobiFunction implements Runnable {
    private double matrix[][];
    private double Copy[][];
    private int numThreads;
    private double[] maxDiff;
    private double realMaxDiff;
    private double epsilon;
    private Barrier barrier = null;
    private int threadID;
    private int firstRow;
    private int lastRow;
    private int matrixSize;

    public JacobiFunction(int matrixSize, double[][] matrix, double[][] Copy, int numThreads, double[] maxDiff, double epsilon, Barrier barrier, int threadID, double realMaxDiff) {
        this.matrix = matrix;
        this.numThreads= numThreads;
        this.maxDiff = maxDiff;
        this.epsilon = epsilon;
        this.barrier = barrier;
        this.threadID = threadID;
        this.matrixSize = matrixSize;
        this.realMaxDiff = realMaxDiff;
        this.Copy = Copy;
    }
    
    @Override
    public void run(){
        try {
            firstRow = ((threadID - 1)*(matrixSize-2)/numThreads) + 1;
            lastRow = (threadID*(matrixSize-2))/numThreads;
            while(realMaxDiff > epsilon){
                //compute new values for my strip
                for(int i = firstRow; i <= lastRow; i++){
                    for(int j = 1; j <= matrixSize-2; j++){
                        Copy[i][j] = (matrix[i-1][j]+matrix[i+1][j]+matrix[i][j-1]+matrix[i][j+1])*0.25;
                    }
                }
                this.barrier.await();
                //recompute new values for my strip
                for(int i = firstRow; i <= lastRow; i++){
                    for(int j = 1; j <= matrixSize-2; j++){
                        matrix[i][j] = (Copy[i-1][j]+ Copy[i+1][j]+ Copy[i][j-1]+ Copy[i][j+1])*0.25;
                    }
                }
                this.barrier.await();

                //compute max difference for my strip
                realMaxDiff = 0.0;
                maxDiff[threadID-1] = 0.0;
                for(int i = firstRow; i <= lastRow; i++){
                    for(int j = 1; j <= matrixSize-2; j++){
                        maxDiff[threadID-1] = Math.max(maxDiff[threadID-1], Math.abs(Copy[i][j] - matrix[i][j]));
                    }
                }
                this.barrier.await();
                for(double max : maxDiff){
                    realMaxDiff = Math.max(realMaxDiff, max);
                }
            }
        }catch (InterruptedException ex){ 
            return; 
        }
    }
}

class Barrier {
    private int numThreads;
    private int awaitingThreads;

    public Barrier(int numThreads) {
        this.numThreads = numThreads;
        this.awaitingThreads = numThreads;
    }
    
    public synchronized void await() throws InterruptedException{
        awaitingThreads--;
        //wait until no thread awaiting
        if(awaitingThreads > 0){
            this.wait();
        }else{
            awaitingThreads = numThreads;
            notifyAll();
        }
    }
}