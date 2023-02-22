package rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import rpc.message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 小山
 * @date: 2023/2/21 17:00
 * @content: 客户端的响应处理
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    //                        序号     异步接收结果
    public static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage msg) throws Exception {
        log.info("{}",msg);
        // 拿到 promise
        Promise<Object> promise = PROMISE_MAP.get(msg.getSequenceId());

        if (promise != null) {
            // 正常
            Object returnValue = msg.getReturnValue();

            // 异常
            Exception exceptionValue = msg.getExceptionValue();

            if (exceptionValue != null){
                promise.setFailure(exceptionValue);
            }else{
                promise.setSuccess(returnValue);

            }
        }

    }
}
