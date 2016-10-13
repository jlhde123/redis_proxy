package com.jlh.core;

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
            Integer num = JedisSocketPropUtils.size();
            for (int i = 0; i < num; i++) {
                String url[] = JedisSocketPropUtils.getAllServer(i);
                try {
                    Socket sk = new Socket(url[1], Integer.valueOf(url[2]));
                    sk = null;
                    if (url[3].equals("0")) {
                        url[3] = "1";
                        JedisSocketPropUtils.setServer(i, url);
                        System.out.println(url[0] + "is open try to add it");
                    }
                } catch (IOException e) {
                    if (url[3].equals("1")) {
                        System.out.println(url[0] + "is closed try to remove it");
                        JedisSocketPropUtils.removeServer(i);
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
