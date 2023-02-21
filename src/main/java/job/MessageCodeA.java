package job;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import job.amessage.MessageA;
import webchat.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author: 小山
 * @date: 2023/2/20 16:15
 * @content:
 */
public class MessageCodeA extends ByteToMessageCodec<MessageA> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageA msg, ByteBuf out) throws Exception {
        //1. 魔术 4
        out.writeBytes(new byte[]{1,2,3,4});

        //3. 通信方式 1
        out.writeByte(msg.getMessageType());

        //9. 消息序列化
        byte[] bytes = msg.toString().getBytes(Charset.forName("GBK"));
        //10. 消息长度
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1.魔术 4
        int magicNum = in.readInt();

        // 3.通信方式 RADIO，NEED  1
        byte communicationMode = in.readByte();

        // 9. 消息内容
        int length = in.readInt();
        // 10. 消息内容 反序列化
        byte[] bytes = new byte[length];
        ByteBuf byteBuf = in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();

//        List<Map<String,Object>> mapList = new ArrayList<>();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("start",start);
//        hashMap.put("communicationMode",communicationMode);
//        hashMap.put("communicationType",communicationType);
//        hashMap.put("sender",sender);
//        hashMap.put("receiver",receiver);
//        hashMap.put("execute",execute);
//        hashMap.put("executeId",executeId);
//        hashMap.put("message",message);
//        mapList.add(hashMap);

        out.add(message);
    }
}
