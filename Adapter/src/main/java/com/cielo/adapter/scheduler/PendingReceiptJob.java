package com.cielo.adapter.scheduler;

import input.ProcessPendingReceiptsInput;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class PendingReceiptJob {

    private final ProcessPendingReceiptsInput processPendingReceipts;

    public PendingReceiptJob(ProcessPendingReceiptsInput processPendingReceipts) {
        this.processPendingReceipts = processPendingReceipts;
    }

    @Scheduled(fixedRate = 60000)
    public void run() {
        processPendingReceipts.process();
    }
}