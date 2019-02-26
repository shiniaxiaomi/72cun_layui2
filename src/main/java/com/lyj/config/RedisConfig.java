package com.lyj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Created by 陆英杰
 * 2018/12/16 21:24
 */

@Configuration
public class RedisConfig {

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


//    public static void main(String[] args) {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        JedisPool jedisPool = new JedisPool(poolConfig, "45.40.206.81", 6379, 3000, "lyjLYJ123", 0);//超时时间的单位是毫秒
//        Jedis jedis = jedisPool.getResource();
//        String asking = jedis.asking();
//        System.out.println(asking);
//        jedis.close();
//    }

}
