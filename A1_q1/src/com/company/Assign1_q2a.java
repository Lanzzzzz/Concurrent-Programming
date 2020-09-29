/******************************************************************
 * Memorial University of Newfoundland<br>
 * 7894 Concurrent Programming<br>
 * Assignment 1 Q1 (a) -- Andrews00 q 2.7(a)<br>
 * Sample solution.
 *
 * @version $Revision: 757 $ $Date: 2009-06-03 23:21:35 -0230 (Wed, 03 Jun 2009) $
 * @author Dennis Peters ($Author: dpeters $)
 *
 * $Id: Assign1_q2a.java 757 2009-06-04 01:51:35Z dpeters $   
 *
 ******************************************************************/
public class Assign1_q2a
{
  public static final int n = 20; /** Size of array */
  public static final int pr = 4; 
  /** number of processes. Assumed to be a divisor of n */
  
/******************************************************************
 * @param args the command line arguments (not used)
 ******************************************************************/
  public static void main(String[] args)
  {
    int[] a = new int[n]; // shared array
    IterativeSummer[] worker_impl = new IterativeSummer[pr];
    Thread[] worker = new Thread[pr]; // worker processes
    int total = 0; // total of all sums

    for (int i = 0; i < n; i++) {
      a[i] = (int)Math.round(Math.random()*2*n);
      // Fill the array with random values
      System.out.print(a[i] + ", ");
    }
    System.out.println();

    int stripSize = n/pr;
    for (int p = 0; p < pr; p++) {
      worker_impl[p] = new IterativeSummer(a, p*stripSize, stripSize);
      worker[p] = new Thread(worker_impl[p]);
    }
    for (int p = 0; p < pr; p++) {
      worker[p].start();
    }
    try {
      for (int p = 0; p < pr; p++) { // { total = sigma_{i = 0}{p-1}{worker_impl[i].getSum(); }
        while (worker[p].isAlive()) Thread.sleep(50); 
        // { worker[p].getSum() = sigma_{i=p*stripSize}^{(p+1)*stripSize-1}{a[i]} }
        total += worker_impl[p].getSum();
      }
    }
    catch (InterruptedException e) {}

    System.out.print("Sum = " + total);
    if (Oracle(a, total)) {
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

class IterativeSummer
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
  IterativeSummer(int[] a_, int first_, int num_)
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
    int indx = first;
    for (int i = 0; i < num; i++) { // { sum = sigma_{j=first}^{indx-1}{a[j]} }
      sum += a[indx];
      indx++;
    }
    // { sum = sigma_{j=first}^{first+num-1}{a[j]} }
    // Interference freedom is by disjoint variables.
  }

/******************************************************************
 * Return the sum computed by this instance.
 ******************************************************************/
  public int getSum()
  {
    return sum;
  }
  
}
