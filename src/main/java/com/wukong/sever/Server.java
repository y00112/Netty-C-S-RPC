package com.wukong.sever;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 服务端启动类
 */
public class Server {
    public static void main(String[] args) {
        // configure the server
        // 创建两个EventLoopGroup对象
        // 创建boss现场组，用户服务的接收客户端的链接
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        // 创建 worker 线程组 用于进行 SocketChannel的数据读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            // 创建 ServerBootStrap 对象
            ServerBootstrap b = new ServerBootstrap();
            // 设置使用EventLoopGroup
            b.group(boosGroup,workerGroup)
                    // 设置要被实例化的NioServerSocketChannel 类
                    .channel(NioServerSocketChannel.class)
                    // 设置NioServerSocketChannel 的处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置连入服务端的 client 的socket Channel的处理器
                    .childHandler(new ServerInitializer());
            // 绑定端口，并同步灯带成功，几启动服务端
            ChannelFuture f = b.bind(8888);
            // 监听服务端关闭，并阻塞等待
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 关闭两个 Event Loop Group对象
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
