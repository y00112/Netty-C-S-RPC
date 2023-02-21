package webchat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import webchat.message.GroupChatRequestMessage;
import webchat.message.GroupChatResponseMessage;
import webchat.session.GroupSessionFactory;

import java.util.List;

/**
 * @author: 小山
 * @date: 2023/2/18
 * @content:
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {

        List<Channel> channels = GroupSessionFactory.getGroupSession()
                .getMembersChannel(msg.getGroupName());
        for (Channel channel:channels) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent()));
        }
    }
}
