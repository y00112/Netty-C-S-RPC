package rpc.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author: 小山
 * @date: 2023/2/21 13:47
 * @content:
 */
@Data
@ToString
public class RpcRequestMessage extends RpcMessage {

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }


    /**
     * 调用的接口全限定名，服务端根据他找到实现
     */
    private String interfaceName;

    /**
     * 调用接口中方法名
     */
    private String methodName;

    // 方法的返回类型
    private Class<?> returnType;

    // 方法参数类型数组
    private Class[] parameterTypes;

    // 方法参数值数组
    private Object[] parameterValue;

    public RpcRequestMessage(int sequenceId,String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        this.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }
}
