package com.sveticov.rediswebfluxtest.component;

import com.sveticov.rediswebfluxtest.model.Employer;
import com.sveticov.rediswebfluxtest.model.Status;
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
                Flux.just(new Employer("1","Beer",10, Status.Start),
                        new Employer("1","Joker",12,Status.Depart),
                        new Employer("1","Flash",17,Status.Wait))
                .map(employer->new Employer(UUID.randomUUID().toString(),employer.getNameEmployer(),employer.getAgeEmployer(),employer.getStatusEmployer()))
                .flatMap(employer->employerOps.opsForValue().set(employer.getIdEmployer(),
                        employer)))
                .thenMany(employerOps.keys("*")
                .flatMap(employerOps.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
