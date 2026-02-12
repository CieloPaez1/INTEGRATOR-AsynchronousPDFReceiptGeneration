package com.cielo.adapter.config;

import input.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import output.OrderOutput;
import output.PdfOutput;
import output.PendingTaskOutput;
import output.UserOutput;
import usecase.*;

import java.time.Clock;


@Configuration
public class UseCaseConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RegisterUserInput registerUser(UserOutput userOutput, Clock clock) {
        return new RegisterUser(userOutput, clock);
    }
    @Bean
    public ActivateUserInput activateUser(UserOutput userOutput, Clock clock) {
        return new ActivateUser(userOutput, clock);
    }

    @Bean
    public CreateOrderInput createOrder(OrderOutput orderOutput, Clock clock) {
        return new CreateOrder(orderOutput, clock);
    }

    @Bean
    public GenerateOrderReceiptPDFInput generateOrderReceiptPDF(
            OrderOutput orderOutput,
            PendingTaskOutput pendingTaskOutput,
            Clock clock
    ) {
        return new GenerateOrderReceiptPDF(orderOutput, pendingTaskOutput, clock);
    }

    @Bean
    public ProcessPendingReceiptsInput processPendingReceipts(
            PendingTaskOutput pendingTaskOutput,
            PdfOutput pdfOutput,
            Clock clock
    ) {
        return new ProcessPendingReceipts(pendingTaskOutput, pdfOutput, clock);
    }

}