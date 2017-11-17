package com.group31.receiver;

import com.group31.Message;
import com.group31.VectorClock;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ReceiverRunnable implements Runnable, ReceiverRemoteInterface {
    int pid;
    VectorClock localClock;
    ArrayList<Message> buffer;

    public ReceiverRunnable(int pid, int totalProcessCount, VectorClock localClock) {
        this.pid = pid;
        this.localClock = localClock;
        this.buffer = new ArrayList<>();
    }

    private boolean canDeliverMessage(Message message) {
        VectorClock expectedClock = new VectorClock(localClock);
        expectedClock.increment(message.getSourcePid());

        return expectedClock.isGreaterThanOrEqualTo(message.getTimestamp());
    }

    private void deliverMessage(Message message) {
        System.out.println("Delivered: " + message + " [AT LOCAL CLOCK]: " + localClock);
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        System.out.println("Received: " + message);
        if (canDeliverMessage(message)) {
            deliverMessage(message);

            for (Message bufferedMessage : buffer) {
                if (canDeliverMessage(bufferedMessage)) {
                    deliverMessage(bufferedMessage);
                    localClock.increment(bufferedMessage.getSourcePid());
                    buffer.remove(bufferedMessage);
                }
            }
        } else {
            System.out.println("Buffering: <" + message + ">");
            buffer.add(message);
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
