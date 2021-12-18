package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoTimeTest {
    @Test
    public void infiniteSequence() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200));

        infiniteFlux.subscribe(el -> System.out.printf("Value is : %d", el));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void infiniteSequenceTests() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3);

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .expectComplete()
                .verify();
    }

    @Test
    public void infiniteSequenceMap() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .map(Long::intValue)
                .take(3);

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .expectComplete()
                .verify();
    }

    @Test
    public void infiniteSequenceMapDelay() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3);

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .expectComplete()
                .verify();
    }
}
