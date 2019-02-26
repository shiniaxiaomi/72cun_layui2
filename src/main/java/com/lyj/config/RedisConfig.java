package com.lyj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Created by 陆英杰
 * 2018/12/16 21:24
 */

@Configuration
@ConfigurationProperties(prefix="my-redis")
public class RedisConfig {

//    private String host;//主机地址
//    private int port;//端口
//    private String password;//连接密码
//    private int timeout;//连接超时时间（秒）
//    private int database;//连接第几个库


    //自定义配置redisTemplate的序列化规则json格式
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

//    //JedisPool连接池
//    @Bean
//    public JedisPool jedisPool(){
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        return new JedisPool(poolConfig,host,port,timeout,password,database);
//    }

//    public RedisTemplate redisTemplate(){
//        return new RedisTemplate();
//    }

}
