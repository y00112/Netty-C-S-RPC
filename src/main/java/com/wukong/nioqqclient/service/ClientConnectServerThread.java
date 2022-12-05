package com.wukong.nioqqclient.service;

import com.wukong.nioqqclient.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * 线程类
 */
public class ClientConnectServerThread extends Thread{

    // 该线程需要持有socket
    private Socket socket;

    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run(){
        // 因为Thread需要在后台和服务器进行通信，因此我们需要while循环
        for(;;){
            System.out.println("客户端线程,等待读取从服务端发送的消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject(); // 如果服务器没有发送message对象，线程会阻塞


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
