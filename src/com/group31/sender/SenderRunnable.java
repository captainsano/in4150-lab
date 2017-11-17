package com.group31.sender;

import com.group31.Message;
import com.group31.receiver.ReceiverRemoteInterface;
import com.group31.VectorClock;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SenderRunnable implements Runnable {
    private final int SENDER_INIT_BACKOFF_MILLIS = 5000;

    private final int PID;

    private final int MAX_MESSAGE_COUNT;
    private final int MAX_INTERVAL;

    private final int TOTAL_PROCESS_COUNT;
    private VectorClock localClock;

    public SenderRunnable(
            int pid,
            int maxMessageCount,
            int maxInterval,
            int totalProcessCount,
            VectorClock localClock
    ) {
        this.PID = pid;
        this.MAX_MESSAGE_COUNT = maxMessageCount;
        this.MAX_INTERVAL = maxInterval;
        this.TOTAL_PROCESS_COUNT = totalProcessCount;
        this.localClock = localClock;
    }

    private void makeRandomDelay() throws InterruptedException {
        long delay = (long)(Math.max(Math.random(), 0.1) * MAX_INTERVAL);
        System.out.println("Random delay: " + delay + "ms");
        Thread.sleep(delay);
    }

    @Override
    public void run() {
        try {
            System.out.println("Sender waiting for network setup");
            Thread.sleep(SENDER_INIT_BACKOFF_MILLIS);
        } catch (Exception e) {
            System.out.println("Sender backoff exception");
            e.printStackTrace();
        }

        for (int i = 0; i < MAX_MESSAGE_COUNT; i++) {
            try {
                Message m;
                synchronized (localClock) {
                    localClock.increment(PID);
                    System.out.println("Broadcasting message " + i + " at " + localClock);
                    m = new Message(PID, localClock, "<some-content>");
                }

                for (int j = 0; j < TOTAL_PROCESS_COUNT; j++) {
                    if (j != PID) { // For now, send to itself
                        String name = "process-" + j;
                        Registry registry = LocateRegistry.getRegistry("localhost"); // TODO: Lookup corresponding hostname
                        ReceiverRemoteInterface receiver = (ReceiverRemoteInterface) registry.lookup(name);
                        makeRandomDelay();
                        receiver.receiveMessage(m);
                    }
                }

                makeRandomDelay();
            } catch (Exception e) {
                System.out.println("Exception in sender thread");
                e.printStackTrace();
            }
        }
    }
}
