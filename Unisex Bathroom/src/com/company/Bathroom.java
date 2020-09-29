package com.company;

public class Bathroom {

    int Capacity;//capacity of the bathroom
    int current_p_num;//current number of people in the bathroom
    int total_num;//total number for one gender turn
    Sex current_g; //current gender in the bathroom
    boolean switch_gender;//a time to switch the gender in the bathroom
  //  boolean male_turn, female_turn; // define whether a period of time is for male or female

    Bathroom(int Cap)
    {
        Capacity = Cap;
        switch_gender = false;
        current_g = Sex.Female;//lady first
       // female_turn = true;
       // male_turn = false;
        current_p_num=0;
        total_num=0;
    }

    public synchronized void enter(int id, Sex g) throws InterruptedException {
        while ( g!= current_g //wait when the gender is different
                || current_p_num == Capacity //wait when the bathroom is full
                || switch_gender) //wait when the process of switching gender is ongoing
        {
            wait((int)Math.round(Math.random()*50));
        }
        current_p_num++;
        total_num++;
        System.out.println("current people: " + current_p_num );

        if (total_num >= 2*Capacity)//after some people have entered, switch the gender
        {
            switch_gender = true;
            total_num = 0;
            System.out.println("Switch " + g + " start");
        }

    }

    public synchronized void leave(Sex g)
    {
        current_p_num--;

        if (switch_gender && current_p_num == 0)//when everyone has left
        {
            //switch the current available gender
            if (g == Sex.Female)
            {
                current_g = Sex.Male;
            }
            else if(g == Sex.Male)
            {
                current_g = Sex.Female;
            }
            switch_gender = false;
            System.out.println("Switch " + g + " done");
        }
    }
}
