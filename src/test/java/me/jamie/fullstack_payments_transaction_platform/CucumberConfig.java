package me.jamie.fullstack_payments_transaction_platform;

import io.cucumber.spring.CucumberContextConfiguration;
import me.jamie.fullstack_payments_transaction_platform.kafka.PaymentEventProducer;
import me.jamie.fullstack_payments_transaction_platform.kafka.TempConsumer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CucumberConfig {
    @MockitoBean
    private PaymentEventProducer paymentEventProducer;
    @MockitoBean
    private TempConsumer tempConsumer;

}
