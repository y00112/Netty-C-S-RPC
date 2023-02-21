# Netty-C/S-RPC
目录结构

```bash
    ├─com            
    │  └─wukong
    │      └─service // RPC模拟第三方接口
    ├─job            // 工作需求，待完善
    │  └─amessage
    ├─rpc            //RPC
    │  ├─handler
    │  └─message
    └─webchat         // 即使通讯
        ├─config      
        ├─handler     // netty 客户端与服务端业务处理器
        ├─message     // netty 客户端与服务端请求与相应消息实体
        ├─protocol    // netty 自定义协议，序列化技术  
        ├─service     // 业务处理接口
        └─session     // 会话管理器
```
### Client And Server

使用Netty实现的客户端与服务器的即时通讯系统

ChatServer.java 即时通讯服务器 ，ChartClient.java 即时通讯客户端。

### RPC 
完善中...

