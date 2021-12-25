package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        dataSetupForCappedCollection();
    }

    private void createCappedCollection() {
        reactiveMongoOperations.dropCollection(ItemCapped.class)
                .then(reactiveMongoOperations
                        .createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000L).capped())
                )
                .subscribe();
    }

    public List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 420.00),
                new Item(null, "Apple Watch", 499.00),
                new Item("abc", "Beats HeadPhones", 149.99)
        );
    }

    private void dataSetupForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(idx -> new ItemCapped(null, "Random item " + idx, (100.0 + idx)));

        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .subscribe(item -> log.info("Inserted item is " + item));
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("item inserted from CommandLineRunner : " + item);
                });
    }
}
