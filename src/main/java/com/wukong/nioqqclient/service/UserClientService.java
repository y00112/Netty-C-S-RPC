package com.wukong.nioqqclient.service;

import com.wukong.nioqqclient.common.Message;
import com.wukong.nioqqclient.common.MessageType;
import com.wukong.nioqqclient.common.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 完成用户的登陆以及注册等功能
 */
public class UserClientService {

    private User user = new User();

    private Socket socket;

    // 根据userId 和 pwd 到服务器验证该用户是否合法
    public boolean checkUser(String userId,String pwd)  {
        // 创建User对象
        user.setUserId(userId);
        user.setPasswd(pwd);

        boolean flag = false;

        // 链接到服务器，发送uers 对象
        try {
            // 创建socket
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            // 发送对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            // 读取服务端回复的message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            // 登录成功。。
            if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){

                // 创建一个和服务器保持通信的线程 -> 创建一个类 ClientConnectServerThread
                ClientConnectServerThread connectServerThread = new ClientConnectServerThread(socket);
                // 启动客户端的线程
                connectServerThread.start();
                // 为了后面客户端的扩展，我们将线程放入到客户端管理
                ManageClientConnectServerThread.addConnectServerTread(userId,connectServerThread);
                flag = true;
            }else {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return flag;
    }
}
