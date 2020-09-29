package com.company;

public class Assign3_Waiter {

    int philoNUM;
    private boolean[] position_inUse;

    Assign3_Waiter(int Num)
    {
        philoNUM = Num;
        position_inUse = new boolean[philoNUM];
        for (int i = 0; i < philoNUM; i++)
        {
            position_inUse[i] = false;
        }
    }

    public synchronized void request(int seat) throws InterruptedException {
        while (position_inUse[seat] //current position in use
                || position_inUse[(seat+1)%philoNUM]//right position in use
                || position_inUse[(seat+philoNUM-1)%philoNUM])//left position in use
        {
            System.out.println("Philo:" + seat + " is requesting");
            wait(Math.round(Math.random()*1000));
        }
        position_inUse[seat] = true;
        System.out.println("Philo:" + seat + " requested");
    }

    public synchronized void done(int seat)
    {
        position_inUse[seat] = false;
        System.out.println("Philo:" + seat + " done");
    }
}
