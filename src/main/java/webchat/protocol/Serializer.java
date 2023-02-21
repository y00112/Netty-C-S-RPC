package webchat.protocol;

import com.google.gson.Gson;



import java.io.*;
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

        JAVA{
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

        JSON{
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(json,clazz);
            }

            @Override
            public <T> byte[] serializer(T object) {
                java.lang.String json = new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }

    }
}
