package com.example.lenovo.wp2p.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by lenovo on 15-04-2018.
 */

public class ClientClass extends Thread{
    Socket socket;
    String hostadd;
    public ClientClass(InetAddress hostaddress){
        hostadd =hostaddress.getHostAddress();
        socket=new Socket();
    }
    @Override
    public void run(){
        try {
            socket.connect(new InetSocketAddress(hostadd,88888),500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

