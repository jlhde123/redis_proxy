package com.jlh.util;

import com.jlh.core.RedisNode;

import java.util.*;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class JedisServerListUtils {
    private static TreeMap<Long, RedisNode> ketamaNodes = new TreeMap<>();
    private static ArrayList<RedisNode> servers =new ArrayList<>();


    public static void printAll(){
        ketamaNodes.entrySet().forEach(System.out::println);
    }
    public static void initlize(int nCopies){
        for(RedisNode node : servers) {
            //每四个虚拟结点为一组，为什么这样？下面会说到
            for(int i=0; i<nCopies / 4; i++) {
                //getKeyForNode方法为这组虚拟结点得到惟一名称
                byte[] digest=HashAlgorithm.KETAMA_HASH.computeMd5(node.getAddr()+":"+node.getPort() + i);
                /** Md5是一个16字节长度的数组，将16字节的数组每四个字节一组，
                 分别对应一个虚拟结点，这就是为什么上面把虚拟结点四个划分一组的原因*/
                for(int h=0;h<4;h++) {
                    //对于每四个字节，组成一个long值数值，做为这个虚拟节点的在环中的惟一key
                    Long k = ((long)(digest[3+h*4]&0xFF) << 24)
                            | ((long)(digest[2+h*4]&0xFF) << 16)
                            | ((long)(digest[1+h*4]&0xFF) << 8)
                            | (digest[h*4]&0xFF);

                    ketamaNodes.put(k, node);
                }
            }
        }
    }
    public static RedisNode getPrimary(final String k) {
        byte[] digest = HashAlgorithm.KETAMA_HASH.computeMd5(k);
        RedisNode rv=getNodeForKey(HashAlgorithm.KETAMA_HASH.hash(digest, 0));
        return rv;
    }
    static RedisNode getNodeForKey(long hash) {
        final RedisNode rv;
        Long key = hash;
        boolean find=false;
        if(!ketamaNodes.containsKey(key)) {
            SortedMap<Long, RedisNode> tailMap=ketamaNodes.tailMap(key);
            if (!tailMap.isEmpty()) {
                key = tailMap.firstKey();
                for (Map.Entry<Long, RedisNode> tmp : tailMap.entrySet()) {
                    if (tmp.getValue().isAlive()) {
                        key = tmp.getKey();
                        find=true;
                        break;
                    }
                }
            }
            if (!find){
                for (Map.Entry<Long, RedisNode> tmp : ketamaNodes.entrySet()) {
                    if (tmp.getValue().isAlive()) {
                        key = tmp.getKey();
                        find=true;
                        break;
                    }
                }
            }
        }
        rv=ketamaNodes.get(key);
        return rv;
    }


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

        initlize(12);
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
