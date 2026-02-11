package com.cielo.adapter.web.pendingTask;

import input.ProcessPendingReceiptsInput;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final ProcessPendingReceiptsInput processPending;

    public TaskController(ProcessPendingReceiptsInput processPending) {
        this.processPending = processPending;
    }

    @GetMapping("/receipt")
    public ResponseEntity<byte[]> downloadReceipts() {

        List<byte[]> pdfs = processPending.process();

        if (pdfs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }



        try {


            if (pdfs.size() == 1) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdfs.get(0));
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);

            int count = 1;
            for (byte[] pdf : pdfs) {
                ZipEntry entry = new ZipEntry("receipt-" + count++ + ".pdf");
                zipOut.putNextEntry(entry);
                zipOut.write(pdf);
                zipOut.closeEntry();
            }

            zipOut.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipts.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
