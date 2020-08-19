package com.sveticov.rediswebfluxtest.component;

import com.sveticov.rediswebfluxtest.model.Employer;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class EmployerLoader {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Employer> employerOps;

    public EmployerLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Employer> employerOps) {
        this.factory = factory;
        this.employerOps = employerOps;
    }

    @PostConstruct
    public void loadData(){
        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
                Flux.just("Nativ","Joker","Flash")
                .map(name->new Employer(UUID.randomUUID().toString(),name))
                .flatMap(employer->employerOps.opsForValue().set(employer.getIdEmployer(),
                        employer)))
                .thenMany(employerOps.keys("*")
                .flatMap(employerOps.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
