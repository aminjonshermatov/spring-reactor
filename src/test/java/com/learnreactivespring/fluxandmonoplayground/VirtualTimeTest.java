package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {
    @Test
    public void testingWithoutVirtualTime() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .expectComplete()
                .verify();
    }

    @Test
    public void testingWithVirtualTime() {
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .expectComplete()
                .verify();
    }
}
