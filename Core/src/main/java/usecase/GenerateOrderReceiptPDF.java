package usecase;

import enums.OrderStatus;
import exception.OrderException;
import input.GenerateOrderReceiptPDFInput;
import model.Order;
import model.PendingTask;
import output.OrderOutput;
import output.PendingTaskOutput;

import java.time.Clock;
import java.time.LocalDateTime;

public class GenerateOrderReceiptPDF implements GenerateOrderReceiptPDFInput {
    private final OrderOutput orderOutput;
    private final PendingTaskOutput pendingTaskOutput;
    private final Clock clock;

    public GenerateOrderReceiptPDF(OrderOutput orderOutput, PendingTaskOutput pendingTaskOutput, Clock clock) {
        this.orderOutput = orderOutput;
        this.pendingTaskOutput = pendingTaskOutput;
        this.clock = clock;
    }

    @Override
    public void generateReceipt(Long orderId) {
        if (orderId == null) {
            throw new OrderException("Order not found");
        }

        Order order = orderOutput.findById(orderId);

        if (order == null) {
            throw new OrderException("Order not found");
        }

        if (pendingTaskOutput.existsPendingForOrder(orderId)) {
            throw new OrderException("Pending receipt task already exists for this order");
        }
        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new OrderException("Only APPROVED orders can generate receipts");
        }


        LocalDateTime now = LocalDateTime.now(clock);
        PendingTask task = PendingTask.factory(order, now);

        if (!pendingTaskOutput.save(task)) {
            throw new OrderException("Could not create pending task");
        }
    }
}
