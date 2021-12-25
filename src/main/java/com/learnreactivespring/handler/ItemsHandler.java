package com.learnreactivespring.handler;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemsHandler {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono
                .flatMap(item -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue(item))
                )
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        return itemMono
                .flatMap(item -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        final String id = serverRequest.pathVariable("id");

        return serverRequest.bodyToMono(Item.class)
                .flatMap(item -> itemReactiveRepository.findById(id)
                        .flatMap(curItem -> {
                            System.out.println(curItem);
                            curItem.setDescription(item.getDescription());
                            curItem.setPrice(item.getPrice());

                            return itemReactiveRepository.save(curItem);
                        }))
                .flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item)))
                .switchIfEmpty(notFound);

    }
}
