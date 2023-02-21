package webchat.protocol;

import com.google.gson.*;


import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author: 小山
 * @date: 2023/2/19
 * @content:
 */
public interface Serializer {

    // 反序列化方法
    <T> T deserializer(Class<T> clazz,byte[] bytes);

    // 序列化方法
    <T> byte[] serializer(T object);

    enum Algorithm implements Serializer{

        Java{
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException("反序列化失败");
                }
            }

            @Override
            public <T> byte[] serializer(T object) {
                // 6.序列化
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);

                } catch (IOException e) {
                    throw new RuntimeException("序列化失败");
                }
                return  bos.toByteArray();

            }
        },

        Json{
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json,clazz);
            }

            @Override
            public <T> byte[] serializer(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                java.lang.String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }

    }


    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            // json -> class
            // 获取json类名
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }

        }

        @Override
        public JsonElement serialize(Class<?> src, Type type, JsonSerializationContext jsonSerializationContext) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }
}
