package me.jamie.fullstack_payments_transaction_platform.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import me.jamie.fullstack_payments_transaction_platform.data.dto.PaymentDto;
import me.jamie.fullstack_payments_transaction_platform.data.request.PaymentRequest;
import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class CreatePaymentSteps {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult result;

    @When("the payments system is running")
    public void whenPaymentsSystemRunning(){

    }
    @When("I submit a payment of {string} GBP from {string} to {string}")
    public void whenSubmitPayment(String amount, String payer, String payee) throws Exception {
        PaymentRequest request = new PaymentRequest(payer, payee, new BigDecimal(amount), Currency.GBP);
        result = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }
    @Then("the response status should be {int}")
    public void checkHttpStatus(int expectedStatus) throws Exception {
        assertEquals(expectedStatus, result.getResponse().getStatus());
    }
    @Then("the payment should have status {string}")
    public void checkPaymentStatus(String expectedStatus) throws Exception {
        PaymentDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentDto.class);
        assertEquals(expectedStatus, dto.status().name());
    }

    @Then("the payment should have an ID")
    public void paymentShouldHaveId() throws Exception {
        PaymentDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentDto.class);
        assertNotNull(dto.id());
        assertFalse(dto.id().isBlank());
    }

}
