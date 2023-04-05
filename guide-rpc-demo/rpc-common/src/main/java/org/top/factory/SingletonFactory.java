package org.top.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取单例对象的工厂类
 */
public class SingletonFactory<T> {

    private static final Map<String,Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> clz){
        if(clz == null){
            throw new IllegalArgumentException();
        }
        String key = clz.toString();
        if(OBJECT_MAP.containsKey(key)){
            return clz.cast(OBJECT_MAP.get(key));
        } else{
            return clz.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try{
                    return clz.getDeclaredConstructor().newInstance();
                } catch (Exception ex){
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }));
        }
    }
}
