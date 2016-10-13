package com.jlh.util;

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
    private static ArrayList<String[]> servers =new ArrayList<>();
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
            String urls[] = new String[4];
            urls[0] = m;
            urls[1] = url[0];
            urls[2] = url[1];
            urls[3] = "1";
            servers.add(urls);
        });
    }
    public static String[]  getServer(int index){
        if (size()<1)
            return null;
        Integer num=servers.size();
        for (int i=0;i<num;i++){
            String res[]=servers.get((index+i)%num);
            if (res[3].equals("1"))
                return res;
        }
        return null;
    }

    public static String[] getAllServer (int index){
        if (size()<1)
            return null;
        return servers.get(index);
    }

    public static void removeServer (int index){
        String urls[]=servers.get(index);
        urls[3]="0";
        servers.set(index,urls);
    }

    public static void setServer (int index,String []url){
        servers.set(index,url);
    }
    public static int size(){
        return servers.size();
    }

    public static String getProp (String key){
        return prop.getProperty(key);
    }
}
