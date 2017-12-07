package com.group31.receiver;

import com.group31.ByzantineMessage;
import com.group31.ByzantineProcessDescription;
import com.group31.ProcessDescription;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class ByzantineRunnable implements Runnable, ReceiverRemoteInterface {
    public static String NOTIFICATION_PHASE = "N";
    public static String PROPOSAL_PHASE = "P";

    private static final Integer MAX_INTERVAL = 2500;

    private String phase = NOTIFICATION_PHASE;
    private int round = 1;
    private int w = 0;
    private boolean decided = false;
    private ByzantineProcessDescription thisProcess;
    private ArrayList<ProcessDescription> allProcesses;

    private HashMap<ProcessDescription, Integer> messageBuffer; // Count down from (n - f)

    // Randomly choose 0 or 1
    private int generateRandomW() {
        // TODO: Implement this
        return -1;
    }

    public ByzantineRunnable(ArrayList<ProcessDescription> allProcesses, ByzantineProcessDescription thisProcess) {
        this.thisProcess = thisProcess;
        this.allProcesses = allProcesses;
    }

    @Override
    public void receive(ByzantineMessage message) throws RemoteException {
        // TODO: the logic for byzantine
    }

    @Override
    public void run() {
        // TODO: initial broadcast
    }
}
