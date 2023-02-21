package webchat.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import webchat.session.SessionFactory;

/**
 * @author: 小山
 * @date: 2023/2/19
 * @content:
 */
@ChannelHandler.Sharable
@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 当连接断开时会触发 channelInactive 事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("{}，正常断开", ctx.channel());
    }

    // 当连接异常断开的时候，会触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("{}，异常断开，异常信息：{}", ctx.channel(),cause.getMessage());
    }
}
