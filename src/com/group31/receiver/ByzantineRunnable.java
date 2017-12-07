package com.group31.receiver;

import com.group31.BroadcastRunnable;
import com.group31.ByzantineMessage;
import com.group31.ByzantineProcessDescription;
import com.group31.ProcessDescription;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class ByzantineRunnable implements Runnable, ReceiverRemoteInterface {
    public static String NOTIFICATION_PHASE = "n";
    public static String PROPOSAL_PHASE = "P";

    private static final Integer MAX_INTERVAL = 2500;

    private String phase = NOTIFICATION_PHASE;
    private int round = 1;
    private int v = 0;
    private boolean decided = false;
    private ByzantineProcessDescription thisProcess;
    private ArrayList<ProcessDescription> allProcesses;
    private Integer n;
    private Integer f;

    private HashMap<ProcessDescription, Integer> messageBuffer; // Count down from (n - f)

    public ByzantineRunnable(ArrayList<ProcessDescription> allProcesses, ByzantineProcessDescription thisProcess) {
        this.thisProcess = thisProcess;
        this.allProcesses = allProcesses;
        n = allProcesses.size();
        f = (int) Math.floor(0.2 * allProcesses.size());
    }

    // ? - Randomly choose 0 or 1
    private int generateRandomW() {
        return ((int) (Math.random() * 10) % 2);
    }

    private void broadcast(int w) {
        ByzantineMessage message = new ByzantineMessage(thisProcess, phase, round, w);
        new Thread(new BroadcastRunnable(message, allProcesses)).start();
    }

    @Override
    public void receive(ByzantineMessage message) throws RemoteException {
        synchronized (this) {
            if (decided) {
                System.out.println("Decided so STOPPED");
            }

            // Notification Phase: Await n - f messages of the form (N; r, *)
            if (phase.equals(NOTIFICATION_PHASE)) {
                if (message.getRound() == round && message.getPhase().equals(phase)) {
                    messageBuffer.put(message.getSender(), message.getW());
                }

                if (messageBuffer.size() >= (n - f)) {
                    phase = PROPOSAL_PHASE;

                    long count0 = messageBuffer.values().stream().filter(value -> value == 0).count();
                    long count1 = n - count0;

                    if (count0 > ((n + f) / 2)) {
                        broadcast(0);
                    } else if (count1 > ((n + f) / 2)) {
                        broadcast(1);
                    } else {
                        broadcast(generateRandomW());
                    }

                    // clear message buffer for next phase
                    messageBuffer = new HashMap<>();
                }
            }

            // Proposal Phase: Await n - f messages of the form (P; r, *)
            if (phase.equals(PROPOSAL_PHASE) && !decided) {
                if (message.getRound() == round && message.getPhase().equals(phase)) {
                    messageBuffer.put(message.getSender(), message.getW());
                }

                if (messageBuffer.size() >= (n - f)) {
                    long count0 = messageBuffer.values().stream().filter(value -> value == 0).count();
                    long count1 = n - count0;

                    if (count0 > f || count1 > f) {
                        v = count0 > f ? 0 : 1;
                        if (count0 > 3 * f || count1 > 3 * f) {
                            decided = true;
                            System.out.println("----- Decided value: " + v);
                        }
                    } else {
                        v = generateRandomW();
                    }

                    if (!decided) {
                        phase = NOTIFICATION_PHASE;
                        round = round + 1;
                        messageBuffer = new HashMap<>();
                        broadcast(v);
                    }
                }
            }
        }
    }

    private void kickstart() {
        synchronized (this) {
            round = 1;
            decided = false;
            v = generateRandomW();
            phase = NOTIFICATION_PHASE;

            if (!decided) {
                // reset message buffer
                messageBuffer = new HashMap<>();

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
        } catch (Exception e) {
            System.out.println("Exception in receiver binding");
            e.printStackTrace();
        }

        kickstart();
    }
}
