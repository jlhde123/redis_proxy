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
    private static ArrayList<RedisNode> servers =new ArrayList<>();
    private static String default_file="/server.properties";
    private static boolean filebool=false;

    public static void setFile(String file){
        filebool=true;
        default_file=file;
    }
    public static void init (){
        LoadProp();
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
    private static void LoadProp (){
        if (prop==null){
            new JedisSocketPropUtils().Load(default_file);
        }

        prop.keySet().stream().map(m -> {
            return String.valueOf(m);
        }).filter(m -> {
                    if (m.startsWith("redis.server."))
                        return true;
                    return false;
                }
        ).forEach(m -> {
            String socket = prop.getProperty(m);
            String url[] = socket.split(":");
            RedisNode r= new RedisNode();
            r.setName(m);
            r.setAddr(url[0]);
            r.setPort(Integer.valueOf(url[1]));
            r.setAlive(true);
            servers.add(r);
        });
    }
    public static RedisNode getServer(int index){
        if (size()<1)
            return null;
        Integer num=servers.size();
        for (int i=0;i<num;i++){
            RedisNode res=servers.get((index+i)%num);
            if (res.isAlive())
                return res;
        }
        return null;
    }

    public static RedisNode getAllServer (int index){
        if (size()<1)
            return null;
        return servers.get(index);
    }

    public static void disableServer (int index){
        RedisNode r=servers.get(index);
        r.setAlive(false);
        servers.set(index,r);
    }

    public static void setServer (int index,RedisNode r){
        servers.set(index,r);
    }
    public static int size(){
        return servers.size();
    }

    public static String getProp (String key){
        return prop.getProperty(key);
    }
}
