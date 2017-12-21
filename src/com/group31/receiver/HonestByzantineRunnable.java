package com.group31.receiver;

import com.group31.BroadcastRunnable;
import com.group31.ByzantineMessage;
import com.group31.ProcessDescription;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

public class HonestByzantineRunnable implements Runnable, ReceiverRemoteInterface {
    public static String NOTIFICATION_PHASE = "N";
    public static String PROPOSAL_PHASE = "P";

    private String phase = NOTIFICATION_PHASE;
    private int round = 1;
    private int v = 0;
    private boolean decided = false;
    private ProcessDescription thisProcess;
    private ArrayList<ProcessDescription> allProcesses;
    private Integer n;
    private Double f;

    private ArrayList<ByzantineMessage> buffer = new ArrayList<>();

    public HonestByzantineRunnable(ArrayList<ProcessDescription> allProcesses, ProcessDescription thisProcess) {
        this.thisProcess = thisProcess;
        this.allProcesses = allProcesses;
        n = allProcesses.size();
        f = 0.2 * n; // 1/5 of n
    }

    // ? - Randomly choose some value, > 1
    private int generateRandomProposal() {
        return new Random().nextInt(100) + 2;
    }

    private int generateRandomBinary() {
        return ((int) (Math.random() * 10) % 2);
    }

    private void broadcast(int w) {
        ByzantineMessage message = new ByzantineMessage(thisProcess, phase, round, w);
        System.out.println(message.getRound() + message.getPhase() + " " + message.getSender().getPid() + " " + message.getW());
        new Thread(new BroadcastRunnable(thisProcess, message, allProcesses)).start();
    }

    @Override
    public void receive(ByzantineMessage message) {
        synchronized (this) {
            buffer.add(message);
        }
    }

    private void await() {
        while (true) {
            synchronized (this) {
                long count = buffer.stream().filter(m -> m.getPhase().equals(this.phase) && m.getRound() == this.round).count();

                if (count >= Math.floor(n - f)) {
                    break;
                }
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Exception in await");
                e.printStackTrace();
            }
        }
    }

    private void kickstart() {
        synchronized (this) {
            round = 1;
            decided = false;
            v = generateRandomBinary();
            phase = NOTIFICATION_PHASE;
        }

        while (!decided) {
            // Broadcast (N; r, v)
            broadcast(v);

            // Notification Phase: Await n - f messages of the form (N; r, *)
            await();
            synchronized (this) {
                if (phase.equals(NOTIFICATION_PHASE)) {
                    long count0 = buffer.stream().filter(m -> m.getPhase().equals(this.phase) && m.getRound() == this.round).filter(m -> m.getW() == 0).count();
                    long count1 = buffer.stream().filter(m -> m.getPhase().equals(this.phase) && m.getRound() == this.round).filter(m -> m.getW() == 1).count();

//                    System.out.println("Notification: " + " count0: " + count0 + " count1: " + count1);

                    phase = PROPOSAL_PHASE;
                    if (count0 > Math.floor((n + f) * 0.5)) {
                        broadcast(0);
                    } else if (count1 > Math.floor((n + f) * 0.5)) {
                        broadcast(1);
                    } else {
                        broadcast(generateRandomProposal());
                    }
                }
            }

            // Proposal Phase: Await n - f messages of the form (P; r, *)
            await();
            synchronized (this) {
                if (phase.equals(PROPOSAL_PHASE) && !decided) {
                    long count0 = buffer.stream().filter(m -> m.getPhase().equals(this.phase) && m.getRound() == this.round).filter(m -> m.getW() == 0).count();
                    long count1 = buffer.stream().filter(m -> m.getPhase().equals(this.phase) && m.getRound() == this.round).filter(m -> m.getW() == 1).count();

                    if (count0 > Math.floor(f) || count1 > Math.floor(f)) {
//                        System.out.println("Proposal: " + " count0: " + count0 + " count1: " + count1);
                        v = count0 > Math.floor(f) ? 0 : 1;
                        if (count0 > 3 * Math.floor(f) || count1 > 3 * Math.floor(f)) {
                            decided = true;
                        }
                    } else {
                        v = generateRandomBinary();
                    }

                    // Broadcast value for the next round
                    phase = NOTIFICATION_PHASE;
                    round = round + 1;
                }
            }
        }

        System.out.println("PID " + thisProcess.getPid() + " Decided value: " + v);
    }

    @Override
    public void run() {
        try {
            // Bind receiver to registry
            ReceiverRemoteInterface stub = (ReceiverRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(thisProcess.getHostname());
            registry.rebind("process-" + thisProcess.getPid(), stub);
            System.out.println("PID " + thisProcess.getPid() + " Receiver bound");

            // Initial delay to wait for other processes
            Thread.sleep(10000);
            kickstart();
        } catch (Exception e) {
            System.out.println("Exception in receiver binding");
            e.printStackTrace();
        }
    }
}
