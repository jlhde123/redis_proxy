package com.jlh.util;

import com.jlh.core.RedisNode;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class JedisServerListUtils {
    private static ArrayList<RedisNode> servers =new ArrayList<>();

    public static void LoadProp (Properties prop){

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
}
