package au.edu.rmit.cucumber.steps;

import au.edu.rmit.cucumber.support.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class LoanSteps {
    private final ScenarioContext context;

    public LoanSteps(ScenarioContext context) {
        this.context = context;
    }

    private void snapshotAccounts() {
        context.accountsBeforeLoan = context.bank.accountBalances();
        context.sourceBefore = context.accountsBeforeLoan.get(context.sourceAccount);
    }

    @Given("my account has at least {int} dollars available for a down payment")
    public void verifyDownPaymentFunds(int amount) {
        snapshotAccounts();
        assertTrue(context.sourceBefore.compareTo(BigDecimal.valueOf(amount)) >= 0,
                "Test precondition failed: insufficient funds for the down payment.");
    }

    @When("I request a loan of {int} dollars with a down payment of {int} dollars")
    public void requestLoan(int amount, int downPayment) {
        context.bank.requestLoan(context.sourceAccount, Integer.toString(amount), Integer.toString(downPayment));
    }

    @When("I request a loan with a down payment greater than all my available funds")
    public void requestUnaffordableLoan() {
        snapshotAccounts();
        BigDecimal total = context.accountsBeforeLoan.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal downPayment = total.max(BigDecimal.ZERO).add(new BigDecimal("100.00"));
        BigDecimal amount = downPayment.multiply(BigDecimal.TEN);
        context.bank.requestLoan(context.sourceAccount, amount.toPlainString(), downPayment.toPlainString());
    }

    @When("I request a loan using {string} as the amount and {string} as the down payment")
    public void requestLoanWithValues(String amount, String downPayment) {
        snapshotAccounts();
        context.bank.requestLoan(context.sourceAccount, amount, downPayment);
    }

    @Then("my loan request should be approved")
    public void verifyApproval() {
        assertEquals("Approved", context.bank.loanStatus());
        assertTrue(context.bank.isVisible("loanRequestApproved"));
        context.loanAccount = context.bank.resultText("newAccountId");
        assertFalse(context.loanAccount.isBlank(), "An approved loan needs a new account number.");
        assertFalse(context.accountsBeforeLoan.containsKey(context.loanAccount));
    }

    @Then("a new loan account should contain {int} dollars")
    public void verifyNewLoanAccount(int amount) {
        Map<String, BigDecimal> accounts = context.bank.accountBalances();
        assertEquals(context.accountsBeforeLoan.size() + 1, accounts.size());
        assertNotNull(accounts.get(context.loanAccount));
        assertEquals(0, BigDecimal.valueOf(amount).compareTo(accounts.get(context.loanAccount)));
    }

    @Then("my funding account balance should decrease by {int} dollars")
    public void verifyDownPaymentDebit(int amount) {
        BigDecimal actual = context.bank.accountBalances().get(context.sourceAccount);
        assertEquals(0, context.sourceBefore.subtract(BigDecimal.valueOf(amount)).compareTo(actual));
    }

    @Then("my loan request should be denied with an explanation")
    public void verifyDenial() {
        assertEquals("Denied", context.bank.loanStatus());
        assertFalse(context.bank.loanDenialExplanation().isBlank());
        assertFalse(context.bank.isVisible("loanRequestApproved"));
    }

    @Then("I should see the loan request error {string}")
    public void verifyLoanRequestError(String message) {
        assertTrue(context.bank.isVisible("requestLoanError"), "A loan form error must be displayed.");
        assertTrue(context.bank.resultText("requestLoanError").contains(message),
                "Expected the loan request error to contain: " + message);
        assertFalse(context.bank.isVisible("loanRequestApproved"),
                "A rejected loan request must not show an approval.");
    }

    @Then("no new loan account should be created")
    public void verifyNoNewLoanAccount() {
        assertEquals(context.accountsBeforeLoan.keySet(), context.bank.accountBalances().keySet());
    }

    @Then("my funding account balance should remain unchanged")
    public void verifyNoDownPaymentDebit() {
        BigDecimal actual = context.bank.accountBalances().get(context.sourceAccount);
        assertEquals(0, context.sourceBefore.compareTo(actual));
    }
}
