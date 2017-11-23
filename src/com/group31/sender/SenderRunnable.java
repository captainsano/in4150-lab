package com.group31.sender;

import com.group31.Message;
import com.group31.receiver.ReceiverRemoteInterface;
import com.group31.VectorClock;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class SenderRunnable implements Runnable {
    private final int SENDER_INIT_BACKOFF_MILLIS = 5000;

    private final int PID;

    private final int MAX_MESSAGE_COUNT;
    private final int MAX_INTERVAL;
    private final int START_DELAY;

    private final int TOTAL_PROCESS_COUNT;
    private VectorClock localClock;

    private final ArrayList<String> hosts;

    public SenderRunnable(
            ArrayList<String> hosts,
            int pid,
            int maxMessageCount,
            int maxInterval,
            int totalProcessCount,
            VectorClock localClock,
            int startDelay
    ) {
        this.hosts = hosts;
        this.PID = pid;
        this.MAX_MESSAGE_COUNT = maxMessageCount;
        this.MAX_INTERVAL = maxInterval;
        this.TOTAL_PROCESS_COUNT = totalProcessCount;
        this.localClock = localClock;
        this.START_DELAY = startDelay;
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
            Thread.sleep(START_DELAY);
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
                    if (j != PID) {
                        String name = "process-" + j;
                        Registry registry = LocateRegistry.getRegistry(hosts.get(j));
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
