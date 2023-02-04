package com.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
@Slf4j
public class Server3 {

    // 指定的分隔符
    public static final String DELIMITER = "#";

    public static void main(String[] args) {
        // 连接线程
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // 处理事件的线程
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            // 调整系统的接收缓冲器（滑动窗口）
//            serverBootstrap.option(ChannelOption.SO_RCVBUF,10);
            // 调成 netty 的接收缓冲区（byteBuffer）
            serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16));
            serverBootstrap.group(boss,worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 换行符分割
//                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));

                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(
                            1024,
                            Unpooled.wrappedBuffer(DELIMITER.getBytes())) // 分割符缓冲对象
                    );
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    // 添加具体的Handler
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        // 建立连接时会执行
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.info("connected {}", ctx.channel());
                            super.channelActive(ctx);
                        }

                        // 读事件
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("读事件运行.....");

                            Thread.sleep(8000);
                        }

                        // 断开连接的时候会执行
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.info("disconnect {}", ctx.channel());
                            super.channelInactive(ctx);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("server error",e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 转化十六进制编码为字符串
     *
     * @param s
     * @return 字符串
     */

    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "GBK");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}
