package com.group31;

import com.group31.receiver.ReceiverRunnable;
import com.group31.sender.SenderRunnable;

import java.time.Duration;

public class BSSProcess {

    public static void main(String[] args) {
//        if (args.length < 2) {
//            system.out.println("arguments must be specified to identify the process");
//        }

//        int PID = Integer.parseInt(args[0]);
        final int PID = 1;
        int TOTAL_PROCESS_COUNT = 3;

        VectorClock localVectorClock = new VectorClock(TOTAL_PROCESS_COUNT);

        Thread senderThread = new Thread(new SenderRunnable(
                PID,
                10,
                Duration.ofMillis(1000),
                TOTAL_PROCESS_COUNT,
                localVectorClock
        ));

        Thread receiverThread = new Thread(new ReceiverRunnable(
                PID,
                TOTAL_PROCESS_COUNT,
                localVectorClock
        ));

        try {
            receiverThread.join();
            receiverThread.start();

            senderThread.join();
            senderThread.start();
        } catch (Exception e) {
            System.out.println("Exception in BSSProcess");
            e.printStackTrace();
        }
    }
}
