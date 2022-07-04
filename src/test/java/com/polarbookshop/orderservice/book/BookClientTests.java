package com.polarbookshop.orderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

public class BookClientTests {

    private MockWebServer mockWebServer;

    private BookClient bookClient;

    @BeforeEach
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        var webClient = WebClient.builder()
                .baseUrl(this.mockWebServer.url("/").uri().toString())
                .build();
        this.bookClient = new BookClient(webClient);
    }

    @AfterEach
    public void clean() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    public void whenBookExistsThenReturnBook() {
        String bookIsbn = "1234567890";
        String content = String.format("{\n" +
                "\"isbn\": \"%s\",\n" +
                "\"title\": \"Title\",\n" +
                "\"author\": \"Author\",\n" +
                "\"price\": 9.90,\n" +
                "\"publisher\": \"Polarsophia\"\n" +
                "}", bookIsbn
        );

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(content);

        this.mockWebServer.enqueue(mockResponse);

        Mono<Book> book = this.bookClient.getBookByIsbn(bookIsbn);

        StepVerifier.create(book)
                .expectNextMatches(b -> b.getIsbn().equals(bookIsbn))
                .verifyComplete();
    }
}
