package com.example.lenovo.wp2p.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lenovo on 15-04-2018.
 */

public class ServerClass extends Thread{
    Socket socket;
    ServerSocket serverSocket;
    @Override
    public void run(){
        try {
            serverSocket=new ServerSocket(88888);
            socket=serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

