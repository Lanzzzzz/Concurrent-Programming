package com.company;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Main {

    public static int NumData = 50;//total number of data
    public static int NumSlot = 5;//total number of buffer slots
    public static int NumUser = 10;//total number of consumers

    public static void main(String[] args) {
	    Buffer buffer = new Buffer();
	    Producer producer = new Producer(buffer);//initialize 1 producer
	    Consumer[] consumer = new Consumer[NumUser];//initialize consumers

	    producer.start();//start deposit
        for (int i = 0; i < NumUser; i++) {
            consumer[i] = new Consumer(i, buffer);//initialize each consumer
            consumer[i].start();//start fetch
        }

    }
}

class Buffer//assign the process for fetch and deposit
{
    private int[] Slot;//slot array to store data from the producer
    private int[] NumRemaining;//the remaining number of readers needed to fetch data for each slot
    private Semaphore[] writable;//whether each slot can be deposited
    private Semaphore[] readable;//whether each slot can be fetch
    private Vector<Semaphore[]> user_readable;//every user has its own writability for each slot
    private int DepositSlot;//the aim slot for the producer
    private int[] FetchSlot;//the aim slot for each consumer

    public Buffer()
    {
        Slot = new int[Main.NumSlot];//set slot array number as NumSlot
        NumRemaining = new int[Main.NumSlot];//remaining number for each slot
        FetchSlot = new int[Main.NumUser];//set consumer's aim slot starting from slot 0
        DepositSlot = 0;//deposit start from slot 0

        writable = new Semaphore[Main.NumSlot];//writability for each slot
        readable = new Semaphore[Main.NumSlot];//readability for each slot

        for (int i = 0; i < Main.NumSlot; i++) {
            readable[i] = new Semaphore(0);//from the start no permits to read
            writable[i] = new Semaphore(1);//from the start every slot can be deposited
            NumRemaining[i] = Main.NumUser;//from the start, the remaining number is same as the user number
        }

        user_readable = new Vector<Semaphore[]>(Main.NumUser);
        for (int i = 0; i < Main.NumUser; i++) {
            user_readable.add(readable);//separately store the writability of each slot for each consumer
        }
    }

    public void deposit(int data) throws InterruptedException {
        writable[DepositSlot].acquire();//wait to write

        Slot[DepositSlot] = data;//write
        System.out.println(data + " deposits in slot " + DepositSlot);

        for (int i = 0; i < Main.NumUser; i++) {
            user_readable.get(i)[DepositSlot].release();
            //after write this slot, set this slot as readable for every consumer
        }

        DepositSlot++;//ready to deposit the next slot
        if (DepositSlot >= Main.NumSlot)
        {
            DepositSlot = 0;//back to the first slot
        }


    }

    public void fetch(int User) throws InterruptedException
    {
        user_readable.get(User)[FetchSlot[User]].acquire();//wait to read

        int ReadData = Slot[FetchSlot[User]];//read
        System.out.println("User " + User + " reads " + ReadData + " from slot " + FetchSlot[User]);

        FetchSlot[User]++;//ready to fetch the next slot
        if(FetchSlot[User] >= Main.NumSlot)
        {
            FetchSlot[User]=0;//back to the first slot
        }
        NumRemaining[FetchSlot[User]]--;//finishing one reading, minus the remaining number
        if (NumRemaining[FetchSlot[User]] == 0)
        {
            writable[FetchSlot[User]].release();//all consumers have read, ready to be deposit
            NumRemaining[FetchSlot[User]] = Main.NumUser;//reset the remaining number
            System.out.println("data " + Slot[FetchSlot[User]] + " in Slot " + FetchSlot[User]  + " finished fetching!");
        }

    }

}

class Producer implements Runnable
{
    private Thread t;
    private Buffer buffer;

    Producer(Buffer buf)
    {
        buffer = buf;
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.NumData; i++) {
            try {
                buffer.deposit(i);
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
        for (int i = 0; i < Main.NumData; i++) {
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
