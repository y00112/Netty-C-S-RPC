package com.wukong.nioqqserver.server;

import com.wukong.nioqqclient.common.Message;
import com.wukong.nioqqclient.common.MessageType;
import com.wukong.nioqqclient.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * QQ 服务端
 */
public class QQServer {
    private ServerSocket serverSocket = null;

    public QQServer(){
        try {
            while (true){
                System.out.println("服务端子啊9999端口监听...");
                // 1. 服务端一直监听客户端的信息
                Socket socket = serverSocket.accept();
                // 2. 得到输入流和输出流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 3. 读取消息
                User user = (User) ois.readObject();
                // 4. 登录校验
                if (user.getUserId().equals("100") && user.getPasswd().equals("123456")){
                    // 5. 登录成功
                    // 5.1 创建应该线程用来保持链接
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                    // 5.2 启动线程
                    serverConnectClientThread.start();
                    // 5.3 添加线程到集合
                    ManageServerConnectClientThread.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);
                }else {
                    //6. 登录失败
                    Message message = new Message();
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 释放流
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
