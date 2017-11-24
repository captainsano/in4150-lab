package com.group31.receiver;

import com.group31.ProcessDescription;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PetersonRunnable implements Runnable, ReceiverRemoteInterface {
    private static final Integer MAX_INTERVAL = 2500;

    private ProcessState processState;
    private ProcessDescription thisProcess;
    private ProcessDescription nextProcess;
    private boolean receivedFirstEvent = false;

    public PetersonRunnable(ProcessDescription thisProcess, ProcessDescription nextProcess) {
        this.thisProcess = thisProcess;
        this.nextProcess = nextProcess;

        processState = new ProcessState(thisProcess.getPid());
    }

    private void makeRandomDelay() throws InterruptedException {
        long delay = (long) (Math.max(Math.random(), 0.1) * MAX_INTERVAL);
        System.out.println("Random delay: " + delay + "ms");
        Thread.sleep(delay);
    }

    public void sendToNextHost(int tid) {
        try {
            makeRandomDelay();
            System.out.println("Sending " + tid + " to " + nextProcess.getName() + " at " + nextProcess.getHostname());
            Registry registry = LocateRegistry.getRegistry(nextProcess.getHostname());
            ReceiverRemoteInterface receiver = (ReceiverRemoteInterface) registry.lookup("process-" + nextProcess.getName());
            receiver.receiveId(tid);
        } catch (Exception e) {
            System.out.println("Exception in sending to next host");
            e.printStackTrace();
        }
    }

    synchronized public boolean hasStartedReceiving() {
        return this.receivedFirstEvent;
    }

    @Override
    public void receiveId(int id) throws RemoteException {
        synchronized (this) {
            receivedFirstEvent = true;
        }

        System.out.println("This process " + thisProcess.getName() + " received " + id);

        int tid = processState.receive(id);
        System.out.println("isElected: " + processState.isElected());

        if (processState.isElected()) {
            System.out.println("Process " + thisProcess.getName() + " got ELECTED with ID: " + thisProcess.getPid());
            return;
        }

        if (tid != ProcessState.DONT_SEND) {
            sendToNextHost(tid);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Receiving Process Id");
            String name = "process-" + thisProcess.getName();
            ReceiverRemoteInterface stub = (ReceiverRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(thisProcess.getHostname());
            registry.rebind(name, stub);
            System.out.println("PetersonRunnable bound");
        } catch (Exception e) {
            System.err.println("Error in receiver thread:");
            e.printStackTrace();
        }
    }
}
