package com.wukong.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端逻辑处理类
 */
@ChannelHandler.Sharable  // Sharable 主要是为了多个handler可以被多个channel安全的共享 保证线程的安全
public class ClientHandler extends SimpleChannelInboundHandler<String> {

  /*
     ChannelInboundHandlerAdapter 或SimpleChannelInboundHandler类，在这里顺便说下它们两的区别吧。
      继承SimpleChannelInboundHandler类之后，会在接收到数据后会自动release掉数据占用的Bytebuffer资源。并且继承该类需要指定数据格式。
       而继承ChannelInboundHandlerAdapter则不会自动释放，需要手动调用ReferenceCountUtil.release()等方法进行释放。继承该类不需要指定数据格式。
      所以在这里，个人推荐服务端继承ChannelInboundHandlerAdapter，手动进行释放，防止数据未处理完就自动释放了。而且服务端可能有多个客户端进行连接，
        并且每一个客户端请求的数据格式都不一致，这时便可以进行相应的处理。
        客户端根据情况可以继承SimpleChannelInboundHandler类。好处是直接指定好传输的数据格式，就不需要再进行格式的转换了。
   */

    /**
     * 打印读取到的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        System.out.println(msg);
    }

    //异常数据捕获
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
