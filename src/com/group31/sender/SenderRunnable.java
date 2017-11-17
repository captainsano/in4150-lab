package com.group31.sender;

import com.group31.Message;
import com.group31.receiver.ReceiverRemoteInterface;
import com.group31.VectorClock;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;

public class SenderRunnable implements Runnable {
    private final int SENDER_INIT_BACKOFF_MILLIS = 15000;

    private final int PID;

    private int messageCount;
    private Duration interval;

    private int totalProcessCount;
    private VectorClock localClock;

    public SenderRunnable(
            int pid,
            int messageCount,
            Duration interval,
            int totalProcessCount,
            VectorClock localClock
    ) {
        this.PID = pid;
        this.messageCount = messageCount;
        this.interval = interval;
        this.totalProcessCount = totalProcessCount;
        this.localClock = localClock;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(SENDER_INIT_BACKOFF_MILLIS);
        } catch (Exception e) {
            System.out.println("Sender backoff exception");
            e.printStackTrace();
        }

        for (int i = 0; i < this.messageCount; i++) {
            try {
                this.localClock.increment(this.PID);
                System.out.println("Sending message " + i + " at " + this.localClock);
                Thread.sleep(this.interval.toMillis());

                String name = "process-" + this.PID;
                Registry registry = LocateRegistry.getRegistry("localhost");
                ReceiverRemoteInterface receiver = (ReceiverRemoteInterface) registry.lookup(name);

                receiver.receiveMessage(new Message(this.PID, this.localClock, "<some-content>"));
            } catch (Exception e) {
                System.out.println("Exception in sender thread");
                e.printStackTrace();
            }
        }
    }
}
