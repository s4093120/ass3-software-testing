package au.edu.rmit.selenium;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpiraTestConfiguration(
    url = "https://demo-au.spiraservice.net/runaria",
    login = "s3923470",
    password = "123456",
    projectId = 24,
    testSetId = 15
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillPayTest extends BaseTest {

    private static final BigDecimal PAYMENT_AMOUNT = new BigDecimal("25.00");
    private static final String PAYEE_NAME = "Demo Electricity";

    private WebDriverWait wait;
    private String sourceAccount;
    private BigDecimal balanceBefore;

    @BeforeEach
    void prepareBillPayment() throws InterruptedException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Login and open Accounts Overview
        TestCustomer.ensureLoggedIn(driver);
        driver.findElement(By.linkText("Accounts Overview")).click();

        // Select an account with enough funds and record its balance
        for (WebElement row : accountRows()) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            BigDecimal availableAmount = money(cells.get(2).getText());
            if (availableAmount.compareTo(PAYMENT_AMOUNT) >= 0) {
                sourceAccount = row.findElement(By.cssSelector("td a")).getText().trim();
                balanceBefore = money(cells.get(1).getText());
                break;
            }
        }
        assertNotNull(
            sourceAccount,
            "Test precondition failed: no account has at least $25 available."
        );

        // Open Bill Pay
        driver.findElement(By.linkText("Bill Pay")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("billpayForm")));
        assertEquals(
            "Bill Payment Service",
            driver.findElement(By.cssSelector("#billpayForm h1")).getText().trim()
        );
        wait.until(d -> new Select(d
            .findElement(By.name("fromAccountId")))
            .getOptions()
            .stream().anyMatch(option -> sourceAccount.equals(option.getAttribute("value")))
        );
        Select fromAccount = new Select(driver.findElement(By.name("fromAccountId")));
        fromAccount.selectByValue(sourceAccount);

        // Fill details
        driver.findElement(By.name("payee.name")).sendKeys(PAYEE_NAME);
        driver.findElement(By.name("payee.address.street")).sendKeys("2 Demo Street");
        driver.findElement(By.name("payee.address.city")).sendKeys("Test City");
        driver.findElement(By.name("payee.address.state")).sendKeys("VIC");
        driver.findElement(By.name("payee.address.zipCode")).sendKeys("3000");
        driver.findElement(By.name("payee.phoneNumber")).sendKeys("0400000000");
        driver.findElement(By.name("payee.accountNumber")).sendKeys("12345");
        driver.findElement(By.name("verifyAccount")).sendKeys("12345");
        driver.findElement(By.name("amount")).sendKeys(PAYMENT_AMOUNT.toPlainString());
    }

    @Test
    @SpiraTestCase(testCaseId = 350)
    @Order(1)
    public void payBillWithValidDetails() {
        // Send payment
        driver.findElement(By.cssSelector("#billpayForm input[value='Send Payment']")).click();

        // Verify payment confirmation
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("billpayResult")));

        assertEquals(
            "Bill Payment Complete",
            driver.findElement(By.cssSelector("#billpayResult h1")).getText().trim()
        );

        assertEquals(
            PAYEE_NAME,
            driver.findElement(By.id("payeeName")).getText().trim()
        );

        assertEquals(
            sourceAccount,
            driver.findElement(By.id("fromAccountId")).getText().trim()
        );

        BigDecimal confirmedAmount = money(driver.findElement(By.id("amount")).getText());

        assertEquals(
            0,
            PAYMENT_AMOUNT.compareTo(confirmedAmount),
            "The confirmation should show the submitted payment amount."
        );
        assertFalse(driver.findElement(By.id("billpayError")).isDisplayed());

        // Verify the payment was deducted
        assertBalance(balanceBefore.subtract(PAYMENT_AMOUNT));
    }

    @Test
    @SpiraTestCase(testCaseId = 351)
    @Order(2)
    public void payBillWithoutPayeeName() {
        // Remove the payee name and send payment
        driver.findElement(By.name("payee.name")).clear();
        driver.findElement(By.cssSelector("#billpayForm input[value='Send Payment']")).click();

        // Verify validation and unchanged balance
        assertRejected("validationModel-name", "Payee name is required.");
    }

    @Test
    @SpiraTestCase(testCaseId = 352)
    @Order(3)
    public void payBillWithMismatchedAccountNumbers() {
        // Enter a different account number and send payment
        WebElement verifyAccount = driver.findElement(By.name("verifyAccount"));
        verifyAccount.clear();
        verifyAccount.sendKeys("54321");
        driver.findElement(By.cssSelector("#billpayForm input[value='Send Payment']")).click();

        // Verify validation and unchanged balance
        assertRejected(
            "validationModel-verifyAccount-mismatch",
            "The account numbers do not match."
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 353)
    @Order(4)
    public void payBillWithoutAmount() {
        // Remove the amount and send payment
        driver.findElement(By.name("amount")).clear();
        driver.findElement(By.cssSelector("#billpayForm input[value='Send Payment']")).click();

        // Verify validation and unchanged balance
        assertRejected("validationModel-amount-empty", "The amount cannot be empty.");
    }

    @Test
    @SpiraTestCase(testCaseId = 354)
    @Order(5)
    public void payBillWithNonNumericAmount() {
        // Enter a non-numeric amount and send payment
        WebElement amount = driver.findElement(By.name("amount"));
        amount.clear();
        amount.sendKeys("abc");
        driver.findElement(By.cssSelector("#billpayForm input[value='Send Payment']")).click();

        // Verify validation and unchanged balance
        assertRejected("validationModel-amount-invalid", "Please enter a valid amount.");
    }

    private void assertRejected(String validationId, String message) {
        WebElement validation = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id(validationId))
        );

        assertEquals(message, validation.getText().trim());

        assertFalse(
            driver.findElement(By.id("billpayResult")).isDisplayed(),
            "Invalid payment details must not show a successful payment."
        );

        assertFalse(
            driver.findElement(By.id("billpayError")).isDisplayed(),
            "Invalid payment details should show validation, not a server error."
        );

        assertBalance(balanceBefore);
    }

    private void assertBalance(BigDecimal expectedBalance) {
        driver.findElement(By.linkText("Accounts Overview")).click();
        BigDecimal actualBalance = null;
        for (WebElement row : accountRows()) {
            String accountNumber = row.findElement(By.cssSelector("td a")).getText().trim();
            if (sourceAccount.equals(accountNumber)) {
                actualBalance = money(row.findElements(By.tagName("td")).get(1).getText());
                break;
            }
        }
        assertNotNull(
            actualBalance,
            "The paying account is missing from Accounts Overview."
        );

        assertEquals(
            0,
            expectedBalance.compareTo(actualBalance),
            "Unexpected balance for account " + sourceAccount
                + ": expected " + expectedBalance + " but was " + actualBalance
        );
    }

    private List<WebElement> accountRows() {
        wait.until(d -> {
            List<WebElement> rows = d.findElements(By.cssSelector("#accountTable tbody tr"));
            return !rows.isEmpty() && rows.get(rows.size() - 1).getText().contains("Total")
                && !d.findElements(By.cssSelector("#accountTable tbody tr td a")).isEmpty();
        });
        return driver.findElements(By.cssSelector("#accountTable tbody tr"))
            .stream()
            .filter(row -> !row.findElements(By.cssSelector("td a")).isEmpty())
            .toList();
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
