package com.group31.receiver;

import com.group31.Message;
import com.group31.VectorClock;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ReceiverRunnable implements Runnable, ReceiverRemoteInterface {
    int pid;
    VectorClock localClock;

    public ReceiverRunnable(int pid, int totalProcessCount, VectorClock localClock) {
        this.pid = pid;
        this.localClock = localClock;
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        System.out.println("Received: " + message);
    }

    @Override
    public void run() {
        try {
            System.out.println("Running receiver");
            String name = "process-" + this.pid;
            ReceiverRemoteInterface stub = (ReceiverRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ReceiverRunnable bound");
        } catch (Exception e) {
            System.err.println("Error in receiver thread:");
            e.printStackTrace();
        }
    }
}
