package com.company;

public class Main {

    private static int Num_peo =6;

    public static void main(String[] args) {
	// write your code here
        Chopstick[] chopsticks = new Chopstick[Num_peo];
        Assign3_Waiter josh = new Assign3_Waiter(Num_peo);
        Thread[] philosophers = new Thread[Num_peo];

        for (int i = 0; i < Num_peo; i++) {
            chopsticks[i] = new Chopstick();
        }

        for (int i = 0; i < Num_peo; i++) {
            philosophers[i] = new Thread(new Philosopher(i, josh, chopsticks[i], chopsticks[(i+1)%Num_peo]));
            philosophers[i].start();
            System.out.println("Philo:" + i + " initialized");
        }
    }
}
