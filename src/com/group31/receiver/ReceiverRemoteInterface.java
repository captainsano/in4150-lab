package com.group31.receiver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReceiverRemoteInterface extends Remote {
    /**
     * Receive the process id from the left neighbour
     * @param id
     * @throws RemoteException
     */
    void receiveId(int id) throws RemoteException;
}
