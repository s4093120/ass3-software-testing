package au.edu.rmit.cucumber.steps;

import au.edu.rmit.cucumber.support.ScenarioContext;
import io.cucumber.java.en.Given;
import java.util.Map;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CustomerSteps {
    private final ScenarioContext context;

    public CustomerSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I am a newly registered bank customer")
    public void registerIndependentCustomer() {
        context.bank.registerNewCustomer();
        Map<String, BigDecimal> accounts = context.bank.accountBalances();
        assertFalse(accounts.isEmpty(), "The new customer needs an initial bank account.");
        context.sourceAccount = accounts.keySet().iterator().next();
    }
}
