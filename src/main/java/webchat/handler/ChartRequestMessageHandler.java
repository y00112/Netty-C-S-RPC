package webchat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import webchat.message.ChatRequestMessage;
import webchat.message.ChatResponseMessage;
import webchat.session.SessionFactory;


/**
 * @author: 小山
 * @date: 2023/2/18
 * @content: 聊天处理器
 */
@ChannelHandler.Sharable
public class ChartRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {

        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        // 在线
        if (channel != null){
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else {
           ctx.writeAndFlush(new ChatResponseMessage(false,"对方用户不在线"));
        }


    }
}
