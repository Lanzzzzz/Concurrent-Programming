package semaph;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

public class semaph_demo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);

        for (int i = 0; i < 10; i++) {
            SemaphoreTask semaphoreTask = new SemaphoreTask(semaphore);
            new Thread(semaphoreTask).start();
        }
    }

}


