package com.group31;

import java.rmi.RemoteException;

class RMIReceiver implements ReceiverRMIInterface {
    private int pid;
    private int totalProcessCount;
    private VectorClock vectorClock;

    RMIReceiver() {
        super();
    }

    RMIReceiver(int pid, int totalProcessCount, VectorClock vectorClock) {
        super();
        this.pid = pid;
        this.totalProcessCount = totalProcessCount;
        this.vectorClock = vectorClock;
    }

    @Override
    public void receiveMessage(int sourcePid, String message, VectorClock timestamp) throws RemoteException {
        System.out.println("Receiving message from " + sourcePid + ", " + message + " at " + timestamp);
    }
}
