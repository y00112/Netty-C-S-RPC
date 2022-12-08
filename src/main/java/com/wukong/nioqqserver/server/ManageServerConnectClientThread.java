package com.wukong.nioqqserver.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    /**
     * 返回在在线用户列表
     */
    public static String getOnlineUsers(){
        // 集合遍历，便利hashmap 的key
        Set<String> strings = hashMap.keySet();
        StringBuffer sb = new StringBuffer();
        for(String str :strings){
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

    /**
     * 移除socket
     */
    public static void removeSocket(String userId){
        hashMap.remove(userId);
    }
}

