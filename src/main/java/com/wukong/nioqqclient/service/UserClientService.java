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

    private static User user = new User();

    private static Socket socket;

    // 根据userId 和 pwd 到服务器验证该用户是否合法
    public static boolean checkUser(String userId,String pwd)  {
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

    /**
     * 获取在线用户列表
     */
    public static void getOnlineUsers(){
        // 1.想服务端发送一个message消息，类型为：MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setGetter(user.getUserId());
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        try {
        // 2. 发送给服务端
            ClientConnectServerThread connectServerThread =  ManageClientConnectServerThread.getConnectServerThread(user.getUserId());
            Socket socket = connectServerThread.getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // 退出系统
    public void logout() {
        try {
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
            message.setSender(user.getUserId()); // 指定要退出的客户端
            // 发送socket
            Socket socket = ManageClientConnectServerThread.getConnectServerThread(user.getUserId()).getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println(user.getUserId() + "退出了系统...");
            System.exit(0); // 结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
