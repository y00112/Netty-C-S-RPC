package webchat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import webchat.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author: 小山
 * @date: 2023/2/17 14:45
 * @content: 自定义编码，解码
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    // 编码
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1.魔数 4字节
        out.writeBytes(new byte[]{1,2,3,4});
        // 2.字节的版本
        out.writeByte(1);
        // 3.序列化算法 0 jdk 1 json
        out.writeByte(0);
        // 4.字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5.请求序号 4个字节
        out.writeInt(msg.getSequenceId());
        // 对其填充用的
        out.writeByte(0xff);
        // 6.获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();

        // 7.长度
        out.writeInt(bytes.length);

        // 8.写入内容
        out.writeBytes(bytes);


    }

    // 解码
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1.魔数
        int magicNum = in.readInt();
        // 2.版本
        byte version = in.readByte();
        // 3.序列化方式
        byte serializableType = in.readByte();
        // 4. 指令类型
        byte messageType = in.readByte();
        // 5. 强求序号
        int sequenceId = in.readInt();
        // 6. 填充位
        in.readByte();
        // 7. 长度
        int length = in.readInt();
        // 8.内容
        byte[] bytes = new byte[length];
        ByteBuf byteBuf = in.readBytes(bytes, 0, length);
        // 反序列化

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Message message = (Message) ois.readObject();


        log.info("{},{},{},{},{},{}",magicNum,version,serializableType,messageType,sequenceId,length);
        log.info("{}",message);
        out.add(message);
    }
}
