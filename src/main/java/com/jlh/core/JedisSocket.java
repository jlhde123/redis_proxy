package com.jlh.core;

import com.jlh.util.JedisSocketPropUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jlh
 * On 2016/10/11 0011.
 *
 * @description
 */
public class JedisSocket {


    public static void main(String[] args) throws IOException {
        if (args.length>0)
            JedisSocketPropUtils.setFile(args[0]);
        JedisSocketPropUtils.init();
        ExecutorService pool= Executors.newFixedThreadPool(Integer.valueOf(JedisSocketPropUtils.getProp("redis.proxy.maxPool")));
        ServerSocket sk = new ServerSocket(Integer.valueOf(JedisSocketPropUtils.getProp("redis.proxy.port")));
        while (true) {
            pool.execute(new ClearFailService());
            Socket clientSk = sk.accept();
            Socket remoteServerSocket = new Socket();
            Exchanger<InetSocketAddress> exchanger = new Exchanger<>();
//            new Thread(new TransportData4Server(clientSk,remoteServerSocket,exchanger)).start();
//            new Thread(new TransportData4Client(remoteServerSocket,clientSk,exchanger)).start();
            pool.execute(new TransportData4Server(clientSk,remoteServerSocket,exchanger));
            pool.execute(new TransportData4Client(remoteServerSocket,clientSk,exchanger));
        }
    }
}
