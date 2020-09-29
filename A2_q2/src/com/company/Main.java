package com.company;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Main {

    public static int NumData = 10;//total number of data for each producers
    public static int NumSlot = 5;//total number of buffer slots
    public static int NumUser = 3;//total number of consumers
    public static int NumProd = 3;//total number of producers

    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        Producer[] producer = new Producer[NumProd];//initialize producers
        Consumer[] consumer = new Consumer[NumUser];//initialize consumers

        for (int i = 0; i < NumProd; i++) {
            producer[i] = new Producer(i, buffer);//initialize each consumer
            producer[i].start();//start fetch
        }

        for (int i = 0; i < NumUser; i++) {
            consumer[i] = new Consumer(i, buffer);//initialize each consumer
            consumer[i].start();//start fetch
        }
    }
}

class Buffer//assign the process for fetch and deposit
{
    private int[] Slot;//slot array to store data from the producer
    private Semaphore writable;//whether each slot can be deposited
    private Semaphore readable;//whether each slot can be fetched
    private Vector<Integer> writeQueue;//the slot id that needs to be deposited
    private Vector<Integer> readQueue;//the slot id that needs to be fetched

    public Buffer()
    {
        Slot = new int[Main.NumSlot];//set slot array number as NumSlot

        writable = new Semaphore(Main.NumSlot);//writability for each slot, from the start every slot can be deposited
        readable = new Semaphore(0);//readability for each slot, from the start no permits to read

        readQueue = new Vector<Integer>();//from the start no slot waits for reading
        writeQueue = new Vector<Integer>();
        for (int i = 0; i < Main.NumSlot; i++) {
            writeQueue.addElement(i);//from the start every slot needs to be deposited
        }

    }

    public void deposit(int Prod, int data) throws InterruptedException {
        writable.acquire();//wait to write

        int DepositSlot = writeQueue.get(0);//get the first in the queue
        writeQueue.removeElementAt(0);//delete the first

        Slot[DepositSlot] = data;//write
        System.out.println("Producer " + Prod + " deposits " + data + " in slot " + DepositSlot);

        readQueue.addElement(DepositSlot);//add this slot to the reading queue
        readable.release();

    }

    public void fetch(int User) throws InterruptedException
    {
        readable.acquire();//wait to read

        int FetchSlot = readQueue.get(0);//get the first in the queue
        readQueue.removeElementAt(0);//delete the first

        int ReadData = Slot[FetchSlot];//read
        System.out.println("User " + User + " fetches " + ReadData + " from slot " + FetchSlot);

        writeQueue.addElement(FetchSlot);//add this slot to the writing queue
        writable.release();

    }

}

class Producer implements Runnable
{
    private Thread t;
    private int Prod;
    private Buffer buffer;

    Producer(int id, Buffer buf)
    {
        Prod = id;
        buffer = buf;
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.NumData; i++) {
            try {
                buffer.deposit(Prod, (i+1)*(Prod+1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start()
    {
        if (t == null)
        {
            t = new Thread(this);
            t.start();
        }
    }
}

class Consumer implements Runnable
{
    private Thread t;
    private int User;
    private Buffer buffer;

    Consumer(int id, Buffer buf)
    {
        User = id;
        buffer = buf;
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.NumData*Main.NumProd/Main.NumUser; i++) {
            try {
                buffer.fetch(User);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start()
    {
        if (t == null)
        {
            t = new Thread(this);
            t.start();
        }
    }
}