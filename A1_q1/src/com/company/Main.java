package com.company;

class Get_Sum implements Runnable
{
    private Thread t;
    private int[] array;//The original array
    private int start;//start index of this thread
    private int num;//total numbers needed to be added
    public int result;

    Get_Sum(int[] a, int s, int n)
    {//construct
        array = a;
        start = s;
        num = n;
        result = 0;
    }

    public void run()
    {
        for(int i = 0; i < num; i++)
        {
            result += array[start];
            start++;
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

    public boolean isAlive()
    {
        return t.isAlive();
    }
}

public class Main
{
    public static final int array_num = 100;
    public static final int Thread_num = 5;

    public static void main(String[] args)
    {
        int[] a = new int[array_num];
        Get_Sum[] Sum_a = new Get_Sum[Thread_num];

        for (int i = 0; i < array_num; i++) {
            a[i] = (int)Math.round(Math.random()*100);//set array as random numbers
            System.out.print(a[i] + ", ");
        }
        System.out.print("\n");

        int Thread_size = array_num/Thread_num;//size for each thread
        for(int i=0; i<Thread_num; i++)
        {
            Sum_a[i] = new Get_Sum(a, i*Thread_size, Thread_size);//construct each thread
            Sum_a[i].start();//start thread to calculate the sub-sum of each tread
        }

        int Sum = 0;
        try
        {
            for (int p = 0; p < Thread_num; p++)
            {
                while (Sum_a[p].isAlive())
                {
                    Thread.sleep(50);
                }
                Sum += Sum_a[p].result;//get the total sum
            }
        }
        catch(InterruptedException e){}

        System.out.print("Sum = " + Sum);

    }
}


