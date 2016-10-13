package com.jlh.core;

import com.jlh.util.JedisServerListUtils;
import com.jlh.util.JedisSocketPropUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class ClearFailService implements Runnable{
    @Override
    public void run() {
        while (true) {
            System.out.println ("clear thread start");
            Integer num = JedisServerListUtils.size();
            for (int i = 0; i < num; i++) {
                RedisNode r = JedisServerListUtils.getAllServer(i);
                try {
                    Socket sk = new Socket(r.getAddr(),r.getPort());
                    sk = null;
                    if (!r.isAlive()) {
                        r.setAlive(true);
                        JedisServerListUtils.setServer(i, r);
                        System.out.println(r.getName() + "is open try to add it");
                    }
                } catch (IOException e) {
                    if (r.isAlive()) {
                        System.out.println(r.getName() + "is closed try to remove it");
                        JedisServerListUtils.disableServer(i);
                    }
                }
            }
            try {
                TimeUnit.SECONDS.sleep(Long.valueOf(JedisSocketPropUtils.getProp("redis.clear.time")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
