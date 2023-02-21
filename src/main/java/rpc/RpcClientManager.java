package rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import rpc.handler.RpcResponseMessageHandler;
import rpc.message.RpcMessageCodec;
import rpc.message.RpcRequestMessage;
import webchat.protocol.MessageCodec;
import webchat.protocol.ProtocolFrameDecoder;

/**
 * @author: 小山
 * @date: 2023/2/21 16:58
 * @content: Rpc 客户端管理器
 *  双重检查单例模式
 */
@Slf4j
public class RpcClientManager {

    public static void main(String[] args) {
        getChannel().writeAndFlush(new RpcRequestMessage(1,
                "rpc.service.HelloService",  // 第三方接口的全路径
                "sayHello",  // 第三方接口的方法名
                String.class, // 第三方接口的返回值类型
                new Class[]{String.class},   //第三方接口方法的参数类型数组（有可能有多个参数）
                new Object[]{"张三"}));
    }

    private static Channel channel = null;

    // 锁
    private static final Object LOCK = new Object();

    // 获取唯一的 channel 对象
    public static Channel getChannel(){
        if (channel != null){

        }

        synchronized (LOCK){
            // 如果
            if (channel != null){
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    /**
     * 初始化 channel
     */
    static void initChannel(){
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        RpcMessageCodec RPC_MESSAGE_CODEC = new RpcMessageCodec();
        // rpc 相应处理器
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(RPC_MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();

            // 异步的关闭channel
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
