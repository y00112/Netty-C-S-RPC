# Netty-C/S-RPC

>使用 Netty自定义协议 实现的了 Client-Server-Client 的即使通讯功能，以及 Rpc 远程调用协议。

目录结构

```bash 
  main
    ├─java
    │  ├─job
    │  │  └─amessage
    │  ├─rpc             // RPC远程调用
    │  │  ├─handler      // netty-rpc 请求响应处理器
    │  │  ├─message      // netty-rpc 请求响应实体
    │  │  ├─service      // 模拟第三方接口
    │  │  └─util         
    │  └─webchat         // 即使通讯
    │      ├─config      
    │      ├─handler     // netty 客户端与服务端业务处理器
    │      ├─message     // netty 客户端与服务端请求与相应消息实体
    │      ├─protocol    // netty 自定义协议，序列化技术  
    │      ├─service     // 业务处理接口
    │      └─session     // 会话管理器
    └─resources

```
### Client And Server

使用Netty实现的客户端与服务器的即时通讯系统

ChatServer.java 即时通讯服务器 ，ChartClient.java 即时通讯客户端。

使用netty 自定义协议，实现了客户端与服务器的一系列的交互

主要功能：

- 即时通讯 1 : 1
- 广播通讯 1 : n
- 即时通讯组 (n:n) (n:n) (n:n) ...
- 等等
### RPC 

RpcServer.java Rpc远程调用服务器

RpcClient.java Rpc远程调用客户端

RpcClientManager.java Rpc远程调用客户端管理器

主要解决了使用 Netty 实现一个 RPC 远程调用协议，客户端使用了代理以及双重检查锁的设计模式，通过netty 服务器使用反射机制调用第三方接口，以及返回结果。

