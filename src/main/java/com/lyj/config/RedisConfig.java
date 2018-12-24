package com.lyj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by 陆英杰
 * 2018/12/16 21:24
 */

@Configuration
@ConfigurationProperties(prefix="redis")
public class RedisConfig {

    private String host;
    private int port;
    private int timeout;//秒
    private int poolMaxTotal;
    private int poolMaxdle;
    private int poolMaxWait;//秒
    private int database;//连几号库

    //创建一个JedisPool
    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig=new JedisPoolConfig();

        poolConfig.setMaxIdle(poolMaxdle);
        poolConfig.setMaxTotal(poolMaxWait);
        poolConfig.setMaxWaitMillis(poolMaxWait*1000);

        JedisPool jp=new JedisPool(poolConfig,host,port,timeout*1000,"lyjLYJ123",database);

        return jp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolMaxdle() {
        return poolMaxdle;
    }

    public void setPoolMaxdle(int poolMaxdle) {
        this.poolMaxdle = poolMaxdle;
    }

    public int getPoolMaxWait() {
        return poolMaxWait;
    }

    public void setPoolMaxWait(int poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
