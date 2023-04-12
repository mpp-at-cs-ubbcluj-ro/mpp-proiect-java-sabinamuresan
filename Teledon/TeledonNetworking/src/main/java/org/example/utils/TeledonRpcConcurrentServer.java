package org.example.utils;

import org.example.rpcprotocol.ClientRpcWorker;
import org.example.services.IService;

import java.net.Socket;

public class TeledonRpcConcurrentServer extends AbsConcurrentServer{
    private IService teledonServer;

    public TeledonRpcConcurrentServer(int port, IService teledonServer) {
        super(port);
        this.teledonServer = teledonServer;
        System.out.println("Teledon - TeledonRpcConcurrentServer");
    }

    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker = new ClientRpcWorker(this.teledonServer, client);
        Thread tw = new Thread(worker);
        return tw;
    }

    public void stop() {
        System.out.println("Stopping services ...");
    }
}
