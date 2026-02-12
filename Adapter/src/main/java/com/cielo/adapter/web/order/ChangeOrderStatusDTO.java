package com.cielo.adapter.web.order;

import enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeOrderStatusDTO {
    @NotNull
    private OrderStatus status;

    public ChangeOrderStatusDTO() {}

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
