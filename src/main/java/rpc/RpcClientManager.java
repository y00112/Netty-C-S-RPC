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
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import rpc.handler.RpcResponseMessageHandler;
import rpc.message.RpcMessageCodec;
import rpc.message.RpcRequestMessage;
import rpc.service.HelloService;
import rpc.util.SequenceIdGenerator;
import webchat.protocol.MessageCodec;
import webchat.protocol.ProtocolFrameDecoder;

import java.lang.reflect.Proxy;

/**
 * @author: 小山
 * @date: 2023/2/21 16:58
 * @content: Rpc 客户端管理器
 *  双重检查锁 -单例模式
 */
@Slf4j
public class RpcClientManager {

    public static void main(String[] args) {
        HelloService proxyService = getProxyService(HelloService.class);
        String xiaoming = proxyService.sayHello("小明");
        System.out.println(xiaoming);
        proxyService.sayHello("小王");
        proxyService.sayHello("小李");
        log.info("main");



    }

    // 代理类 可以代理任何接口类型
    public static <T> T getProxyService(Class<T> serviceClass){
        // 类加载器
        ClassLoader loader = serviceClass.getClassLoader();
        // 代理类要实现的接口
        Class[] interfaces = new Class[]{serviceClass};

        int sequenceId = SequenceIdGenerator.nextId();

        Object o = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),  // 第三方接口的全路径
                    method.getName(),  // 第三方接口的方法名
                    method.getReturnType(), // 第三方接口的返回值类型
                    method.getParameterTypes(),   //第三方接口方法的参数类型数组（有可能有多个参数）
                    args);
            // 2. 将消息发送出去
            getChannel().writeAndFlush(message);
            // 3. 准备一个Promise对象，来接收结果                  指定 promise 对象接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISE_MAP.put(sequenceId,promise);

            // 4. 等待 promise 结果
            promise.await();
            if (promise.isSuccess()){
                // 结果
                return promise.getNow();
            }else {
                throw new RuntimeException( promise.cause());
            }

        }));

        return (T) o;
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
