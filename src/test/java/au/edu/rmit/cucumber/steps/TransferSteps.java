package au.edu.rmit.cucumber.steps;

import au.edu.rmit.cucumber.support.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class TransferSteps {
    private final ScenarioContext context;

    public TransferSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I have two different accounts available for a transfer")
    public void createDestinationAccount() {
        context.destinationAccount = context.bank.openSecondAccount(context.sourceAccount);
        assertNotEquals(context.sourceAccount, context.destinationAccount);
        Map<String, BigDecimal> balances = context.bank.accountBalances();
        context.sourceBefore = balances.get(context.sourceAccount);
        context.destinationBefore = balances.get(context.destinationAccount);
        assertNotNull(context.sourceBefore);
        assertNotNull(context.destinationBefore);
    }

    @Given("my source account has at least {int} dollars available")
    public void verifySufficientFunds(int amount) {
        assertTrue(context.sourceBefore.compareTo(BigDecimal.valueOf(amount)) >= 0,
                "Test precondition failed: insufficient initial funds for the successful transfer.");
    }

    @When("I transfer {int} dollars from my source account to my destination account")
    public void transferAmount(int amount) {
        context.bank.transfer(Integer.toString(amount), context.sourceAccount, context.destinationAccount);
    }

    @When("I attempt a transfer using {string} as the amount")
    public void transferInvalidAmount(String amount) {
        context.bank.transfer(amount, context.sourceAccount, context.destinationAccount);
    }

    @Then("I should receive confirmation of the {int} dollar transfer")
    public void verifyTransferConfirmation(int amount) {
        assertEquals("Transfer Complete!", context.bank.transferHeading());
        assertEquals(context.sourceAccount, context.bank.resultText("fromAccountIdResult"));
        assertEquals(context.destinationAccount, context.bank.resultText("toAccountIdResult"));
        assertEquals(0, BigDecimal.valueOf(amount).compareTo(context.bank.resultMoney("amountResult")));
    }

    @Then("my source balance should decrease by {int} dollars")
    public void verifySourceDebit(int amount) {
        BigDecimal actual = context.bank.accountBalances().get(context.sourceAccount);
        assertEquals(0, context.sourceBefore.subtract(BigDecimal.valueOf(amount)).compareTo(actual),
                "Source account must be debited by the requested amount.");
    }

    @Then("my destination balance should increase by {int} dollars")
    public void verifyDestinationCredit(int amount) {
        BigDecimal actual = context.bank.accountBalances().get(context.destinationAccount);
        assertEquals(0, context.destinationBefore.add(BigDecimal.valueOf(amount)).compareTo(actual),
                "Destination account must be credited by the requested amount.");
    }

    @Then("the transfer should be unsuccessful with an error message")
    public void verifyRejectedTransfer() {
        assertFalse(context.bank.transferError().isBlank(), "An unsuccessful transfer needs a visible error.");
        assertFalse(context.bank.isVisible("showResult"), "A rejected transfer must not show success.");
    }

    @Then("both account balances should remain unchanged")
    public void verifyNoMoneyMoved() {
        Map<String, BigDecimal> balances = context.bank.accountBalances();
        assertAll("Rejected transfers must have no balance changes",
                () -> assertEquals(0, context.sourceBefore.compareTo(balances.get(context.sourceAccount))),
                () -> assertEquals(0, context.destinationBefore.compareTo(balances.get(context.destinationAccount))));
    }
}
