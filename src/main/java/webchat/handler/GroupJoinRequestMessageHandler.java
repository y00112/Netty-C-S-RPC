package webchat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import webchat.message.GroupJoinRequestMessage;
import webchat.message.GroupJoinResponseMessage;
import webchat.server.session.Group;
import webchat.server.session.GroupSessionFactory;
import webchat.server.session.SessionFactory;

/**
 * @author: 小山
 * @date: 2023/2/19
 * @content: 加入聊天室
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());
        if (group != null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true,"你已经成功加入聊天室："+msg.getGroupName()));
        }else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false,"加入失败："+msg.getGroupName()));

        }
    }
}
