package com.manikanta.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String eventId;
    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;
    private Double price;
}