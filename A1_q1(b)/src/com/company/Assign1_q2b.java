/******************************************************************
 * Memorial University of Newfoundland<br>
 * 7894 Concurrent Programming<br>
 * Assignment 1 Q1 (b) -- Andrews00 q 2.7(b)<br>
 * Sample solution.
 *
 * @version $Revision: 758 $ $Date: 2009-06-03 23:21:53 -0230 (Wed, 03 Jun 2009) $
 * @author Dennis Peters ($Author: dpeters $)
 *
 * $Id: Assign1_q2b.java 758 2009-06-04 01:51:53Z dpeters $   
 *
 ******************************************************************/
public class Assign1_q2b
{
  public static final int n = 40; /** Size of arry */
  public static final int pr = 5; 
  /** number of processes. Assumed to be a divisor of n */
  
/******************************************************************
 * @param args the command line arguments (not used)
 ******************************************************************/
  public static void main(String[] args)
  {
    int[] a = new int[n]; // shared array
    RecursiveSummer worker_impl = new RecursiveSummer(a, 0, n);
    Thread worker = new Thread(worker_impl); // worker process

    for (int i = 0; i < n; i++) {
      a[i] = (int)Math.round(Math.random()*2*n);
      // Fill the array with random values
      System.out.print(a[i] + ", ");
    }
    System.out.println();

    worker.start();
    try {
      while (worker.isAlive()) Thread.sleep(50);
    }
    catch (InterruptedException e) {}

    System.out.print("Sum = " + worker_impl.getSum());
    if (Oracle(a, worker_impl.getSum())) {
      System.out.println(" correct!");
    } else {
      System.out.println(" false!");
    }
    System.exit(0);
  }

  /**
   * Test to see if the results are correct.
   * @return true if the result is correct, false otherwise
   */
  private static boolean Oracle(int[] a, int sum) 
  {
    int actual = 0;
    for (int i = 0; i < n; i++) {
      actual += a[i];
    }
    return (actual == sum);
  }
}

class RecursiveSummer
  implements Runnable
{
  private int[] a;  /** shared array */
  private int first; /** start index for my stripe */
  private int num; /** number of elements to sum */
  private int sum; /** result */

/******************************************************************
 * @param a_ the array to be shared. 
 * @param first_ the index for this instance to start at
 * @param num_ the number of elements to sum
 ******************************************************************/
  RecursiveSummer(int[] a_, int first_, int num_)
  {
    a = a_;
    first = first_;
    num = num_;
    sum = 0;
  }

/******************************************************************
 * Method invoked by start.
 ******************************************************************/
  public void run()
  {
    if (num <= Assign1_q2b.n/Assign1_q2b.pr) {
      // base case, num < threshold
      int indx = first;
      for (int i = 0; i < num; i++) { // { sum = sigma_{j=0}^{num-1}{a[first+j]} }
        sum += a[indx];
        indx++;
      }
    } else {
      // recursive case, split list in half.
      RecursiveSummer left = new RecursiveSummer(a, first, num/2);
      RecursiveSummer right = new RecursiveSummer(a, first+num/2, num-num/2);
      Thread left_th = new Thread(left);
      Thread right_th = new Thread(right);
      left_th.start();
      right_th.start();
      try {
        while (left_th.isAlive()) Thread.sleep(50);
        while (right_th.isAlive()) Thread.sleep(50);
      } 
      catch (InterruptedException e) {}
      // { left.getSum() = sigma_{i=first}^{first+num/2-1}{a[i]} /\
      //   right.getSum() = sigma_{i=first+num/2}^{first+(num-num/2)-1}{ a[i] } }
      sum = left.getSum() + right.getSum();
    }
    // {sum = sigma_{i=first}^{first+num-1}{a[i]}
    // non-interference is by disjoint variables.
  }

/******************************************************************
 * Return the sum computed by this instance.
 ******************************************************************/
  public int getSum()
  {
    return sum;
  }
  
}

/******************************************************************
 *                     REVISION HISTORY
 *
 * $Log$
 *
 ******************************************************************/

