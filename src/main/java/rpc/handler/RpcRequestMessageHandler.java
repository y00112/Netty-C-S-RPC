package rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import rpc.message.RpcRequestMessage;
import rpc.message.RpcResponseMessage;
import rpc.service.HelloService;
import rpc.service.ServiceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: 小山
 * @date: 2023/2/21 16:54
 * @content: Rpc 服务端的请求处理
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {

        // 1. 相应信息
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());

        try {
            HelloService service =
                    (HelloService)
                            ServiceFactory.getService(Class.forName(message.getInterfaceName()));

            // 获取对象的方法
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());

            response.setReturnValue(invoke);

        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错："+ e.getCause().getMessage()));
        }

        ctx.writeAndFlush(response);
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(1,
                "rpc.service.HelloService",  // 第三方接口的全路径
                "sayHello",  // 第三方接口的方法名
                String.class, // 第三方接口的返回值类型
                new Class[]{String.class},   //第三方接口方法的参数类型数组（有可能有多个参数）
                new Object[]{"张三"});// 第三方接口方法的参数值数组（有可能有多个参数）

        HelloService service =
                (HelloService)
                        ServiceFactory.getService(Class.forName(message.getInterfaceName()));

        // 获取对象的方法
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
