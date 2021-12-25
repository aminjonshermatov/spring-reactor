package com.learnreactivespring.router.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {
    private static final String VERSION = "/v1";

    @Bean
    public RouterFunction<ServerResponse> itemRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(
                        GET(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::getAllItems
                )
                .andRoute(
                        GET(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::getOneItem
                )
                .andRoute(
                        POST(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::createItem
                )
                .andRoute(
                        DELETE(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::deleteItem
                )
                .andRoute(
                        PUT(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::updateItem
                );
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
        return RouterFunctions.route(
                GET(VERSION + ItemConstants.ITEM_FUNCTIONAL_ENDPOINT + "/runtimeException").and(accept(MediaType.APPLICATION_JSON)),
                itemsHandler::itemsEx
        );
    }
}
