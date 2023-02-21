package rpc.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author: 小山
 * @date: 2023/2/21 13:53
 * @content:
 */
@Data
@ToString
public class RpcResponseMessage extends RpcMessage {
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

    // 返回值
    private Object returnValue;

    // 异常值
    private Exception exceptionValue;


}
