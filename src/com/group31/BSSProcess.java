package com.group31;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;

public class BSSProcess {

    public static void main(String[] args) {
        int PID = 1;
        int totalProcessCount = 3;

        VectorClock vc = new VectorClock(totalProcessCount);

        Sender sender = new Sender(
                PID,
                10,
                Duration.ofMillis(1000),
                totalProcessCount,
                vc
        );

        Thread senderThread = new Thread(sender);

        String name = "Process-" + PID;
        Receiver receiver = new Receiver(
                PID,
                totalProcessCount,
                vc
        );

        Thread receiverThread = new Thread(receiver);

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
