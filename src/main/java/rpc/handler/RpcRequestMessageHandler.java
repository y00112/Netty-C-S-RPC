package rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import rpc.message.RpcRequestMessage;

/**
 * @author: 小山
 * @date: 2023/2/21 16:54
 * @content: Rpc 请求处理器
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequestMessage rpcRequestMessage) throws Exception {

    }

    public static void main(String[] args) {
        new RpcRequestMessage(1,
                "com.wukong.service.HelloService",  // 第三方接口的全路径
                "sayHello",  // 第三方接口的方法名
                String.class, // 第三方接口的返回值类型
                new Class[]{String.class},   //第三方接口方法的参数类型数组（有可能有多个参数）
                new Object[]{"张三"});  // 第三方接口方法的参数值数组（有可能有多个参数）
    }
}
