package com.polarbookshop.orderservice.order.web;

import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderService;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
public class OrderControllerWebFluxTests {

    @MockBean
    private OrderService orderService;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void whenBookNotAvailableThenRejectOrder() {
        OrderRequest orderRequest = new OrderRequest("1234567890", 3);
        Order expectedOrder = OrderService.buildRejectedOrder(orderRequest.getIsbn(), orderRequest.getQuantity());

        given(this.orderService.submitOrder(orderRequest.getIsbn(), orderRequest.getQuantity()))
                .willReturn(Mono.just(expectedOrder));

        this.webClient
                .post().uri("/orders/")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.REJECTED);
                });
    }
}
