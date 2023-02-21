package rpc.service;

import webchat.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 小山
 * @date: 2023/2/21
 * @content: 使用反射的方式，在资源文件中通过接口的全路径找到实现类的全路径
 */
public class ServiceFactory {
    static Properties properties;
    static Map<Class<?>,Object> map = new ConcurrentHashMap<>();

    // 通过反射的方式根据接口的类型获取实例对象
    static {
        try {
            InputStream in = Config.class.getResourceAsStream("/application.properties");
            properties = new Properties();
            properties.load(in);
            Set<String> names = properties.stringPropertyNames();
            for(String name : names){
                if (name.endsWith("Service")){
                    Class<?> interfaceClass = Class.forName(name);
                    Class<?> instanceClass = Class.forName(properties.getProperty(name));
                    map.put(interfaceClass,instanceClass.newInstance());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getService(Class<T> interfaceClass){
        return (T) map.get(interfaceClass);
    }
}
