package com.group31;

import com.group31.receiver.ReceiverRunnable;
import com.group31.sender.SenderRunnable;

public class BSSProcess {

    private static final int MAX_INTERVAL = 10000;
    private static final int MAX_MESSAGE_COUNT = 10;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("required args: <pid> <total-process-count>");
            System.exit(0);
        }

        final int PID = Integer.parseInt(args[0]);
        int TOTAL_PROCESS_COUNT = Integer.parseInt(args[1]); // TODO: get this from file

        VectorClock localVectorClock = new VectorClock(TOTAL_PROCESS_COUNT);

        Thread senderThread = new Thread(new SenderRunnable(
                PID,
                MAX_MESSAGE_COUNT,
                MAX_INTERVAL,
                TOTAL_PROCESS_COUNT,
                localVectorClock
        ));

        Thread receiverThread = new Thread(new ReceiverRunnable(PID, localVectorClock));

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
