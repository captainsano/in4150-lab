package com.group31;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Receiver implements Runnable {
    int pid;
    ReceiverRMIInterface rmiReceiver;

    public Receiver(int pid, int totalProcessCount, VectorClock vectorClock) {
        this.pid = pid;
        this.rmiReceiver = new RMIReceiver(pid, totalProcessCount, vectorClock);
    }

    @Override
    public void run() {
        try {
            String name = "process-" + this.pid;
            ReceiverRMIInterface stub = (ReceiverRMIInterface) UnicastRemoteObject.exportObject(this.rmiReceiver, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Receiver bound");
        } catch (Exception e) {
            System.err.println("Error in receiver thread:");
            e.printStackTrace();
        }
    }
}
