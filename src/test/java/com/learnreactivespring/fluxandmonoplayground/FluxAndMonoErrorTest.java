package com.learnreactivespring.fluxandmonoplayground;

import com.learnreactivespring.fluxandmonoplayground.exception.CustomException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoErrorTest {
    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D", "E", "F"))
                .onErrorResume(ex -> {
                    System.err.printf("Exception is : %s\n", ex.getMessage());
                    return Flux.just("resume");
                });

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "resume")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D", "E", "F"))
                .onErrorReturn("return");

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "return")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D", "E", "F"))
                .onErrorMap(CustomException::new);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D", "E", "F"))
                .onErrorMap(CustomException::new)
                .retry(1);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetryBackOf() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D", "E", "F"))
                .onErrorMap(CustomException::new);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }
}
