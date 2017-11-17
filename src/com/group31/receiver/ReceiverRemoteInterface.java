package com.group31.receiver;

import com.group31.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReceiverRemoteInterface extends Remote {
    public void receiveMessage(Message message) throws RemoteException;
}
