package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void filterTest() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(name -> name.startsWith("A"))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Adam", "Anna")
                .expectComplete()
                .verify();
    }

    @Test
    public void filterTest_Length() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(name -> name.length() > 4)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Jenny")
                .expectComplete()
                .verify();
    }
}
