package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {
    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10);

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(el -> System.out.printf("Element is : %d\n", el),
                ex -> System.out.printf("Exception occurred : %s\n", ex.getMessage()),
                () -> System.out.println("Done"),
                subscription -> subscription.request(2));
    }
}
