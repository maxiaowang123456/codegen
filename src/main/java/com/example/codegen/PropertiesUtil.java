package com.example.codegen;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    public static String getProperty(String key,String filePath){
        Properties properties;
        try{
            properties=new Properties();
            ClassLoader classLoader=PojoGen.class.getClassLoader();
            InputStream in=classLoader.getResourceAsStream(filePath);
            properties.load(in);
            return properties.getProperty(key);
        }catch(Exception e){
            e.printStackTrace();
        }
       return null;
    }

    public static void main(String[] args){
        System.out.println(getProperty("1111111","codegenResource.properties"));
    }

}
