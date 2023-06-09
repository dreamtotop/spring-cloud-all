package org.top.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesFileUtil {

    private static final Logger log = LoggerFactory.getLogger(PropertiesFileUtil.class);


    private PropertiesFileUtil(){}

    public static Properties readPropertiesFile(String fileName) {

        URL url = Thread.currentThread().getContextClassLoader().getResource("");

        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }

        Properties properties = null;
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)){
            properties = new Properties();
            properties.load(reader);
        } catch (Exception ex){
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;
    }
}
