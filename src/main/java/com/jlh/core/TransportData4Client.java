package com.jlh.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Exchanger;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class TransportData4Client implements Runnable {
    Socket getDataSocket;
    Socket putDataSocket;
    InetSocketAddress target;
    Exchanger<InetSocketAddress> exchange;

    public TransportData4Client(Socket getDataSocket , Socket putDataSocket , Exchanger<InetSocketAddress> exchange){
        this.getDataSocket = getDataSocket;
        this.putDataSocket = putDataSocket;
        this.exchange=exchange;
    }

    @Override
    public void run() {
        try {
            while(true){
                if (target==null)
                    target=exchange.exchange(null);
                InputStream in = getDataSocket.getInputStream() ;
                OutputStream out = putDataSocket.getOutputStream();
                //读入数据
                byte[] data = new byte[1024];
                int readlen = in.read(data);
                //如果没有数据，则暂停
                if(readlen<=0){
                    Thread.sleep(300);
                    continue;
                }

                out.write(data ,0,readlen);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println ("client is closed");

        }
        finally{
            //关闭socket
            System.out.println ("client  finally is closed");
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
    }
}
