package com.wukong.nioqqserver.server;

import java.util.HashMap;

public class ManageServerConnectClientThread {
    private static HashMap<String,ServerConnectClientThread> hashMap = new HashMap<String, ServerConnectClientThread>();

    // 添加线程到集合
    public static void addServerConnectClientThread(String userId,ServerConnectClientThread serverConnectClientThread){
        hashMap.put(userId,serverConnectClientThread);
    }

    // 根据userId 得到线程对象
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hashMap.get(userId);
    }
}
