package com.lyj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by 陆英杰
 * 2018/12/16 21:24
 */

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private String host;
    private String password;
    private int port;
    private int timeout;

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


    //配置的操作的是1号库
    @Bean
    public RedisTemplate redisTemplate(){
        //创建连接工厂
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(new JedisPoolConfig());
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setTimeout(timeout);
        jedisConnectionFactory.setDatabase(1);//选择1号库

        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        //设置redisTempalte的序列化方式
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// 设置值（value）的序列化采用FastJsonRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());// 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


//    public static void main(String[] args) {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        JedisPool jedisPool = new JedisPool(poolConfig, "45.40.206.81", 6379, 3000, "lyjLYJ123", 0);//超时时间的单位是毫秒
//        Jedis jedis = jedisPool.getResource();
//        String asking = jedis.asking();
//        System.out.println(asking);
//        jedis.close();
//    }

    public void setHost(String host) {
        this.host = host;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
