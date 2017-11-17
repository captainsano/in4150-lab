package com.group31.receiver;

import com.group31.Message;
import com.group31.VectorClock;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class ReceiverRunnable implements Runnable, ReceiverRemoteInterface {
    private final int pid;
    private VectorClock localClock;
    private ArrayList<Message> buffer;

    public ReceiverRunnable(int pid, VectorClock localClock) {
        this.pid = pid;
        this.localClock = localClock;
        this.buffer = new ArrayList<>();
    }

    synchronized private boolean canDeliverMessage(Message message) {
        VectorClock expectedClock = new VectorClock(localClock);
        expectedClock.increment(message.getSourcePid());

        boolean ret = expectedClock.isGreaterThanOrEqualTo(message.getTimestamp());

        System.out.println("Expected: " + expectedClock + " GE? Message: " + message.getTimestamp() + " " + ret);

        return ret;
    }

    synchronized private void deliverMessage(Message message) {
        System.out.println("Delivered: " + message);
    }

    @Override
    synchronized public void receiveMessage(Message message) throws RemoteException {
        System.out.println("Received: " + message);
        synchronized (localClock) {
            if (canDeliverMessage(message)) {
                deliverMessage(message);
                localClock.increment(message.getSourcePid());

                Iterator<Message> iter = buffer.iterator();
                while (iter.hasNext()) {
                    Message bufferedMessage = iter.next();

                    if (canDeliverMessage(bufferedMessage)) {
                        deliverMessage(bufferedMessage);
                        localClock.increment(bufferedMessage.getSourcePid());
                        iter.remove();
                    }
                }
            } else {
                System.out.println("Buffering: <" + message + ">");
                buffer.add(message);
            }
        }
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
