package com.wukong.nioqqserver.server;

import com.wukong.nioqqclient.common.Message;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 服务端连接客户端的线程池
 */
@AllArgsConstructor
@NoArgsConstructor
public class ServerConnectClientThread extends Thread{
    // socket
    private Socket socket;
    //
    private String userId;


    @Override
    public void run() {
        while (true){
            System.out.println("客户端和服务端通信中...");
            try {
                // 1. 获取输入输出流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 2.接收和发送消息
                Message message = (Message) ois.readObject();
                System.out.println(message.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
