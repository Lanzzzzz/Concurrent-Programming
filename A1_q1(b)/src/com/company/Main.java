package com.company;

class Get_Sum_Recur implements Runnable
{
    private Thread t;
    private int[] array;//The original array
    private int start;//start index of this thread
    private int num;//total numbers needed to be added
    public int result;

    Get_Sum_Recur(int[] a, int s, int n)
    {//construct
        array = a;
        start = s;
        num = n;
        result = 0;
    }

    public void run()
    {
        if (num <= Main.array_num/Main.Thread_ratio)
        {
            for (int i = 0; i < num; i++)
            {
                result += array[start];
                start++;
            }
        }
        else
        {//recursively split the array into two parts until the size is smaller than the set value
            Get_Sum_Recur left = new Get_Sum_Recur(array, start, num/2);
            Get_Sum_Recur right = new Get_Sum_Recur(array, start+num/2, num-num/2);

            left.start();
            right.start();
            try {
                while (left.isAlive()) Thread.sleep(50);
                while (right.isAlive()) Thread.sleep(50);
            }
            catch (InterruptedException e) {}
            result = left.result + right.result;
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
    public static final int Thread_ratio = 5;

    public static void main(String[] args)
    {
        int[] a = new int[array_num];
        Get_Sum_Recur Sum_a = new Get_Sum_Recur(a, 0, array_num);

        for (int i = 0; i < array_num; i++) {
            a[i] = (int)Math.round(Math.random()*100);//set array as random numbers
            System.out.print(a[i] + ", ");
        }
        System.out.print("\n");

       Sum_a.start();

        int Sum = 0;
        try
        {
            while (Sum_a.isAlive())
            {
                Thread.sleep(50);
            }
            System.out.print("Sum = " + Sum_a.result);
        }
        catch(InterruptedException e){}

    }
}


