package com.jonet.eventbooking.components;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.jonet.eventbooking.model.TicketQuantityChangedEvent;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class TicketQuantityChangedListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TicketQuantityChangedEvent event) {
        messagingTemplate.convertAndSend(
            "/topic/events/" + event.ticketTypeId() + "/available",
            event.remainingQuantity()
        );
    } 
}
