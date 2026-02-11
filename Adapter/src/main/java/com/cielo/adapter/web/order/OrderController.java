package com.cielo.adapter.web.order;

import enums.OrderStatus;
import input.CreateOrderInput;
import input.GenerateOrderReceiptPDFInput;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping
public class OrderController {
    private final CreateOrderInput createOrder;
    private final GenerateOrderReceiptPDFInput generateReceipt;

    public OrderController(CreateOrderInput createOrder, GenerateOrderReceiptPDFInput generateReceipt) {
        this.createOrder = createOrder;
        this.generateReceipt = generateReceipt;
    }


    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Void> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderDTO request
    ) {

        createOrder.createOrder(userId, request.getAmount());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/orders/{orderId}/receipt")
    public ResponseEntity<Void> createReceipt(@PathVariable Long orderId) {
        generateReceipt.generateReceipt(orderId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid ChangeOrderStatusDTO request
    ) {
        createOrder.stateChange(orderId, request.getStatus());
        return ResponseEntity.noContent().build();
    }


}

