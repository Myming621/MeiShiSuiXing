package com.mym.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author mingbb
 * @create 2023-09-23-20:42
 */

/**
 * Redis配置类
 * 编写配置类，创建RedisTemplate对象
 * 通过RedisTemplate对象操作Redis
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象...");
        //创建RedisTemplate对象
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工厂对象，pom中引入的redis依赖会自动创建RedisConnectionFactory连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
