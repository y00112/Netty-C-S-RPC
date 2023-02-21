package rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import rpc.message.RpcResponseMessage;

/**
 * @author: 小山
 * @date: 2023/2/21 17:00
 * @content: 客户端的响应处理
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage msg) throws Exception {
        log.info("{}",msg);
    }
}
