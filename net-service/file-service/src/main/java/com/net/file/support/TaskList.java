package com.net.file.support;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TaskList extends Thread{
    private BlockingQueue<Runnable> blockingQueue=new LinkedBlockingQueue<>();
    public TaskList() {
        setName("RunnableList");
        start();
    }
    public void addTask(Runnable task){
        blockingQueue.offer(task);
    }

    @Override
    public void run() {
        while(true){
            Runnable task = null;
            try {
                task = blockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            task.run();
        }
    }

}
