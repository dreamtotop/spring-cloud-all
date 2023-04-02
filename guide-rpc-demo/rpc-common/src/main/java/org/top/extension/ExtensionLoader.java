package org.top.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.top.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  加载类
 * @param <T>
 */
public class ExtensionLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    private static final Map<Class, ExtensionLoader> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type){
        if(type == null){
            throw new IllegalArgumentException("Extension type must not be null");
        }
        if(! type.isInterface()){
            throw new IllegalArgumentException("Extension type must be interface");
        }

        if(type.getAnnotation(SPI.class) == null){
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }

        ExtensionLoader extensionLoader = EXTENSION_LOADERS.get(type);
        if(extensionLoader == null){
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }


    public T getExtension(String name){
        if(StringUtil.isBlank(name)){
            throw new IllegalArgumentException("Extension name should not be null or empty");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if(holder == null){
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        // 加载类实例
        if(instance == null){
            synchronized (holder){
                instance = holder.get();
                if(instance == null){
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if(clazz == null){
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if(instance == null){
            try{
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception ex){
                log.error(ex.getMessage());
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses(){
        Map<String, Class<?>> classes  = cachedClasses.get();
        if(classes == null){
            synchronized (cachedClasses){
                classes = cachedClasses.get();
                if(classes == null){
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses){
        String fileName = SERVICE_DIRECTORY + type.getName();
        try{
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if(urls != null){
                while(urls.hasMoreElements()){
                    URL resource = urls.nextElement();
                    loadResource(classLoader, resource, extensionClasses);
                }
            }
        } catch (IOException ex){
            log.error(ex.getMessage());
        }
    }

    private void loadResource(ClassLoader loader, URL resource, Map<String, Class<?>> extensionClasses){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null){
                int index = line.indexOf('#');
                if(index >= 0){
                    line = line.substring(0, index);
                }
                line = line.trim();
                if(line.length() > 0){
                    try{
                        int ei = line.indexOf('#');
                        String name = line.substring(0, ei).trim();
                        String className = line.substring(ei+1).trim();
                        log.info("load name is {}, load className is {}", name, className);
                        if (name.length() > 0 && className.length() > 0) {
                            Class<?> clazz = loader.loadClass(className);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException ex){
                        log.error(ex.getMessage());
                    }
                }
            }
        } catch (IOException ex){
            log.error(ex.getMessage());
        }
    }
}
