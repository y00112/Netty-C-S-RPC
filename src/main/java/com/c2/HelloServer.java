package com.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        // 1. 启动器 负责组装netty组件
        new ServerBootstrap()
                // 2. BossEventLoop WorkerEventLoop(selector,thread)
                .group(new NioEventLoopGroup())
                // 3. 选择服务器的 server socket io实现
                .channel(NioServerSocketChannel.class)
                // 4. boss负责处理连接  worker负责处理事件 决定了worker能执行哪些操作(handler)
                .childHandler(
                        // channel 代表和客户端进行数据读写的通道 Initializer 初始化，负责添加别的 handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 添加具体的handler
                        ch.pipeline().addLast(new StringDecoder()); // 将ByteBuffer 转为字符串
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){ // 自定义 handler
                            @Override
                            // 读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
