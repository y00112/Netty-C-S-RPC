package webchat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import webchat.amessage.MessageA;
import webchat.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 小山
 * @date: 2023/2/20 16:15
 * @content:
 */
public class MessageCodeA extends ByteToMessageCodec<MessageA> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageA msg, ByteBuf out) throws Exception {


    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1.魔术 4
        int magicNum = in.readInt();
        // 2.唤醒头 +START   1
        byte start = in.readByte();
        // 3.通信方式 RADIO，NEED  1
        byte communicationMode = in.readByte();
        // 4.通信类型 广播，定向，其他  1
        int communicationType = in.readByte();
        // 5. 发送端姓名
        String sender = in.readBytes(new byte[3]).toString(Charset.forName("GBK"));
        // 6. 接收端姓名
        String receiver = in.readBytes(new byte[3]).toString(Charset.forName("GBK"));
        // 7. 执行端设备
        byte execute = in.readByte();
        // 8. 执行段设备 ID
        int executeId= in.readInt();
        // 9. 消息内容
        int length = in.readInt();
        // 10. 消息内容 反序列化
        byte[] bytes = new byte[length];
        ByteBuf byteBuf = in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();

        List<Map<String,Object>> mapList = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("start",start);
        hashMap.put("communicationMode",communicationMode);
        hashMap.put("communicationType",communicationType);
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("execute",execute);
        hashMap.put("executeId",executeId);
        hashMap.put("message",message);
        mapList.add(hashMap);

        out.add(mapList);
    }
}
