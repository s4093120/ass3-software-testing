package au.edu.rmit.cucumber.steps;

import au.edu.rmit.cucumber.support.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class BillPaySteps {
    private final ScenarioContext context;

    public BillPaySteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I have a paying account with at least {int} dollars available")
    public void preparePayment(int amount) {
        context.sourceBefore = context.bank.accountBalances().get(context.sourceAccount);
        assertTrue(context.sourceBefore.compareTo(BigDecimal.valueOf(amount)) >= 0,
                "Test precondition failed: the paying account does not have sufficient funds.");
    }

    @When("I pay {int} dollars to {string} using valid payee details")
    public void payBill(int amount, String payee) {
        context.bank.billPayment(context.sourceAccount, payee, Integer.toString(amount), "valid");
    }

    @When("I submit a bill payment with {string}")
    public void submitInvalidBill(String problem) {
        context.bank.billPayment(context.sourceAccount, "Demo Electricity", "25", problem);
    }

    @Then("I should receive confirmation of the {int} dollar payment to {string}")
    public void verifyPayment(int amount, String payee) {
        assertEquals("Bill Payment Complete", context.bank.billPaymentHeading());
        assertEquals(payee, context.bank.resultText("payeeName"));
        assertEquals(context.sourceAccount, context.bank.resultText("fromAccountId"));
        assertEquals(0, BigDecimal.valueOf(amount).compareTo(context.bank.resultMoney("amount")));
    }

    @Then("my paying account balance should decrease by {int} dollars")
    public void verifyPaymentDebit(int amount) {
        BigDecimal actual = context.bank.accountBalances().get(context.sourceAccount);
        assertEquals(0, context.sourceBefore.subtract(BigDecimal.valueOf(amount)).compareTo(actual),
                "A successful bill payment must debit the paying account.");
    }

    @Then("I should see the bill payment validation message {string}")
    public void verifyValidation(String message) {
        assertTrue(context.bank.visibleBillValidationMessages().contains(message),
                "Expected validation message: " + message);
        assertFalse(context.bank.isVisible("billpayResult"), "Invalid details must not show payment success.");
    }

    @Then("my paying account balance should remain unchanged")
    public void verifyNoPaymentDebit() {
        BigDecimal actual = context.bank.accountBalances().get(context.sourceAccount);
        assertEquals(0, context.sourceBefore.compareTo(actual), "Invalid payments must not debit money.");
    }
}
