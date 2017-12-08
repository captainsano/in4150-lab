package com.group31.receiver;

import com.group31.BroadcastRunnable;
import com.group31.ByzantineMessage;
import com.group31.ProcessDescription;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HonestByzantineRunnable implements Runnable, ReceiverRemoteInterface {
    public static String NOTIFICATION_PHASE = "N";
    public static String PROPOSAL_PHASE = "P";

    private String phase = NOTIFICATION_PHASE;
    private int round = 1;
    private int v = 0;
    private boolean decided = false;
    private ProcessDescription thisProcess;
    private ArrayList<ProcessDescription> allProcesses;
    private double n;
    private double f;

    private ArrayList<ByzantineMessage> buffer = new ArrayList<>();
    private HashMap<ProcessDescription, Integer> messages;

    public HonestByzantineRunnable(ArrayList<ProcessDescription> allProcesses, ProcessDescription thisProcess) {
        this.thisProcess = thisProcess;
        this.allProcesses = allProcesses;
        n = allProcesses.size();
        f = n / 5.0;
    }

    private int generateRandomBinary() {
        return ((int) (Math.random() * 10) % 2);
    }

    private int generateRandomNonBinary() {
        return ((int) (Math.random() * 10) + 2);
    }

    private void broadcast(int w) {
        ByzantineMessage message = new ByzantineMessage(thisProcess, phase, round, w);
//        System.out.println("Broadcasting: " + message);
        new Thread(new BroadcastRunnable(message, allProcesses)).start();
    }

    synchronized private boolean canDeliverMessage(ByzantineMessage message) {
        return phase.equals(message.getPhase()) && round == message.getRound();
    }

    synchronized private void checkBuffer() {
        Iterator<ByzantineMessage> iter = buffer.iterator();
        while (iter.hasNext()) {
            ByzantineMessage message = iter.next();
            if (canDeliverMessage(message)) {
                iter.remove();
                deliver(message);
            }
        }
    }

    synchronized public void deliver(ByzantineMessage message) {
        if (decided) {
            return;
        }

//        System.out.println("-----> Delivering ");
//        System.out.println(message);
//        System.out.println("------------------");

        // Notification Phase: Await n - f messages of the form (N; r, *)
        if (phase.equals(NOTIFICATION_PHASE)) {
            if (message.getRound() == round && message.getPhase().equals(phase)) {
                messages.put(message.getSender(), message.getW());
            }

            if (messages.size() >= (n - f)) {
                phase = PROPOSAL_PHASE;

                double count0 = messages.values().stream().filter(value -> value == 0).count();
                double count1 = messages.values().stream().filter(value -> value == 1).count();

                System.out.println("NOTIFICATION; " +  "Round: " + round + " Count0: " + count0 + " Count1: " + count1);

                if (count0 > ((n + f) / 2.0)) {
                    broadcast(0);
                } else if (count1 > ((n + f) / 2.0)) {
                    broadcast(1);
                } else {
                    broadcast(generateRandomNonBinary());
                }

                // clear message buffer for next phase
                messages = new HashMap<>();
            }
        }

        // Proposal Phase: Await n - f messages of the form (P; r, *)
        if (!decided && phase.equals(PROPOSAL_PHASE)) {
            if (message.getRound() == round && message.getPhase().equals(phase)) {
                messages.put(message.getSender(), message.getW());
            }

            if (messages.size() >= (n - f)) {
                double count0 = messages.values().stream().filter(value -> value == 0).count();
                double count1 = messages.values().stream().filter(value -> value == 1).count();

                System.out.println("PROPOSAL; " +  "Round: " + round + " Count0: " + count0 + " Count1: " + count1);

                if (count0 > f || count1 > f) {
                    if (count0 > f) v = 0;
                    if (count1 > f) v = 1;

                    if (count0 > 3 * f || count1 > 3 * f) {
                        decided = true;
                    }
                } else {
                    v = generateRandomBinary();
                }

                // Broadcast value for the next round
                phase = NOTIFICATION_PHASE;
                round = round + 1;
                messages = new HashMap<>();
                broadcast(v);

                if (decided) {
                    System.out.println("----- Decided value: " + v);
                }
            }
        }

        checkBuffer();
    }

    @Override
    public void receive(ByzantineMessage message) throws RemoteException {
        synchronized (this) {
            if (!decided) {
                if (canDeliverMessage(message)) {
                    deliver(message);
                } else {
//                    System.out.println("-----> Buffering ");
//                    System.out.println(message);
//                    System.out.println("-----------------");
                    buffer.add(message);
                }

                checkBuffer();
            }
        }
    }

    private void kickstart() {
        synchronized (this) {
            round = 1;
            decided = false;
            v = generateRandomBinary();
            phase = NOTIFICATION_PHASE;

            if (!decided) {
                // reset message buffer
                messages = new HashMap<>();

                // Broadcast (N; r, v)
                broadcast(v);
            } else {
                System.out.println("Decided on value " + v);
            }
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
            Thread.sleep(10000);
            kickstart();
        } catch (Exception e) {
            System.out.println("Exception in receiver binding");
            e.printStackTrace();
        }
    }
}
