package com.jlh.util;

import com.jlh.core.RedisNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class JedisSocketPropUtils {
    private static Properties prop;
    private static String default_file="/server.properties";
    private static boolean filebool=false;

    public static void setFile(String file){
        filebool=true;
        default_file=file;
    }
    public static void init (){
        JedisSocketPropUtils jd= new JedisSocketPropUtils();
        jd.Load(default_file);
        JedisServerListUtils.LoadProp(prop);
    }
    public void Load(String file) {
        prop=new Properties();
        InputStream in=null;
        if (!filebool) {
            System.out.println(this.getClass().getResource(file));
            in= this.getClass().getResourceAsStream(file);
        }else{
            try {
                System.out.println(file);
                in=new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProp (String key){
        return prop.getProperty(key);
    }
}
