package com.group31;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;

public class Sender implements Runnable {
    private int pid;

    private int messageCount;
    private Duration interval;

    private int totalProcessCount;
    private VectorClock vectorClock;

    public Sender(
            int pid,
            int messageCount,
            Duration interval,
            int totalProcessCount,
            VectorClock vectorClock
    ) {
        this.pid = pid;
        this.messageCount = messageCount;
        this.interval = interval;
        this.totalProcessCount = totalProcessCount;
        this.vectorClock = vectorClock;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.messageCount; i++) {
            try {
                this.vectorClock.increment(this.pid);
                System.out.println("Sending message " + i + " at " + this.vectorClock);
                Thread.sleep(this.interval.toMillis());

                String name = "process-" + this.pid;
                Registry registry = LocateRegistry.getRegistry("localhost");
                ReceiverRMIInterface receiver = (ReceiverRMIInterface) registry.lookup(name);

                receiver.receiveMessage(this.pid, "Hello: " + i, this.vectorClock);
            } catch (Exception e) {
                System.out.println("Exception in sender thread");
                e.printStackTrace();
            }
        }
    }
}
