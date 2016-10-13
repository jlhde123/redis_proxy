package com.jlh.core;

import com.jlh.util.JedisSocketPropUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Exchanger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class TransportData4Server implements Runnable{

    Socket getDataSocket;
    Socket putDataSocket;
    InetSocketAddress target;
    Exchanger<InetSocketAddress> exchange;

    public TransportData4Server(Socket getDataSocket , Socket putDataSocket , Exchanger<InetSocketAddress> exchange){
        this.getDataSocket = getDataSocket;
        this.putDataSocket = putDataSocket;
        this.exchange=exchange;
    }

    @Override
    public void run() {
        try {
            while(true){
                InputStream in = getDataSocket.getInputStream() ;
                //读入数据
                byte[] data = new byte[1024];
                int readlen = in.read(data);
                //如果没有数据，则暂停
                String key = getKey(data);
                String url[]=JedisSocketPropUtils.getServer(key.hashCode()% JedisSocketPropUtils.size());
                System.out.println (url[0]);
                if (target==null&&!isEmpty(key)) {
                    target = new InetSocketAddress(url[1], Integer.valueOf(url[2]));
                    putDataSocket.connect(target);
                    new Thread(()->{
                        try {
                            exchange.exchange(target);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                OutputStream out = putDataSocket.getOutputStream() ;
                if(readlen<=0){
                    Thread.sleep(300);
                    continue;
                }

                out.write(data ,0,readlen);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println ("server is closed");
        }
        finally{
            //关闭socket
            System.out.println ("server finally is closed");
            try {
                if(putDataSocket != null){
                    putDataSocket.close();
                }
            } catch (Exception exx) {
                exx.printStackTrace();
            }

            try {
                if(getDataSocket != null){
                    getDataSocket.close();
                }
            } catch (Exception exx) {
                exx.printStackTrace();
            }
        }
        System.out.println ("end");
    }
    public String getKey(byte []data){
        String tmp=new String (data);
        Pattern p= Pattern.compile("(?<=.*\\r\\n).*(?=\\r\\n)");
        Matcher m2=p.matcher(tmp);
        int i=0;
        while (m2.find()&&i++<=2);
        return m2.group();
    }

    public boolean isEmpty(String a){
        return a==null? true:a.equals("");
    }
}
