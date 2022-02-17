package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@AutoConfigureWebTestClient
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ItemStreamControllerTest {
    private static final String VERSION = "/v1";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;

    public static Flux<ItemCapped> data() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(idx -> new ItemCapped(null, "Random item " + idx, "Item", (100.0 + idx)))
                .take(5);
    }

    @BeforeAll
    public void setUp() {
        reactiveMongoOperations
                .dropCollection(ItemCapped.class)
                .then(reactiveMongoOperations
                        .createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000L).capped())
                )
                .subscribe();

        itemReactiveCappedRepository
                .insert(data())
                .doOnNext(itemCapped -> System.out.println("Inserted item in setup : " + itemCapped))
                .blockLast();
    }

    @Test
    public void testStreamAllItems() {
        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(VERSION + ItemConstants.ITEM_STREAM_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectSubscription()
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
