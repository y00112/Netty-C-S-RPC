package webchat.config;

import webchat.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: 小山
 * @date: 2023/2/19
 * @content:
 */
public class Config {
    static Properties properties;
    static {
        try {
            InputStream in = Config.class.getResourceAsStream("/application.properties");
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Serializer.Algorithm getSerializerAlgorithm(){
        String value = properties.getProperty("serializer.algorithm");
        if (value == null){
            return Serializer.Algorithm.Java;
        }else {
            return Serializer.Algorithm.Json;
        }
    }
}
