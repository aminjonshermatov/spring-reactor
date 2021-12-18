package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> stringNames = Flux.fromIterable(names)
                .map(name -> name.toUpperCase(Locale.ROOT));

        StepVerifier.create(stringNames)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .expectComplete()
                .verify();
    }

    @Test
    public void transformUsingMap_Length() {
        Flux<Integer> stringNames = Flux.fromIterable(names)
                .map(String::length);

        StepVerifier.create(stringNames)
                .expectNext(4, 4, 4, 5)
                .expectComplete()
                .verify();
    }

    @Test
    public void transformUsingMap_Length_Repeat() {
        Flux<Integer> stringNames = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1);

        StepVerifier.create(stringNames)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .expectComplete()
                .verify();
    }

    @Test
    public void transformUsingMapFilter() {
        Flux<Integer> stringNames = Flux.fromIterable(names)
                .map(String::length)
                .filter(length -> length > 4);

        StepVerifier.create(stringNames)
                .expectNext(5)
                .expectComplete()
                .verify();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringNames = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s ->  Flux.fromIterable(convertToList(s)))
                .log();

        StepVerifier.create(stringNames)
                .expectNextCount(12)
                .expectComplete()
                .verify();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformUsingFlatMap_UsingParallel() {
        Flux<String> stringNames = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(s ->
                    s
                            .map(this::convertToList)
                            .subscribeOn(parallel())
                            .flatMap(Flux::fromIterable)
                )
                .log();

        StepVerifier.create(stringNames)
                .expectNextCount(12)
                .expectComplete()
                .verify();
    }

    @Test
    public void transformUsingFlatMap_UsingParallel_maintain_order() {
        Flux<String> stringNames = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .concatMap(s ->
                        s
                                .map(this::convertToList)
                                .subscribeOn(parallel())
                                .flatMap(Flux::fromIterable)
                )
                .log();

        StepVerifier.create(stringNames)
                .expectNextCount(12)
                .expectComplete()
                .verify();
    }
}
