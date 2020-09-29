/**
 * 
 */
package q14;

import java.util.concurrent.Semaphore;

/**
 * @author dennis
 *
 */
public class Assign2_q14 {
	public final static int NumMsg = 30;
	public final static int NumCons = 4;
	public final static int NumProd = 4;

	/**
	 * @param args the command line arguments (not used)
	 */
	public static void main(String[] args) {
		BoundedBuffer buf = new BoundedBuffer();
		Thread[] prod = new Thread[NumProd];
		Thread[] consumer = new Thread[NumCons];

		for (int i = 0; i < NumCons; i++) {
			consumer[i] = new Thread(new Consumer(i, buf));
			consumer[i].start();
		}
		for (int i = 0; i < NumProd; i++) {
			prod[i] = new Thread(new Producer(i, buf));
			prod[i].start();
		}

		try {
			for (int i = 0; i < NumCons; i++) {
				while (consumer[i].isAlive()) Thread.sleep(10);
				while (prod[i].isAlive()) Thread.sleep(10);
			}
		}
		catch (InterruptedException e) { }

		System.out.println("Done");

		System.exit(0);

	}

}
/** 
 * Buffer ensures that multiple producers and consumers can concurrently access 
 * the buffer.
 *
 * Inv: <pre>0 &leq; count &leq; NumBuf /\ 0 &leq; front, read &lt; NumBuf /\
 *     0 &leq; entry + depSem + fetchSem &leq; 1 /\
 *     Each item deposited is fetched exactly once.
 * </pre>
 */
class BoundedBuffer {
	/** The buffer where the data is stored */
	private int[] buf; 
	/** Buffer size */
	private static final int NumBuf = 5;
	/** Next place for producer to write to */
	private int rear; 
	/** Next place for consumer to read from */
	private int front; 
	/** Number of buffer slots used */
	private int count; 
	/** Entry semaphore */
	private Semaphore entry; 
	/** Semaphore for depositing */
	private Semaphore depSem; 
	/** Semaphore for fetching */
	private Semaphore fetchSem;  
	/** Number of threads waiting to deposit */
	private int delDep;
	/** Number of threads waiting to fetch */
	private int delFetch;


	public BoundedBuffer()
	{
		buf = new int[NumBuf];
		front = 0;
		rear = 0;
		count = 0;
		entry = new Semaphore(1);
		depSem = new Semaphore(0);
		fetchSem = new Semaphore(0);
		delDep = 0;
		delFetch = 0;
	}

	public void deposit(int x)
	throws InterruptedException
	{
		// < await(count < NumBuf) count++; depSlot = rear; rear = (rear+1)%NumBuf; >
		entry.acquire();
		if (count == NumBuf) {
			delDep++;
			entry.release();
			depSem.acquire();
			delDep--;
		}
		count++;
		int depSlot = rear;
		rear = (rear + 1) % NumBuf;
		signal();
		// Note that the access to the buffer itself is not mutually exclusive.
		buf[depSlot] = x;


	}

	public int fetch()
	throws InterruptedException
	{
		// < await(count > 0) count--; fetchSlot = front; front = (front+1)%NumBuf; >
		entry.acquire();
		if (count == 0) {
			delFetch++;
			entry.release();
			fetchSem.acquire();
			delFetch--;
		}
		count--;
		int fetchSlot = front;
		front = (front+1) % NumBuf;
		signal();
		// Note that the access to the buffer itself is not mutually exclusive.
		int result = buf[fetchSlot];

		return result;
	}

	private void signal() {
		if (count < NumBuf && delDep > 0) {
			depSem.release();
		} else if (count > 0 && delFetch > 0) {
			fetchSem.release();
		} else {
			entry.release();
		}	  
	}

}

class Consumer
implements Runnable
{
	/** consumer number */
	private int myId;  
	/** Id of this producer */
	private BoundedBuffer buf; 

	/******************************************************************
	 * @param id_ consumer Id
	 * @param buf_ shared atomic broadcast buffer
	 ******************************************************************/
	Consumer(int id_, BoundedBuffer buf_)
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
		for (int i = 0; i < Assign2_q14.NumMsg; i++) {
			try {
				Thread.sleep((int)Math.round(Math.random()*100));
				msg = buf.fetch();
				System.out.println("Consumer " + myId + " got " + msg);
			}
			catch (InterruptedException e) {}
		}

	}
}

class Producer
implements Runnable
{
	/** The buffer to write to */
	private BoundedBuffer buf; 
	/** Id of this producer */
	private int myId;          

	/******************************************************************
	 * @param buf_ shared atomic broadcast buffer
	 ******************************************************************/
	Producer(int id, BoundedBuffer buf_)
	{
		myId = id;
		buf = buf_;
	}

	/******************************************************************
	 * Method invoked by start.
	 ******************************************************************/
	public void run()
	{
		int dep = 0;
		for (int i = 0; i < Assign2_q14.NumMsg; i++) {
			try { 
				dep = myId * Assign2_q14.NumProd + i; // make a unique number
				Thread.sleep((int)Math.round(Math.random()*100));
				System.out.println("Producer " + myId + " deposits " + dep);
				buf.deposit(dep);
			}
			catch (InterruptedException e) {}
		}
	}
}

