package com.group31.receiver;

import com.group31.ByzantineMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReceiverRemoteInterface extends Remote {
    void receive(ByzantineMessage message) throws RemoteException;
}
