package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import com.polarbookshop.orderservice.order.event.OrderAcceptedMessage;
import com.polarbookshop.orderservice.order.event.OrderDispatchedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final BookClient bookClient;
    private final OrderRepository orderRepository;

    private final StreamBridge streamBridge;

    public Flux<Order> getAllOrders(String userId) {
        return this.orderRepository.findAllByCreatedBy(userId);
    }

    @Transactional
    public Mono<Order> submitOrder(String isbn, int quantity) {
        return this.bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }

    public static Order buildAcceptedOrder(Book book, int quantity) {
        return Order.of(book.getIsbn(),
                book.getTitle() + " - " + book.getAuthor(),
                book.getPrice(),
                quantity,
                OrderStatus.ACCEPTED);
    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
        return flux.flatMap(message -> this.orderRepository.findById(message.getOrderId()))
                .map(this::buildDispatchedOrder)
                .flatMap(this.orderRepository::save);
    }

    private void publishOrderAcceptedEvent(Order order) {
        if (!order.getStatus().equals(OrderStatus.ACCEPTED)) {
            return;
        }

        var orderAcceptedMessage = new OrderAcceptedMessage(order.getId());
        log.info("Sending order accepted event with id: {}", order.getId());

        var result = this.streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}", order.getId(), result);
    }

    private Order buildDispatchedOrder(Order existingOrder) {
        return new Order(
                existingOrder.getId(),
                existingOrder.getBookIsbn(),
                existingOrder.getBookName(),
                existingOrder.getBookPrice(),
                existingOrder.getQuantity(),
                OrderStatus.DISPATCHED,
                existingOrder.getCreatedDate(),
                existingOrder.getLastModifiedDate(),
                existingOrder.getCreatedBy(),
                existingOrder.getLastModifiedBy(),
                existingOrder.getVersion());
    }
}
