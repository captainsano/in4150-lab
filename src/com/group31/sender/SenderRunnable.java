package com.group31.sender;

import com.group31.Message;
import com.group31.receiver.ReceiverRemoteInterface;
import com.group31.VectorClock;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.util.Arrays;

public class SenderRunnable implements Runnable {
    private final int SENDER_INIT_BACKOFF_MILLIS = 15000;

    private final int PID;

    private final int MAX_MESSAGE_COUNT;
    private Duration interval;

    private final int TOTAL_PROCESS_COUNT;
    private VectorClock localClock;

    public SenderRunnable(
            int pid,
            int maxMessageCount,
            Duration interval,
            int totalProcessCount,
            VectorClock localClock
    ) {
        this.PID = pid;
        this.MAX_MESSAGE_COUNT = maxMessageCount;
        this.interval = interval;
        this.TOTAL_PROCESS_COUNT = totalProcessCount;
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

        for (int i = 0; i < MAX_MESSAGE_COUNT; i++) {
            try {
                localClock.increment(PID);
                System.out.println("Broadcasting message " + i + " at " + localClock);

                for (int j = 0; j < TOTAL_PROCESS_COUNT; j++) {
                    if (j == PID) { // For now, send to itself
                        String name = "process-" + j;
                        Registry registry = LocateRegistry.getRegistry("localhost");
                        ReceiverRemoteInterface receiver = (ReceiverRemoteInterface) registry.lookup(name);
                        receiver.receiveMessage(new Message(PID, localClock, "<some-content>"));
                    }
                }

                Thread.sleep(interval.toMillis());
            } catch (Exception e) {
                System.out.println("Exception in sender thread");
                e.printStackTrace();
            }
        }
    }
}
