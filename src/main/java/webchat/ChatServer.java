package webchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import webchat.handler.*;
import webchat.protocol.MessageCodec;
import webchat.protocol.ProtocolFrameDecoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 小山
 * @date: 2023/2/17 15:57
 * @content:
 */
@Slf4j
public class ChatServer {


    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        // 业务处理器
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        LoginRequestMessageHandler LOGIN_REQUEST_HANDLER = new LoginRequestMessageHandler();
        ChartRequestMessageHandler CHART_REQUEST_HANDLER = new ChartRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler  GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast( new MessageCodec());
                    // 判断读空闲过程，或者写空闲过长
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));

                    // 处理特殊事件
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        // 用来处理 IdleStateHandler 触发的特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了读空闲事件
                            if (event.state() == IdleState.READER_IDLE) {
                                log.info("读空闲时间，已经超过了5s");
                                ctx.channel().close();
                            }
                        }
                    });
                    // 登录业务
                    ch.pipeline().addLast(LOGIN_REQUEST_HANDLER);
                    // 聊天业务
                    ch.pipeline().addLast(CHART_REQUEST_HANDLER);
                    // 拉群消息
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    // 群聊功能
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    // 加入聊天室
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    // 服务器断开
                    ch.pipeline().addLast(QUIT_HANDLER);



                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception exception) {
            exception.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
