package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {
    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxUsingArray() {
        Flux<String> namesFlux = Flux.fromArray(new String[]{"Foo", "Bar"});

        StepVerifier.create(namesFlux)
                .expectNext("Foo", "Bar")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .expectComplete()
                .verify();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "Adam";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono)
                .expectNext("Adam")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(2, 5);

        StepVerifier.create(integerFlux)
                .expectNext( 2, 3, 4, 5, 6)
                .expectComplete()
                .verify();
    }
}
