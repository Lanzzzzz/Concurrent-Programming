package com.company;

public class Main {

    private static int Bath_capacity = 3;
    private static int peopleNum = 10;

    public static void main(String[] args) {
	// write your code here
        Bathroom bath = new Bathroom(Bath_capacity);
        Thread[] persons = new Thread[peopleNum];
        Sex s;

        for (int i = 0; i < peopleNum; i++) {
            if ((int)Math.round(Math.random()*peopleNum) > peopleNum/2)
            {
                s = Sex.Female;
            }
            else
            {
                s = Sex.Male;
            }
            persons[i] = new Thread(new Person(bath, s, i));
            System.out.println("Person " + i  + " (" + s + ") initialized. ");
            persons[i].start();

        }
    }
}
