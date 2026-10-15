package com.manikanta.paymentservice.kafka;

import com.manikanta.paymentservice.entity.Payment;
import com.manikanta.paymentservice.event.OrderCreatedEvent;
import com.manikanta.paymentservice.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;

@Service
public class OrderEventConsumer {

    private final PaymentRepository paymentRepository;

    public OrderEventConsumer(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @KafkaListener(
            topics = "order-created-v2",
            groupId = "payment-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreated(
            ConsumerRecord<String, OrderCreatedEvent> record
          //  ,            Acknowledgment acknowledgment
    ) {

        OrderCreatedEvent event = record.value();

        System.out.println("Received event: " + event);

        if (paymentRepository.findByEventId(event.getEventId()).isPresent()) {

            System.out.println(
                    "Payment already processed for eventId: "
                            + event.getEventId()
            );

         //   acknowledgment.acknowledge();
            return;
        }

        Payment payment = new Payment();
        payment.setEventId(event.getEventId());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getPrice());
        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);

        System.out.println(
                "Payment processed successfully for order: "
                        + event.getOrderId()
        );

      //  acknowledgment.acknowledge();
    }
}