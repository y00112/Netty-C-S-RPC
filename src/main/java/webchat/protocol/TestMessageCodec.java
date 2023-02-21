package webchat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import webchat.message.LoginRequestMessage;

/**
 * @author: 小山
 * @date: 2023/2/17 15:19
 * @content: 出栈入栈测试
 */
public class TestMessageCodec {


    public static void main(String[] args) throws Exception {

        LengthFieldBasedFrameDecoder FrameDecoder = new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0);



                EmbeddedChannel channel = new EmbeddedChannel(
                        FrameDecoder,
                 new LoggingHandler(),
                new MessageCodec());
        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
//        channel.writeOutbound(message);

        // decode
         ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
         new MessageCodec().encode(null,message,buf);

        ByteBuf s1 = buf.slice(0, 100);
        final ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
        // 入栈
        s1.retain();
        channel.writeInbound(s1);
        channel.writeInbound(s2);
    }
}
