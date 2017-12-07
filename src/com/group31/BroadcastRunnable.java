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

    private void makeRandomDelay() {
        try {
            Thread.sleep(MAX_INTERVAL);
        } catch (Exception e) {
            System.out.println("Exception in broadcast runnable");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.destinations.forEach(processDescription -> {
            makeRandomDelay();
            try {
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
