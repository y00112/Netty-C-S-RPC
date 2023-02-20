package webchat.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import webchat.ChatServer;
import webchat.message.LoginRequestMessage;
import webchat.message.LoginResponseMessage;
import webchat.server.service.UserService;
import webchat.server.service.UserServiceFactory;
import webchat.server.service.UserServiceMemoryImpl;
import webchat.server.session.SessionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 小山
 * @date: 2023/2/18
 * @content: 登录处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        // 业务处理
        log.info("username: {},password: {}", msg.getUsername(), msg.getPassword());

        UserService userService = new UserServiceMemoryImpl();
        boolean login = userService.login(msg.getUsername(), msg.getPassword());
        LoginResponseMessage message;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), msg.getUsername());
            message = new LoginResponseMessage(true, "登录成功...");
        } else {
            message = new LoginResponseMessage(false, "登录失败,用户名或者密码错误...");
        }
        ctx.writeAndFlush(message);

    }
}
