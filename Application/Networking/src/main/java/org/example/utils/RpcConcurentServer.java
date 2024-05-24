package org.example.utils;


import org.example.Service;
import org.example.rpcprotocol.ClientRpcReflectionWorker;

import java.net.Socket;

public class RpcConcurentServer extends  AbsConcurentServer{

    private Service chatServer;
    public RpcConcurentServer(int port, Service chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- RpcConcurrentServer");
    }


    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcReflectionWorker worker=new ClientRpcReflectionWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }
}
