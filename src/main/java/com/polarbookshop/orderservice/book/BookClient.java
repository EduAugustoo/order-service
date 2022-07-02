package com.polarbookshop.orderservice.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class BookClient {

    private static final String BOOKS_ROOT_API = "/books/";

    private final WebClient webClient;

    public Mono<Book> getBookByIsbn(String isbn) {
        return this.webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class);
    }
}