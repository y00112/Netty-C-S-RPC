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
 * @content:
 */
@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        RpcMessageCodec RPC_MESSAGE_CODEC = new RpcMessageCodec();


        // rpc 相应处理器
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try{
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
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            // Rpc客户端请求服务端
            ChannelFuture channelFuture = channel.writeAndFlush(new RpcRequestMessage(1,
                    "rpc.service.HelloService",  // 第三方接口的全路径
                    "sayHello",  // 第三方接口的方法名
                    String.class, // 第三方接口的返回值类型
                    new Class[]{String.class},   //第三方接口方法的参数类型数组（有可能有多个参数）
                    new Object[]{"张三"}));

            // 异步判断结果成功与否
            channelFuture.addListener(promise->{
                if (!promise.isSuccess()) {
                    Throwable cause = promise.cause();
                    log.info("error:{}",cause);
                }
            });

            channel.closeFuture().sync();
        } catch (Exception exception) {
            exception.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
