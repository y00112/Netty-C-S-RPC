package webchat.amessage;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 小山
 * @date: 2023/2/20 16:17
 * @content:
 */
@Data
public abstract class MessageA implements Serializable {


    private int messageType;

    public abstract int getMessageType();

    // 广播通知
    public static final int RadioRequestMessage = 0;
    public static final int RadioResponseMessage = 1;
    // 物料需求
    public static final int NeedRequestMessage = 2;
    public static final int NeedResponseMessage = 3;
    // 人员联系
    public static final int SearchRequestMessage = 4;
    public static final int SearchResponseMessage = 5;
    // 紧急呼救
    public static final int CallRequestMessage = 6;
    public static final int CallResponseMessage = 7;


    // 心跳
    public static final int PingMessage = 14;
    public static final int PongMessage = 15;

    public static final Map<Integer,Class<?>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RadioRequestMessage,RadioResponseMessage.class);
    }
}
