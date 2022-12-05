package com.wukong.nioqqclient.service;

import java.util.HashMap;

/**
 * 管理socket链接线程集合
 */
public class ManageClientConnectServerThread {

    private static HashMap<String,ClientConnectServerThread> hashMap = new HashMap<String, ClientConnectServerThread>();

    // 添加链接到线程集合
    public static void  addConnectServerTread(String userId,ClientConnectServerThread connectServerThread){
        hashMap.put(userId,connectServerThread);
    }

    // 根据userID 获得指定的线程Id
    public ClientConnectServerThread getConnectServerThread(String userId){
        return hashMap.get(userId);
    }

}
