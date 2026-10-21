package au.edu.rmit.selenium;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpiraTestConfiguration(
    url = "https://demo-au.spiraservice.net/runaria",
    login = "s4093120",
    password = "123456",
    projectId = 24,
    testSetId = 17
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoanRequestTest extends BaseTest {

    private WebDriverWait wait;
    private String sourceAccount;
    private BigDecimal availableBefore;
    private Map<String, BigDecimal> accountsBefore;

    @BeforeEach
    void prepareLoanRequest() throws InterruptedException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Login
        TestCustomer.ensureLoggedIn(driver);

        // Record existing accounts and the funding account balance
        accountsBefore = accountBalances();
        sourceAccount = accountsBefore.keySet().iterator().next();
        WebElement accountRow = driver.findElement(By.linkText(sourceAccount))
            .findElement(By.xpath("ancestor::tr"));
        availableBefore = money(accountRow.findElements(By.tagName("td")).get(2).getText());

        // Open Request Loan
        driver.findElement(By.linkText("Request Loan")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestLoanForm")));

        assertEquals(
            "Apply for a Loan",
            driver.findElement(By.cssSelector("#requestLoanForm h1")).getText().trim()
        );

        wait.until(d -> new Select(d
            .findElement(By.id("fromAccountId")))
            .getOptions()
            .stream().anyMatch(option -> sourceAccount.equals(option.getAttribute("value")))
        );
        Select fromAccount = new Select(driver.findElement(By.id("fromAccountId")));
        fromAccount.selectByValue(sourceAccount);
    }

    @Test
    @SpiraTestCase(testCaseId = 355)
    @Order(1)
    void requestLoanWithValidDetails() {
        BigDecimal loanAmount = new BigDecimal("100.00");
        BigDecimal downPayment = new BigDecimal("50.00");

        assertTrue(
            availableBefore.compareTo(downPayment) >= 0,
            "Test precondition failed: the funding account needs at least $50 available."
        );

        // Fill details and apply
        applyForLoan(loanAmount.toPlainString(), downPayment.toPlainString());

        // Verify approval and the new account number
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("loanStatus"), "Approved"));

        assertEquals("Approved", driver.findElement(By.id("loanStatus")).getText().trim());
        assertTrue(driver.findElement(By.id("loanRequestApproved")).isDisplayed());
        assertFalse(driver.findElement(By.id("loanRequestDenied")).isDisplayed());
        assertFalse(driver.findElement(By.id("requestLoanError")).isDisplayed());

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newAccountId")));
        String loanAccount = driver.findElement(By.id("newAccountId")).getText().trim();

        assertFalse(loanAccount.isBlank(), "An approved loan should have a new account number.");
        assertFalse(accountsBefore.containsKey(loanAccount));

        // Verify the loan balance and down payment deduction
        Map<String, BigDecimal> expectedAccounts = new LinkedHashMap<>(accountsBefore);
        expectedAccounts.put(sourceAccount, accountsBefore.get(sourceAccount).subtract(downPayment));
        expectedAccounts.put(loanAccount, loanAmount);

        assertEquals(
            expectedAccounts,
            accountBalances(),
            "Approval should create the funded loan account and deduct only the down payment."
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 356)
    @Order(2)
    void requestLoanWithInsufficientFunds() {
        // Set a down payment greater than all existing account balances
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (BigDecimal balance : accountsBefore.values()) {
            totalBalance = totalBalance.add(balance.max(BigDecimal.ZERO));
        }
        BigDecimal downPayment = totalBalance.max(availableBefore).add(new BigDecimal("100.00"));
        BigDecimal loanAmount = downPayment.multiply(BigDecimal.TEN);

        // Fill details and apply
        applyForLoan(loanAmount.toPlainString(), downPayment.toPlainString());

        // Verify denial and unchanged accounts
        assertDenied();
    }

    @Test
    @SpiraTestCase(testCaseId = 357)
    @Order(3)
    void requestLoanWithNegativeAmount() {
        // Fill details and apply
        applyForLoan("-100", "10");

        // Verify denial and unchanged accounts
        assertDenied();
    }

    @Test
    @SpiraTestCase(testCaseId = 358)
    @Order(4)
    void requestLoanWithoutAmountShowsError() {
        assertInvalidDetailsShowsError("", "10");
    }

    @Test
    @SpiraTestCase(testCaseId = 359)
    @Order(5)
    void requestLoanWithNonNumericAmountShowsError() {
        assertInvalidDetailsShowsError("abc", "10");
    }

    @Test
    @SpiraTestCase(testCaseId = 360)
    @Order(6)
    void requestLoanWithoutDownPaymentShowsError() {
        assertInvalidDetailsShowsError("1000", "");
    }

    @Test
    @SpiraTestCase(testCaseId = 361)
    @Order(7)
    void requestLoanWithZeroAmountShowsError() {
        assertInvalidDetailsShowsError("0", "10");
    }

    private void assertInvalidDetailsShowsError(String amount, String downPayment) {
        // Fill invalid details and apply
        applyForLoan(amount, downPayment);

        // Verify the current server error response and unchanged accounts
        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("requestLoanError"))
        );

        assertEquals("Error!", error.findElement(By.tagName("h1")).getText().trim());

        assertTrue(
            error.getText().contains("An internal error has occurred and has been logged."),
            "Malformed loan details currently produce a generic server error."
        );

        assertFalse(driver.findElement(By.id("requestLoanResult")).isDisplayed());
        assertFalse(driver.findElement(By.id("loanRequestApproved")).isDisplayed());

        assertEquals(
            accountsBefore,
            accountBalances(),
            "An invalid loan request must not create an account or change existing balances."
        );
    }

    private void applyForLoan(String amount, String downPayment) {
        driver.findElement(By.id("amount")).sendKeys(amount);
        driver.findElement(By.id("downPayment")).sendKeys(downPayment);
        driver.findElement(By.cssSelector("#requestLoanForm input[value='Apply Now']")).click();
    }

    private void assertDenied() {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("loanStatus"), "Denied"));
        WebElement explanation = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#loanRequestDenied .error"))
        );

        assertEquals("Denied", driver.findElement(By.id("loanStatus")).getText().trim());
        assertFalse(explanation.getText().isBlank(), "A denied loan should have an explanation.");
        assertFalse(driver.findElement(By.id("loanRequestApproved")).isDisplayed());
        assertFalse(driver.findElement(By.id("requestLoanError")).isDisplayed());

        assertEquals(
            accountsBefore,
            accountBalances(),
            "A denied loan must not create an account or change existing balances."
        );
    }

    private Map<String, BigDecimal> accountBalances() {
        driver.findElement(By.linkText("Accounts Overview")).click();
        wait.until(d -> {
            List<WebElement> rows = d.findElements(By.cssSelector("#accountTable tbody tr"));
            return !rows.isEmpty() && rows.get(rows.size() - 1).getText().contains("Total")
                && !d.findElements(By.cssSelector("#accountTable tbody tr td a")).isEmpty();
        });

        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        for (WebElement row : driver.findElements(By.cssSelector("#accountTable tbody tr"))) {
            List<WebElement> links = row.findElements(By.cssSelector("td a"));
            if (!links.isEmpty()) {
                String accountNumber = links.get(0).getText().trim();
                BigDecimal balance = money(row.findElements(By.tagName("td")).get(1).getText());
                balances.put(accountNumber, balance);
            }
        }
        return balances;
    }

    private BigDecimal money(String value) {
        String amount = value.replace("$", "").replace(",", "").trim();
        if (amount.startsWith("(") && amount.endsWith(")")) {
            amount = "-" + amount.substring(1, amount.length() - 1);
        }
        assertFalse(
            amount.isBlank(),
            "The account amount should be populated."
        );
        return new BigDecimal(amount);
    }
}
