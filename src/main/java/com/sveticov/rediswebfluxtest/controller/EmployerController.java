package com.sveticov.rediswebfluxtest.controller;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sveticov.rediswebfluxtest.model.Employer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.lettuce.core.pubsub.PubSubOutput.Type.subscribe;

@RestController
@Slf4j
public class EmployerController {
    private final ReactiveRedisOperations<String, Employer> employerOps;

    public EmployerController(ReactiveRedisOperations<String, Employer> employerOps) {
        this.employerOps = employerOps;
    }

    @GetMapping("/employer")
    public Flux<Employer> all() {
        return employerOps.keys("*")
                .flatMap(employerOps.opsForValue()::get);
    }

    @GetMapping("/employer/{id}")
    public Mono<Employer> getId(@PathVariable("id") String id) {
        return employerOps.opsForValue().get(id);
    }

    @PostMapping("/employer/save")
    public Mono<Employer> save(@RequestBody Employer employer){
        log.info("save >>>>>>>>>>>>>>>>>>>>>>.");
        log.info(employer.toString());
        employer.setIdEmployer(UUID.randomUUID().toString());
        log.info(employer.toString());
        employerOps.opsForValue().set(employer.getIdEmployer(),employer)
               .then(employerOps.opsForValue().get(employer.getIdEmployer()))
                .subscribe(System.out::println); //answer
//                .thenMany(employerOps.keys("*")
//                .flatMap(employerOps.opsForValue()::get))
//                .subscribe(System.out::println);

        return employerOps.opsForValue().get(employer.getIdEmployer());
    }
}
