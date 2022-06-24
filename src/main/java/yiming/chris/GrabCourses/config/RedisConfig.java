package yiming.chris.GrabCourses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * ClassName:RedisConfig
 * Package:yiming.chris.GrabCourses.cofig
 * Description:
 * @Author: ChrisEli
 */
    @Configuration
    public class RedisConfig {

        /**
         * 注入自定义的RedisTemplate
         * Serializer
         * @param redisConnectionFactory
         * @return
         */
        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);

            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

            redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
            redisTemplate.setHashValueSerializer(redisTemplate.getValueSerializer());
            return redisTemplate;
        }


    }
