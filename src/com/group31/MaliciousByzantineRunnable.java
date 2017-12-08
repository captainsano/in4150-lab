package com.group31;

import com.group31.BroadcastRunnable;
import com.group31.ByzantineMessage;
import com.group31.ByzantineProcessDescription;
import com.group31.ProcessDescription;
import com.group31.receiver.ReceiverRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MaliciousByzantineRunnable implements Runnable, ReceiverRemoteInterface {
    private ProcessDescription thisProcess;
    private ArrayList<ProcessDescription> allProcesses;

    public MaliciousByzantineRunnable(ArrayList<ProcessDescription> allProcesses, ProcessDescription thisProcess) {
        this.thisProcess = thisProcess;
        this.allProcesses = allProcesses;
    }

    private void broadcast(String phase, int round, int w) {
        ByzantineMessage message = new ByzantineMessage(thisProcess, phase, round, w);
        System.out.println("Broadcasting: " + message);
        new Thread(new BroadcastRunnable(message, allProcesses)).start();
    }

    @Override
    public void receive(ByzantineMessage message) throws RemoteException {
        // Discard messages from self and broadcast opposite value from others
        if (message.getSender().getPid() != thisProcess.getPid()) {
            broadcast(message.getPhase(), message.getRound(), 1 - message.getW());
        }
    }

    @Override
    public void run() {
        try {
            // Bind receiver to registry
            ReceiverRemoteInterface stub = (ReceiverRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(thisProcess.getHostname());
            registry.rebind("process-" + thisProcess.getName(), stub);
            System.out.println("Receiver bound");


            // Initial delay to wait for other processes
            Thread.sleep(5000);
            broadcast("N", 1, ((int)(Math.random() * 10)) % 2);
        } catch (Exception e) {
            System.out.println("Exception in receiver binding");
            e.printStackTrace();
        }
    }
}

