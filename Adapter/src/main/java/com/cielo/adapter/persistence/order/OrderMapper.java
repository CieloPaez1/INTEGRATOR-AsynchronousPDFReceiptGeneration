package com.cielo.adapter.persistence.order;

import com.cielo.adapter.persistence.user.UserMapper;
import model.Order;

public class OrderMapper {

    public static OrderEntity coreToEntity(Order order) {
        if (order == null) return null;

        return new OrderEntity(
                order.getId(),
                UserMapper.coreToEntity(order.getUser()),
                order.getStatus(),
                order.getAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public static Order entityToCore(OrderEntity entity) {
        if (entity == null) return null;

        return Order.restore(
                entity.getId(),
                UserMapper.entityToCore(entity.getUser()),
                entity.getStatus(),
                entity.getAmount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }


}
