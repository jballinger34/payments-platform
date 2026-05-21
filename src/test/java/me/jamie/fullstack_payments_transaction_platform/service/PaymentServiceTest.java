package me.jamie.fullstack_payments_transaction_platform.service;

import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentCreatedEvent;
import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentUpdatedStatusEvent;
import me.jamie.fullstack_payments_transaction_platform.exception.PaymentNotFoundException;
import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;
import me.jamie.fullstack_payments_transaction_platform.kafka.PaymentEventProducer;
import me.jamie.fullstack_payments_transaction_platform.repository.PaymentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepo repo;
    @Mock
    PaymentEventProducer producer;

    @InjectMocks
    PaymentService service;

    //create
    @Test
    void testCreateFlow_SavesAndPublishesEvent(){
        String payer = "Payer";
        String payee = "Payee";
        BigDecimal amount = new BigDecimal("99.99");
        Payment payment = new Payment(payer, payee, amount, Currency.GBP);

        when(repo.save(any(Payment.class))).thenReturn(payment);

        Payment result = service.createPayment(payer,payee,amount,Currency.GBP);

        verify(repo, times(1)).save(any(Payment.class));
        verify(producer, times(1)).publishPaymentCreated(any(PaymentCreatedEvent.class));

        assertEquals(payment,result);
    }
    @Test
    void testCreateGivesPaymentId(){

        String payer = "Payer";
        String payee = "Payee";
        BigDecimal amount = new BigDecimal("99.99");
        Payment payment = new Payment(payer, payee, amount, Currency.GBP);
        when(repo.save(any(Payment.class))).thenReturn(payment);

        Payment result = service.createPayment(payer,payee,amount,Currency.GBP);

        assertNotNull(result);
        assertNotNull(result.getId());
    }
    @Test
    void testCreateSetsPaymentNames(){
        String payer = "Payer";
        String payee = "Payee";
        BigDecimal amount = new BigDecimal("99.99");

        Payment payment = new Payment(payer, payee, amount, Currency.GBP);
        when(repo.save(any(Payment.class))).thenReturn(payment);

        Payment result = service.createPayment(payer,payee,amount,Currency.GBP);
        assertEquals(payer, result.getPayerName());
        assertEquals(payee, result.getPayeeName());
    }
    @Test
    void testCreateSetsStatus(){
        String payer = "Payer";
        String payee = "Payee";
        BigDecimal amount = new BigDecimal("99.99");

        Payment payment = new Payment(payer, payee, amount, Currency.GBP);
        when(repo.save(any(Payment.class))).thenReturn(payment);

        Payment result = service.createPayment(payer,payee,amount,Currency.GBP);
        assertEquals(PaymentStatus.INITIATED, result.getStatus());
    }
    @Test
    void testCreateSetsCurrencyAndAmount(){
        String payer = "Payer";
        String payee = "Payee";
        BigDecimal amount = new BigDecimal("99.99");

        Payment payment = new Payment(payer, payee, amount, Currency.GBP);
        when(repo.save(any(Payment.class))).thenReturn(payment);

        Payment result = service.createPayment(payer,payee,amount,Currency.GBP);
        assertEquals(Currency.GBP, result.getCurrency());
        assertEquals(amount, result.getAmount());
    }
    @Test
    void testCreateNullPayerName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment(null, "Payee", new BigDecimal("10.00"), Currency.GBP));


        verify(repo, never()).save(any());
    }
    @Test
    void testCreateNullPayeeName(){
        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment("Payer", null, new BigDecimal("10.00"), Currency.GBP));


        verify(repo, never()).save(any());
    }
    @Test
    void testCreateNullAmount(){
        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment("Payer", "Payee", null, Currency.GBP));

        verify(repo, never()).save(any());
    }
    @Test
    void testCreateAmountBelow0(){
        BigDecimal amount = new BigDecimal("-1.00");

        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment("Payer", "Payee", amount, Currency.GBP));

        verify(repo, never()).save(any());
    }
    @Test
    void testCreateAmount0(){
        BigDecimal amount = BigDecimal.ZERO;

        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment("Payer", "Payee", amount, Currency.GBP));

        verify(repo, never()).save(any());
    }
    @Test
    void testCreateAmountNotRounded() {
        BigDecimal amount = new BigDecimal("10.999");

        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment("Payer", "Payee", amount, Currency.GBP));
        verify(repo, never()).save(any());
    }
    @Test
    void testCreateNoCurrency(){
        assertThrows(IllegalArgumentException.class,
                () -> service.createPayment("Payer", "Payee", new BigDecimal("10.00"), null));

        verify(repo, never()).save(any());
    }

    //get all
    @Test
    void testGetAllSuccess(){
        List<Payment> payments = List.of(
                new Payment("Payer1", "Payee1", new BigDecimal("10.00"), Currency.GBP),
                new Payment("Payer1", "Payee2", new BigDecimal("5.49"),Currency.USD)
        );

        when(repo.findAll()).thenReturn(payments);

        List<Payment> result = service.getAllPayments();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(repo, times(1)).findAll();
    }
    @Test
    void testGetAllPaymentsEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<Payment> result = service.getAllPayments();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repo, times(1)).findAll();
    }
    //get by id
    @Test
    void testGetPaymentSuccess() {
        String id = "123";
        Payment payment = new Payment("Payer", "Payee", new BigDecimal("10.01") ,Currency.GBP);
        when(repo.findById(id)).thenReturn(Optional.of(payment));

        Payment result = service.getPayment(id);

        assertNotNull(result);
        assertEquals(payment, result);

        verify(repo, times(1)).findById(id);
    }
    @Test
    void testGetPaymentNotFound() {
        String id = "999";
        when(repo.findById(id)).thenReturn(Optional.empty());


        PaymentNotFoundException e = assertThrows(PaymentNotFoundException.class, () -> service.getPayment(id));

        assertEquals("Payment " + id + " not found.", e.getMessage());

        verify(repo, times(1)).findById(id);
    }
    @Test
    void testGetPaymentNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.getPayment(null));

        verify(repo, never()).findById(any());
    }
    @Test
    void testGetPaymentBlankId() {
        assertThrows(IllegalArgumentException.class, () -> service.getPayment(" "));

        verify(repo, never()).findById(any());
    }

    //update status
    @Test
    void testUpdateStatusSuccess() {

        String id = "123";

        Payment payment = new Payment("Payer", "Payee",new BigDecimal("10.72"), Currency.GBP);

        when(repo.findById(id)).thenReturn(Optional.of(payment));
        when(repo.save(any(Payment.class))).thenReturn(payment);

        Payment result = service.updateStatus(id, PaymentStatus.COMPLETED);

        // Assert
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());

        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(payment);
        verify(producer, times(1)).publishPaymentStatusUpdated(any(PaymentUpdatedStatusEvent.class));
    }
    @Test
    void testUpdateNullStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus("123", null));

        verify(repo, never()).save(any());
    }
    @Test
    void testUpdateStatusPaymentNotFound() {

        when(repo.findById("999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                        () -> service.updateStatus("999", PaymentStatus.COMPLETED));

        assertEquals("Payment 999 not found.", exception.getMessage());

        verify(repo, never()).save(any());
    }
    @Test
    void testUpdateStatusInvalidTransition() {
        Payment payment = new Payment("123", "Jamie", "Amazon",
                new BigDecimal("32.12"), Currency.GBP, PaymentStatus.COMPLETED);


        when(repo.findById("123")).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class,
                        () -> service.updateStatus("123", PaymentStatus.INITIATED));

        verify(repo, never()).save(any());
    }
    //get by status
    @Test
    void testGetPaymentsByStatusSuccess() {

        Payment payment1 = new Payment("123", "Payer1", "Payee1", new BigDecimal("3.43"), Currency.GBP, PaymentStatus.INITIATED);
        Payment payment2 = new Payment("456", "Payer2", "Payee2", new BigDecimal("10.12"), Currency.USD, PaymentStatus.COMPLETED);
        Payment payment3 = new Payment("789", "Payer3", "Payee3", new BigDecimal("18.32"), Currency.GBP, PaymentStatus.COMPLETED);

        List<Payment> payments = List.of(payment1, payment2, payment3);

        when(repo.findAll()).thenReturn(payments);

        List<Payment> initiated = service.getPaymentsByStatus(PaymentStatus.INITIATED);

        assertNotNull(initiated);
        assertEquals(1, initiated.size());
        assertEquals(List.of(payment1), initiated);

        List<Payment> completed = service.getPaymentsByStatus(PaymentStatus.COMPLETED);

        assertNotNull(completed);
        assertEquals(2, completed.size());
        assertEquals(List.of(payment2, payment3), completed);
    }
    @Test
    void testGetPaymentsByStatus_NoneOfThatStatus() {
        //payment1 will be INITIALISED
        Payment payment1 = new Payment("Payer1", "Payee1", new BigDecimal("22.43"), Currency.GBP);
        when(repo.findAll()).thenReturn(List.of(payment1));

        List<Payment> result = service.getPaymentsByStatus(PaymentStatus.COMPLETED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetPaymentsByStatus_Empty() {

        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<Payment> result = service.getPaymentsByStatus(PaymentStatus.COMPLETED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetPaymentsByStatus_Null() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getPaymentsByStatus(null));
    }

    //get by payer name
    @Test
    void testGetPaymentsByPayerName_Success() {

        Payment payment1 = new Payment("Payer1", "Payee1", new BigDecimal("22.43"), Currency.GBP);
        Payment payment2 = new Payment("Payer1", "Payee2",new BigDecimal("14.37"), Currency.USD);
        Payment payment3 = new Payment("Payer2", "Payee3", new BigDecimal("82.12") ,Currency.USD);

        List<Payment> payments = List.of(payment1, payment2, payment3);

        when(repo.findAll()).thenReturn(payments);

        List<Payment> result = service.getPaymentsByPayerName("Payer1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(List.of(payment1, payment2), result);
    }
    @Test
    void testGetPaymentsByPayerName_Empty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<Payment> result = service.getPaymentsByPayerName("Payer1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetPaymentsByPayerName_NoPaymentsByPayer() {
        Payment payment1 = new Payment("Payer1", "Payee1", new BigDecimal("22.43"), Currency.GBP);
        when(repo.findAll()).thenReturn(List.of(payment1));

        List<Payment> result = service.getPaymentsByPayerName("Payer2");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetPaymentsByPayerName_Null() {
        assertThrows(IllegalArgumentException.class, () -> service.getPaymentsByPayerName(null));
    }







}