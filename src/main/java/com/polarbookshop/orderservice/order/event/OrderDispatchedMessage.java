package com.polarbookshop.orderservice.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDispatchedMessage {

    private Long orderId;
}
