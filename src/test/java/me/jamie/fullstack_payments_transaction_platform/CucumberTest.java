package me.jamie.fullstack_payments_transaction_platform;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "me.jamie.fullstack_payments_transaction_platform"
)
public class CucumberTest {
}
