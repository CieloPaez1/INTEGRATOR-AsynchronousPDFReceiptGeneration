package web.pendingTask;

import com.cielo.adapter.web.pendingTask.TaskController;
import input.ProcessPendingReceiptsInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private ProcessPendingReceiptsInput processPending;

    @InjectMocks
    private TaskController taskController;

    @Test
    void shouldReturnNoContentWhenNoPdfs() {

        when(processPending.process()).thenReturn(List.of());

        ResponseEntity<byte[]> result = taskController.downloadReceipts();

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(processPending).process();
    }

    @Test
    void shouldReturnSinglePdf() {

        byte[] pdf = "fake-pdf".getBytes();
        when(processPending.process()).thenReturn(List.of(pdf));

        ResponseEntity<byte[]> result = taskController.downloadReceipts();

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertArrayEquals(pdf, result.getBody());
        Assertions.assertTrue(
                result.getHeaders()
                        .getFirst(HttpHeaders.CONTENT_DISPOSITION)
                        .contains("receipt.pdf")
        );

        verify(processPending).process();
    }

    @Test
    void shouldReturnZipWhenMultiplePdfs() {

        byte[] pdf1 = "pdf1".getBytes();
        byte[] pdf2 = "pdf2".getBytes();

        when(processPending.process()).thenReturn(List.of(pdf1, pdf2));

        ResponseEntity<byte[]> result = taskController.downloadReceipts();

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertTrue(
                result.getHeaders()
                        .getFirst(HttpHeaders.CONTENT_DISPOSITION)
                        .contains("receipts.zip")
        );

        verify(processPending).process();
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() {

        when(processPending.process()).thenThrow(new RuntimeException());

        ResponseEntity<byte[]> result = taskController.downloadReceipts();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());

        verify(processPending).process();
    }
}
