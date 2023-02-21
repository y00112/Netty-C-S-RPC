package rpc.util;

import com.google.gson.*;
import webchat.protocol.Serializer;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author: 小山
 * @date: 2023/2/21
 * @content: 使用Gson转换Java中所有的引用数据类型，需要类型转换器
 */
public class TestGson {

    /*
    io.netty.handler.codec.EncoderException:
    java.lang.UnsupportedOperationException:
    Attempted to serialize java.lang.Class:
     java.lang.String. Forgot to register a type adapter?

     */

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        System.out.println(gson.toJson(HashMap.class));
    }

//    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>>{
//
//        @Override
//        public Class<?> deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            // json -> class
//            // 获取json类名
//            try {
//                String str = json.getAsString();
//                return Class.forName(str);
//            } catch (ClassNotFoundException e) {
//                throw new JsonParseException(e);
//            }
//
//        }
//
//        @Override
//        public JsonElement serialize(Class<?> src, Type type, JsonSerializationContext jsonSerializationContext) {
//           // class -> json
//            return new JsonPrimitive(src.getName());
//        }
//    }
}
