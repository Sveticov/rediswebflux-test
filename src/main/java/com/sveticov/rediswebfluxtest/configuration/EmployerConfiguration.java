package com.sveticov.rediswebfluxtest.configuration;

import com.sveticov.rediswebfluxtest.model.Employer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class EmployerConfiguration {
    @Bean
    ReactiveRedisOperations<String, Employer> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Employer> serializer = new Jackson2JsonRedisSerializer<>(Employer.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Employer> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, Employer> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
