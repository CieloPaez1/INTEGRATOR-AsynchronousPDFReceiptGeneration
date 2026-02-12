package web.order;

import com.cielo.adapter.web.order.ChangeOrderStatusDTO;
import com.cielo.adapter.web.order.OrderController;
import com.cielo.adapter.web.order.OrderDTO;
import enums.OrderStatus;
import input.CreateOrderInput;
import input.GenerateOrderReceiptPDFInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private CreateOrderInput createOrder;

    @Mock
    private GenerateOrderReceiptPDFInput generateReceipt;

    @InjectMocks
    private OrderController orderController;

    @Test
    void shouldCreateOrderSuccessfully() {

        OrderDTO dto = new OrderDTO();
        dto.setAmount(new BigDecimal("100.00"));

        ResponseEntity<Void> response =
                orderController.createOrder(1L, dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(createOrder).createOrder(1L, new BigDecimal("100.00"));
    }

    @Test
    void shouldGenerateReceiptSuccessfully() {

        ResponseEntity<Void> response =
                orderController.createReceipt(1L);

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        verify(generateReceipt).generateReceipt(1L);
    }

    @Test
    void shouldChangeStatusSuccessfully() {

        ChangeOrderStatusDTO dto = new ChangeOrderStatusDTO();
        dto.setStatus(OrderStatus.APPROVED);

        ResponseEntity<Void> response =
                orderController.changeStatus(1L, dto);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(createOrder).stateChange(1L, OrderStatus.APPROVED);
    }
}
