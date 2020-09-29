package com.company;

import java.util.concurrent.Semaphore;

/******************************************************************
 * Memorial University of Newfoundland<br>
 * 8893 Concurrent Programming<br>
 * Assignment 2 Q2 (b) -- Andrews00 q 4.16(b)<br>
 * Sample solution.
 *
 * @version $Revision$ $Date$
 * @author Dennis Peters ($Author$)
 *
 * $RCSfile$
 * $State$
 *
 ******************************************************************/
public class Main
{
    public final static int NumMsg = 30;
    public final static int NumCons = 4;

    /******************************************************************
     * @param args the command line arguments (not used)
     ******************************************************************/
    public static void main(String[] args)
    {
        BroadcastBuffer buf = new BroadcastBuffer();
        Thread prod = new Thread(new Producer(buf));
        Thread[] consumer = new Thread[NumCons];

        for (int i = 0; i < NumCons; i++) {
            consumer[i] = new Thread(new Consumer(i, buf));
            consumer[i].start();
        }
        prod.start();

        try {
            for (int i = 0; i < NumCons; i++) {
                while (consumer[i].isAlive()) Thread.sleep(10);
            }
            while (prod.isAlive()) Thread.sleep(10);
        }
        catch (InterruptedException e) { }

        System.out.println("Done");

        System.exit(0);
    }
}

/**
 * Buffer ensures that multiple consumers all read the contents before it
 * can overwritten by a future <code>deposit</code>.
 *
 * Inv: <pre>0 &lt;= entry + empty + sum(i=0:NumBuf)(full[i]) &lt;= 1 /\
 *      (A)i, 0 &lt; i &lt; NumBuf -&gt;
 *        numToRead[i] = NumCons - (number of consumers who have already
 *                                read buf[i] since it was last written) /\
 *      (A)i, 0 &lt; i &lt; assign2_q16b.NumCons -&gt;
 *        front[i] = oldest buffer written but not read by consumer i.</pre>
 */
class BroadcastBuffer {
    private int[] buf; /** The buffer where the data is stored */
    private Semaphore entry; /** All these semaphores operate as SBS */
    private Semaphore[] full;
    private Semaphore empty;
    private int[] dFull; /** counters for number delayed */
    private int dEmpty;
    private static final int NumBuf = 5;
    private int rear; /** Next place for producer to write to */
    private int[] front; /** Next place for consumer i to read from */
    private int[] numToRead; /** Number of readers left to read this slot */

    public BroadcastBuffer()
    {
        buf = new int[NumBuf];//创建5个slot
        numToRead = new int[NumBuf];
        front = new int[Main.NumCons];
        rear = 0;

        entry = new Semaphore(1);//初始线程1
        empty = new Semaphore(0);//初始线程0
        full = new Semaphore[NumBuf];//总线程5
        dFull = new int[NumBuf];
        for (int i = 0; i < NumBuf; i++) {
            full[i] = new Semaphore(0);//初始线程0
            numToRead[i] = 0;
            dFull[i] = 0;
        }
        dEmpty = 0;
    }

    public void deposit(int x)
            throws InterruptedException
    {
        // < await(numToRead[rear] == 0) >
        entry.acquire();
        if (numToRead[rear] > 0) {
            dEmpty++;
            entry.release();
            empty.acquire();
        }
        signal();

        // { numToRead[rear] == 0 }
        buf[rear] = x;
        System.out.println("Producer deposits " + x + " in slot " + rear);

        // < numToRead[rear] = NumCons; rear = (rear + 1) % NumBuf; >
        entry.acquire();
        numToRead[rear] = Main.NumCons;
        rear = (rear + 1) % NumBuf;
        signal();
    }

    public int fetch(int id)
            throws InterruptedException
    {
        // < await(numToRead[front[id]] > 0) >
        entry.acquire();
        if (numToRead[front[id]] == 0) {
            dFull[front[id]]++;
            entry.release();
            full[front[id]].acquire();
        }
        signal();

        // { numToRead[front[id]] > 0 }
        int result = buf[front[id]];
        System.out.println("Consumer " + id + " gets " + result);

        // < numToRead[front[id]]--; front[id] = (front[id] + 1) % NumBuf; >
        entry.acquire();
        numToRead[front[id]]--;
        front[id] = (front[id] + 1) % NumBuf;
        signal();

        return result;
    }

    private void signal()
    {
        boolean released = false;
        if (dEmpty > 0 && numToRead[rear] == 0) {
            dEmpty--;
            released = true;
            empty.release();
        } else {
            int i = 0;
            while (!released && i < NumBuf) {
                if (dFull[i] > 0 && numToRead[i] > 0) {
                    released = true;
                    dFull[i]--;
                    full[i].release();
                }
                i++;
            }
        }
        if (!released) {
            entry.release();
        }
    }

}

class Consumer
        implements Runnable
{
    private int myId;  /** consumer number */
private BroadcastBuffer buf; /** The buffer to read from */

    /******************************************************************
     * @param id_ consumer Id
     * @param buf_ shared atomic broadcast buffer
     ******************************************************************/
    Consumer(int id_, BroadcastBuffer buf_)
    {
        myId = id_;
        buf = buf_;
    }

    /******************************************************************
     * Method invoked by start.
     ******************************************************************/
    public void run()
    {
        int msg;
        for (int i = 0; i < Main.NumMsg; i++) {
            try {
                Thread.sleep((int)Math.round(Math.random()*100));
                msg = buf.fetch(myId);
            }
            catch (InterruptedException e) {}
        }

    }
}

class Producer
        implements Runnable
{
    private BroadcastBuffer buf; /** The buffer to write to */

    /******************************************************************
     * @param buf_ shared atomic broadcast buffer
     ******************************************************************/
    Producer(BroadcastBuffer buf_)
    {
        buf = buf_;
    }

    /******************************************************************
     * Method invoked by start.
     ******************************************************************/
    public void run()
    {
        for (int i = 0; i < Main.NumMsg; i++) {
            try {
                Thread.sleep((int)Math.round(Math.random()*100));
                buf.deposit(i);
            }
            catch (InterruptedException e) {}
        }
    }
}

/******************************************************************
 *                     REVISION HISTORY
 *
 * $Log$
 *
 ******************************************************************/
