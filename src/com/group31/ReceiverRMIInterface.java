package com.group31;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface ReceiverRMIInterface extends Remote {
    public void receiveMessage(int sourcePid, String message, VectorClock timestamp) throws RemoteException;
}
