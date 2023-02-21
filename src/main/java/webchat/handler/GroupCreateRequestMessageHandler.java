package webchat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import webchat.message.GroupCreateRequestMessage;
import webchat.message.GroupCreateResponseMessage;
import webchat.session.Group;
import webchat.session.GroupSession;
import webchat.session.GroupSessionFactory;

import java.util.List;
import java.util.Set;

/**
 * @author: 小山
 * @date: 2023/2/18
 * @content: 创建群聊处理器
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);

        if (group == null){
            // 给创建这发送成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建成功...."));
            // 发送给群成员拉群的消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for(Channel channel : channels){
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入"+ groupName));
            }
        }else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName + "已经存在"));
        }
    }
}
