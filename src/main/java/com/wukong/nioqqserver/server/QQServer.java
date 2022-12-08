package com.wukong.nioqqserver.server;

import com.wukong.nioqqclient.common.Message;
import com.wukong.nioqqclient.common.MessageType;
import com.wukong.nioqqclient.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * QQ 服务端
 */
public class QQServer {
    private ServerSocket serverSocket = null;

    // 创建一个集合存放多个用户，如果是这些用户登陆，就认为是合法的,ConcurrentHashMap 线程安全的
    private static ConcurrentHashMap<String,User> validUsers = new ConcurrentHashMap<String, User>();
    static {
        validUsers.put("zhubajie",new User("zhubajie","123456"));
        validUsers.put("sunwukong",new User("sunwukong","123456"));
        validUsers.put("bailongma",new User("bailongma","123456"));
        validUsers.put("400",new User("400","123456"));
        validUsers.put("500",new User("500","123456"));
        validUsers.put("600",new User("600","123456"));

    }

    public QQServer(){
        try {
            System.out.println("服务端正在监听9999端口...");
            serverSocket = new ServerSocket(9999);
            while (true){
                // 1. 服务端一直监听客户端的信息
                Socket socket = serverSocket.accept();
                // 2. 得到输入流和输出流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 3. 读取消息
                User user = (User) ois.readObject();
                // 4. 登录校验
                if (checkUser(user)){
                    // 5. 登录成功
                    Message message = new Message();
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    // 5.1 创建应该线程用来保持链接
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                    // 5.2 启动线程
                    serverConnectClientThread.start();
                    // 5.3 添加线程到集合
                    ManageServerConnectClientThread.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);
                }else {
                    //6. 登录失败
                    System.out.println("用户 id = " + user.getUserId() + "登录失败！");
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

    // 校验用户
    private boolean checkUser(User user) {
        User u = validUsers.get(user.getUserId());
        if (u == null){
            return false;
        }else if (!u.getPasswd().equals(user.getPasswd())){
            return false;
        }
        return true;

    }
}
