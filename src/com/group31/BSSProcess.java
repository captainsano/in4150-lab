package com.group31;

import com.group31.receiver.ReceiverRunnable;
import com.group31.sender.SenderRunnable;

public class BSSProcess {
    /**
     * The BSS process launches a sender and a receiver thread and waits for them to complete.
     * @param args 0 - file to read the network addresses
     *             1 - this process number
     *             2 - max random delay in milliseconds
     *             3 - max messages to send
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("required args: <network-file> <this-pid> <max-random-delay-ms> <max-messages>");
            System.exit(0);
        }

        final int PID = Integer.parseInt(args[1]);
        final int MAX_INTERVAL = Integer.parseInt(args[2]);
        final int MAX_MESSAGE_COUNT = Integer.parseInt(args[3]);
        final int TOTAL_PROCESS_COUNT = 5; // TODO: infer this from network file

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
