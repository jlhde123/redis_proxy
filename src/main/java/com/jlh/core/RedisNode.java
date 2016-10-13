package com.jlh.core;

/**
 * Created by jlh
 * On 2016/10/13 0013.
 *
 * @description
 */
public class RedisNode {
    private String name;
    private String addr;
    private Integer port;
    private boolean isAlive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public String toString() {
        return "RedisNode{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", port=" + port +
                ", isAlive=" + isAlive +
                '}';
    }
}
