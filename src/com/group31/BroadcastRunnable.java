package com.group31;

import com.group31.receiver.ReceiverRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class BroadcastRunnable implements Runnable {
    private static Integer MAX_INTERVAL = 2500;

    private ArrayList<ProcessDescription> destinations;
    private ByzantineMessage message;

    public BroadcastRunnable(ByzantineMessage message, ArrayList<ProcessDescription> destinations) {
        this.message = message;
        this.destinations = destinations;
    }

    private void makeRandomDelay() throws InterruptedException {
        long delay = (long)(Math.max(Math.random(), 0.1) * MAX_INTERVAL);
//        System.out.println("Random delay: " + delay + "ms");
        Thread.sleep(delay);
    }

    @Override
    public void run() {
        this.destinations.forEach(processDescription -> {
            try {
                makeRandomDelay();
                Registry r = LocateRegistry.getRegistry(processDescription.getHostname());
                ReceiverRemoteInterface receiver = (ReceiverRemoteInterface)r.lookup("process-" + processDescription.getName());
                receiver.receive(message);
                makeRandomDelay();
            } catch (Exception e) {
                System.out.println("Exception in broadcast runnable");
                e.printStackTrace();
            }
        });
    }
}
