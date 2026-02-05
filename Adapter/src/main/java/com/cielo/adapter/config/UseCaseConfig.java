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
    public RegisterUserInput registerUser(UserOutput u, Clock c) {
        return new RegisterUser(u, c);
    }

    @Bean
    public ActivateUserInput activateUser(UserOutput u, Clock c) {
        return new ActivateUser(u, c);
    }

    @Bean
    public CreateOrderInput createOrder(OrderOutput o, Clock c) {
        return new CreateOrder(o, c);
    }

    @Bean
    public GenerateOrderReceiptPDFInput generateReceipt(
            OrderOutput o,
            PendingTaskOutput p,
            Clock c) {
        return new GenerateOrderReceiptPDF(o, p, c);
    }

    @Bean
    public ProcessPendingReceiptsInput processReceipts(
            PendingTaskOutput p,
            PdfOutput pdf,
            Clock c) {
        return new ProcessPendingReceipts(p, pdf, c);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}