package me.jamie.fullstack_payments_transaction_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jamie.fullstack_payments_transaction_platform.data.dto.PaymentDto;
import me.jamie.fullstack_payments_transaction_platform.data.request.PaymentRequest;
import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;
import me.jamie.fullstack_payments_transaction_platform.exception.PaymentNotFoundException;
import me.jamie.fullstack_payments_transaction_platform.exception.PersistenceException;
import me.jamie.fullstack_payments_transaction_platform.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import scala.concurrent.impl.FutureConvertersImpl;
import scala.concurrent.java8.FuturesConvertersImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;


    //createPayment
    @Test
    void testCreatePayment_Success_Returns201() throws Exception {
        PaymentRequest request = new PaymentRequest("Payer", "Payee", new BigDecimal("100.01"), Currency.GBP);

        Payment payment = new Payment("123", "Payer", "Payee", new BigDecimal("100.01"), Currency.GBP, PaymentStatus.INITIATED);
        when(paymentService.createPayment("Payer", "Payee", new BigDecimal("100.01"), Currency.GBP)).thenReturn(payment);


        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(paymentService, times(1)).createPayment(eq("Payer"), eq("Payee"), eq(new BigDecimal("100.01")), eq(Currency.GBP));
    }
    @Test
    void testCreatePayment_Success_ReturnsCorrectPaymentData() throws Exception {
        PaymentRequest request = new PaymentRequest("Payer", "Payee", new BigDecimal("100.01"), Currency.GBP);

        Payment payment = new Payment("123", "Payer", "Payee", new BigDecimal("100.01"), Currency.GBP, PaymentStatus.INITIATED);
        when(paymentService.createPayment("Payer", "Payee", new BigDecimal("100.01"), Currency.GBP)).thenReturn(payment);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.payerName").value("Payer"))
                .andExpect(jsonPath("$.payeeName").value("Payee"))
                .andExpect(jsonPath("$.amount").value(100.01))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.status").value("INITIATED"));

        verify(paymentService, times(1)).createPayment(eq("Payer"), eq("Payee"), eq(new BigDecimal("100.01")), eq(Currency.GBP));
    }
    @Test
    void testCreatePayment_InvalidJson_Returns400() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest());
        verify(paymentService, never()).createPayment(any(), any(), any(), any());
    }
    @Test
    void testCreatePayment_MissingFields_Returns400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).createPayment(any(), any(), any(), any());
    }

    //get all payments
    @Test
    void testGetAllPayments_Returns200() throws Exception {
        Payment payment = new Payment("Payer", "Payee",
                new BigDecimal("100.00"), Currency.GBP);

        when(paymentService.getAllPayments()).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).getAllPayments();
    }
    @Test
    void testGetAllPayments_ReturnsJsonArray() throws Exception {

        Payment payment = new Payment("Payer", "Payee", new BigDecimal("100.00"), Currency.GBP);

        when(paymentService.getAllPayments())
                .thenReturn(List.of(payment));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(paymentService).getAllPayments();
    }
    @Test
    void testGetAllPayments_ReturnsCorrectPaymentData() throws Exception {
        Payment payment = new Payment("Payer", "Payee", new BigDecimal("100.00"), Currency.GBP);

        when(paymentService.getAllPayments())
                .thenReturn(List.of(payment));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].payerName").value("Payer"))
                .andExpect(jsonPath("$[0].payeeName").value("Payee"))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("GBP"))
                .andExpect(jsonPath("$[0].status").value("INITIATED"));

        verify(paymentService).getAllPayments();
    }
    @Test
    void testGetAllPayments_EmptyList_ReturnsEmptyJsonArray() throws Exception {

        when(paymentService.getAllPayments())
                .thenReturn(List.of());

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(paymentService).getAllPayments();
    }

    @Test
    void testGetAllPayments_ServiceThrowsPersistenceException_Returns500() throws Exception {

        when(paymentService.getAllPayments()).thenThrow(new PersistenceException("Simulating DB failure"));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).getAllPayments();
    }

    //get payment by id
    @Test
    void testGetPaymentById_Success() throws Exception {
        Payment payment = new Payment("123","Payer", "Payee", new BigDecimal("100.00"), Currency.GBP, PaymentStatus.COMPLETED);

        when(paymentService.getPayment("123")).thenReturn(payment);

        mockMvc.perform(get("/payments/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payerName").value("Payer"))
                .andExpect(jsonPath("$.payeeName").value("Payee"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(paymentService, times(1)).getPayment("123");
    }

    @Test
    void testGetPaymentById_NotFound_Returns404() throws Exception {

        when(paymentService.getPayment("999")).thenThrow(new PaymentNotFoundException("999"));

        mockMvc.perform(get("/payments/999")).andExpect(status().isNotFound());

        verify(paymentService, times(1)).getPayment("999");
    }
    @Test
    void testGetPaymentById_ServiceThrowsPersistenceException_Returns500() throws Exception {

        when(paymentService.getPayment("123")).thenThrow(new PersistenceException("Simulating DB crash"));

        mockMvc.perform(get("/payments/123"))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).getPayment("123");
    }

    //update status
    @Test
    void testUpdateStatus_Success() throws Exception {
        PaymentStatus status = PaymentStatus.COMPLETED;
        Payment payment = new Payment("123", "Payer", "Payee", new BigDecimal("100.01"), Currency.GBP, status);
        when(paymentService.updateStatus("123", PaymentStatus.COMPLETED)).thenReturn(payment);

        mockMvc.perform(put("/payments/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).updateStatus(eq("123"), eq(status));
    }
    @Test
    void testUpdateStatus_Success_ReturnsCorrectPaymentData() throws Exception {

        Payment payment = new Payment("123", "Payer", "Payee", new BigDecimal("100.01"), Currency.GBP, PaymentStatus.COMPLETED);
        when(paymentService.updateStatus("123", PaymentStatus.COMPLETED)).thenReturn(payment);

        PaymentStatus status = PaymentStatus.COMPLETED;

        mockMvc.perform(put("/payments/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.payerName").value("Payer"))
                .andExpect(jsonPath("$.payeeName").value("Payee"))
                .andExpect(jsonPath("$.amount").value(100.01))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(paymentService, times(1)).updateStatus(eq("123"), eq(PaymentStatus.COMPLETED));

    }
    @Test
    void testUpdateStatus_InvalidEnum_Returns400() throws Exception {

        String invalidJson = "{ \"status\": \"NOT_A_STATUS\"";

        mockMvc.perform(put("/payments/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(paymentService, never())
                .updateStatus(any(), any());
    }
    @Test
    void testUpdateStatus_InvalidJson_Returns400() throws Exception {

        mockMvc.perform(put("/payments/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verify(paymentService, never())
                .updateStatus(any(), any());
    }
    @Test
    void testUpdateStatus_PaymentNotFound_Returns404() throws Exception {

        when(paymentService.updateStatus(eq("123"), eq(PaymentStatus.COMPLETED)))
                .thenThrow(new PaymentNotFoundException("Not found"));

        PaymentStatus status = PaymentStatus.COMPLETED;

        mockMvc.perform(put("/payments/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isNotFound());

        verify(paymentService, times(1)).updateStatus("123", PaymentStatus.COMPLETED);
    }
    @Test
    void testUpdateStatus_ServiceThrowsPersistenceException_Returns500() throws Exception {

        when(paymentService.updateStatus(any(), any())).thenThrow(new PersistenceException("DB failure"));
        PaymentStatus status = PaymentStatus.COMPLETED;

        mockMvc.perform(put("/payments/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).updateStatus("123", PaymentStatus.COMPLETED);
    }
    //get payments by status
    @Test
    void testGetPaymentsByStatus_Success() throws Exception {
        Payment payment = new Payment("111","Payer", "Payee", new BigDecimal("100.00"), Currency.GBP, PaymentStatus.COMPLETED);

        when(paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED))
                .thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/search/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].payerName").value("Payer"))
                .andExpect(jsonPath("$[0].payeeName").value("Payee"))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("GBP"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        verify(paymentService, times(1))
                .getPaymentsByStatus(PaymentStatus.COMPLETED);
    }
    @Test
    void testGetPaymentsByStatus_EmptyList_ReturnsEmptyArray() throws Exception {
        when(paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED)).thenReturn(List.of());

        mockMvc.perform(get("/payments/search/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(paymentService, times(1)).getPaymentsByStatus(PaymentStatus.COMPLETED);
    }
    @Test
    void testGetPaymentsByStatus_InvalidEnum_Returns400() throws Exception {

        mockMvc.perform(get("/payments/search/status/INVALID_ENUM"))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).getPaymentsByStatus(any());
    }
    @Test
    void testGetPaymentsByStatus_ServiceThrowsPersistenceException_Returns500() throws Exception {

        when(paymentService.getPaymentsByStatus(any())).thenThrow(new PersistenceException("Simulating DB failure"));

        mockMvc.perform(get("/payments/search/status/COMPLETED"))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).getPaymentsByStatus(PaymentStatus.COMPLETED);
    }


    //get payments by payer name
    @Test
    void testGetPaymentsByPayerName_Success() throws Exception {
        Payment payment = new Payment("111","Payer", "Payee", new BigDecimal("100.00"), Currency.GBP, PaymentStatus.COMPLETED);

        when(paymentService.getPaymentsByPayerName("Payer"))
                .thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/search/payer/Payer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].payerName").value("Payer"))
                .andExpect(jsonPath("$[0].payeeName").value("Payee"))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("GBP"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        verify(paymentService, times(1))
                .getPaymentsByPayerName("Payer");
    }
    @Test
    void testGetPaymentsByPayerName_EmptyList_ReturnsEmptyArray() throws Exception {
        when(paymentService.getPaymentsByPayerName("Payer")).thenReturn(List.of());

        mockMvc.perform(get("/payments/search/payer/Payer"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(paymentService, times(1)).getPaymentsByPayerName("Payer");
    }

    @Test
    void testGetPaymentsByPayerName_ServiceThrowsPersistenceException_Returns500() throws Exception {

        when(paymentService.getPaymentsByPayerName(any())).thenThrow(new PersistenceException("Simulating DB failure"));

        mockMvc.perform(get("/payments/search/payer/Payer"))
                .andExpect(status().isInternalServerError());

        verify(paymentService, times(1)).getPaymentsByPayerName("Payer");
    }



}
