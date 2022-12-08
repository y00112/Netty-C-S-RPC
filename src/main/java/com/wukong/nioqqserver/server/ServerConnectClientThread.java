package com.wukong.nioqqserver.server;

import com.wukong.nioqqclient.common.Message;
import com.wukong.nioqqclient.common.MessageType;
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
            System.out.println("服务端和客户端"+userId+"通信中...");
            try {
                // 1. 获取输入输出流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                // 2.接收和发送消息
                Message message = (Message) ois.readObject();
                // 后面会使用到message，根据message的类型，做相应的业务处理
                if (MessageType.MESSAGE_GET_ONLINE_FRIEND.equals(message.getMesType())){
                    System.out.println(message.getGetter() + "请求在线用户列表");
                    // 1.拉去在线用户列表
                    String onlineUsers = ManageServerConnectClientThread.getOnlineUsers();
                    Message returnMsg = new Message();
                    returnMsg.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    returnMsg.setContent(onlineUsers);
                    returnMsg.setGetter(message.getGetter());
                    oos.writeObject(returnMsg );
                }else if (MessageType.MESSAGE_CLIENT_EXIT.equals(message.getMesType())){
                    // 退出
                    System.out.println("用户："+ userId + "退出了系统...");
                    ManageServerConnectClientThread.removeSocket(userId);
                    socket.close();
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
