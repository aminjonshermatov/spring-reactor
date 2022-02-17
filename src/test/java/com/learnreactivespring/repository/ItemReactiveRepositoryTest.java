package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class ItemReactiveRepositoryTest {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", "TV", 400.00),
            new Item(null, "LG TV", "TV", 420.00),
            new Item(null, "Apple Watch", "Watch",499.00),
            new Item(null, "Beats HeadPhones", "HeadPhone", 149.99)
    );

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted Item is : " + item))
                .blockFirst();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete()
                .verify();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABCD"))
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Apple Watch"))
                .expectSubscription()
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void saveItem() {
        Item itemForSave = new Item(null, "Google Home Mini", "Assistant", 39.00);
        Mono<Item> savedItem = itemReactiveRepository.save(itemForSave);

        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getId() != null && item.getDescription().equals(itemForSave.getDescription()))
                .expectComplete()
                .verify();
    }

    @Test
    public void updateItem() {
        final String description = "Samsung TV";
        final Double newPrice = 450D;


        Flux<Item> updatedItem = itemReactiveRepository.findByDescription(description)
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(itemReactiveRepository::save);

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .expectComplete()
                .verify();
    }

    @Test
    public void deleteItemById() {
        Flux<Void> itemSelected = itemReactiveRepository.findByDescription("Apple Watch")
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(itemSelected)
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    public void deleteByItem() {
        Flux<Void> deleteResult = itemReactiveRepository.findByDescription("Apple Watch")
                .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(deleteResult)
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }
}
