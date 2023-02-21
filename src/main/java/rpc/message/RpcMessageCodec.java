package rpc.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import webchat.config.Config;
import webchat.message.Message;
import webchat.protocol.Serializer;

import java.util.List;

/**
 * @author: 小山
 * @date: 2023/2/17 14:45
 * @content: Rpc自定义编码，解码
 */
@Slf4j
public class RpcMessageCodec extends ByteToMessageCodec<RpcMessage> {

    // 编码
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        // 1.魔数 4字节
        out.writeBytes(new byte[]{1,2,3,4});
        // 2.字节的版本
        out.writeByte(1);
        // 3.序列化算法 0 jdk 1 json
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4.字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5.请求序号 4个字节
        out.writeInt(msg.getSequenceId());
        // 对其填充用的
        out.writeByte(0xff);
        // 6.获取内容的字节数组
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(msg);
        // 6. 序列化
        byte[] bytes =  Config.getSerializerAlgorithm().serializer(msg);
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
        // 1.1查询反序列化的算法
        Serializer.Algorithm serializer = Serializer.Algorithm.values()[serializableType];
        // 1.2查询具体的消息类型
        Class<?> aClass = Message.getMessageClass(messageType);
        Object message = serializer.deserializer(aClass, bytes);

//        log.info("{},{},{},{},{},{}",magicNum,version,serializableType,messageType,sequenceId,length);
//        log.info("{}",message);
        out.add(message);


    }
}
