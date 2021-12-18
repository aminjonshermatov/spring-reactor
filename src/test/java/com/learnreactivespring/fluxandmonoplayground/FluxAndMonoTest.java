package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                                .concatWith(Flux.just("After Error"))
                                .log();

        stringFlux
                .subscribe(System.out::println,
                        System.err::println,
                        () -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest_Success() {
        Mono<String> stringMono = Mono.just("Spring")
                .log();

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .expectComplete()
                .verify();
    }

    @Test
    public void monoTest_Error() {
        Mono<Object> stringMono = Mono.error(new RuntimeException("Exception occurred"))
                .log();

        StepVerifier.create(stringMono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
