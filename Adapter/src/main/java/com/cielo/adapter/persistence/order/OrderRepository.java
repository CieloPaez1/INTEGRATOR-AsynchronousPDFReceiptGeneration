package com.cielo.adapter.persistence.order;

import com.cielo.adapter.persistence.user.UserEntity;
import com.cielo.adapter.persistence.user.UserJPARepository;
import com.cielo.adapter.persistence.user.UserMapper;
import exception.OrderException;
import model.Order;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import output.OrderOutput;

@Repository
public class OrderRepository implements OrderOutput {
    private final OrderJPARepository orderjpa;
    private final UserJPARepository userjpa;

    @Autowired
    public OrderRepository(OrderJPARepository orderjpa,
                           UserJPARepository userjpa) {
        this.orderjpa = orderjpa;
        this.userjpa = userjpa;
    }


    @Override
    public boolean saveOrder(Order order) {
        if (order == null) return false;

        OrderEntity entity = OrderMapper.coreToEntity(order);

        UserEntity userEntity = userjpa.findById(order.getUser().getId())
                .orElseThrow(() -> new OrderException("User not found"));

        entity.setUser(userEntity);

        OrderEntity saved = orderjpa.save(entity);

        if (saved.getId() != null) {
            order.setId(saved.getId());
            return true;
        }

        return false;
    }

    @Override
    public User findUserById(Long id) {
        if (id == null) return null;

        return userjpa.findById(id)
                .map(UserMapper::entityToCore)
                .orElse(null);
    }

    @Override
    public Order findById(Long orderId) {
        if (orderId == null) return null;

        return orderjpa.findById(orderId)
                .map(OrderMapper::entityToCore)
                .orElse(null);
    }
}
