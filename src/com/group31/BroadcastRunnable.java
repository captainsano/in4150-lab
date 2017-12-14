package com.group31;

import com.group31.receiver.ReceiverRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;

public class BroadcastRunnable implements Runnable {
    private static Integer MAX_INTERVAL = 2500;

    private ProcessDescription sender;
    private ArrayList<ProcessDescription> destinations;
    private ByzantineMessage message;

    public BroadcastRunnable(ProcessDescription sender, ByzantineMessage message, ArrayList<ProcessDescription> destinations) {
        this.sender = sender;
        this.message = message;
        this.destinations = destinations;
    }

    private void makeRandomDelay() throws InterruptedException {
        int delay = new Random().nextInt(100);
        Thread.sleep(delay);
    }

    @Override
    public void run() {
        this.destinations.forEach(processDescription -> {
            if (processDescription.getPid() != this.sender.getPid()) {
                try {
                    makeRandomDelay();
                    Registry r = LocateRegistry.getRegistry(processDescription.getHostname());
                    ReceiverRemoteInterface receiver = (ReceiverRemoteInterface) r.lookup("process-" + processDescription.getPid());
                    receiver.receive(message);
                    makeRandomDelay();
                } catch (Exception e) {
                    //                System.out.println("Exception in broadcast runnable");
                    //                e.printStackTrace();
                }
            }
        });
    }
}
