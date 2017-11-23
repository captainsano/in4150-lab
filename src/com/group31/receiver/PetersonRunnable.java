package com.group31.receiver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PetersonRunnable implements Runnable, ReceiverRemoteInterface {
    private final int PID;
    private Logger logger;
    private ProcessState processState;
    private String thisHost;
    private String nextHost;

    public PetersonRunnable(int pid, String thisHost, String nextHost) {
        this.PID = pid;
        this.thisHost = thisHost;
        this.nextHost = nextHost;

        logger = new Logger<String>("Delivered", pid);
        processState = new ProcessState(pid);
    }

    public void sendToNextHost(int tid) {
        try {
            Registry registry = LocateRegistry.getRegistry(nextHost);
            ReceiverRemoteInterface receiver = (ReceiverRemoteInterface) registry.lookup("process-" + nextHost);
            receiver.receiveId(tid);
        } catch (Exception e) {
            System.out.println("Exception in sending to next host");
            e.printStackTrace();
        }
    }

    @Override
    public void receiveId(int id) throws RemoteException {
        System.out.println("PID " + PID + " received " + id);

        synchronized (processState) {
            int tid = processState.receive(id);
            System.out.println("isElected: " + processState.isElected());

            if (processState.isElected()) {
                System.out.println("PID: " + PID + " got ELECTED so discarding the message");
                return;
            }

            if (tid != ProcessState.DONT_SEND) {
                sendToNextHost(tid);
            }
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Receiving Process Id");
            String name = "process-" + thisHost;
            ReceiverRemoteInterface stub = (ReceiverRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("PetersonRunnable bound");
        } catch (Exception e) {
            System.err.println("Error in receiver thread:");
            e.printStackTrace();
        }
    }
}
