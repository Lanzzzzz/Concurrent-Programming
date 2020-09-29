package semaph;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

public class SemaphoreTask implements Runnable {

    private Semaphore semaphore;

    public SemaphoreTask(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            Thread.sleep(5000);
            System.out.println("1:["+LocalDateTime.now()+"]"+Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release(2);
            //System.out.println("2:["+LocalDateTime.now()+"]"+Thread.currentThread().getName());
        }
    }
}
