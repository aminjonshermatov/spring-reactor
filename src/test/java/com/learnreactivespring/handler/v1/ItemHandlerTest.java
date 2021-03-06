package com.learnreactivespring.handler.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ItemHandlerTest {
    private static final String VERSION = "/v1";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public static List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", "TV", 400.00),
                new Item(null, "LG TV", "TV", 420.00),
                new Item(null, "Apple Watch", "Watch", 499.00),
                new Item("ABC", "Beats HeadPhones", "HeadPhone", 149.99)
        );
    }

    @BeforeAll
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted item : " + item);
                })
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/ABC")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.price", 149.99);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient.get().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/ABCD")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item(null, "IPhone 13", "Phone", 999.99);

        webTestClient.post().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo(item.getDescription())
                .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    public void deleteItem() {
        webTestClient.delete().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        Double newPrice = 159.99;
        Item item = new Item(null, "Beats HeadPhones", "HeadPhone", newPrice);

        webTestClient.put().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    public void updateItem_notFound() {
        Double newPrice = 159.99;
        Item item = new Item(null, "Beats HeadPhones", "HeadPhone", newPrice);

        webTestClient.put().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/ABCD")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    /*
    @Test
    public void runtimeException() {
        webTestClient.get().uri(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Runtime exception occurred functional");
    }
    */
}
