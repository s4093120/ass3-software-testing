package au.edu.rmit.selenium;

import java.time.Duration;
import java.util.List;

import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.Assertions;
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
    login = "s3923470",
    password = "123456",
    projectId = 24,
    testSetId = 16
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FindTransactionsTest extends BaseTest {

    private WebDriverWait wait;
    private String accountId;
    private String transactionId;
    private String transactionDate;
    private String description;

    @BeforeEach
    void prepareTransactionSearch() throws InterruptedException {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Login and open Find Transactions
        TestCustomer.ensureLoggedIn(driver);
        driver.findElement(By.linkText("Find Transactions")).click();
        wait.until(d -> !new Select(d.findElement(By.id("accountId"))).getOptions().isEmpty());
        Select account = new Select(driver.findElement(By.id("accountId")));
        accountId = account.getFirstSelectedOption().getAttribute("value");
    }

    private void preparePayment() {
        // Create a known payment to search for
        driver.findElement(By.linkText("Bill Pay")).click();
        wait.until(d -> new Select(d.findElement(By.name("fromAccountId"))).getOptions()
            .stream().anyMatch(option -> accountId.equals(option.getAttribute("value")))
        );
        Select fromAccount = new Select(driver.findElement(By.name("fromAccountId")));
        fromAccount.selectByValue(accountId);
        String payee = "Search " + TestCustomer.newUsername();
        description = "Bill Payment to " + payee;

        driver.findElement(By.name("payee.name")).sendKeys(payee);
        driver.findElement(By.name("payee.address.street")).sendKeys("2 Demo Street");
        driver.findElement(By.name("payee.address.city")).sendKeys("Test City");
        driver.findElement(By.name("payee.address.state")).sendKeys("VIC");
        driver.findElement(By.name("payee.address.zipCode")).sendKeys("3000");
        driver.findElement(By.name("payee.phoneNumber")).sendKeys("0400000000");
        driver.findElement(By.name("payee.accountNumber")).sendKeys("12345");
        driver.findElement(By.name("verifyAccount")).sendKeys("12345");
        driver.findElement(By.name("amount")).sendKeys("25.00");
        driver.findElement(By.cssSelector("#billpayForm input[value='Send Payment']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("billpayResult")));
        accountId = driver.findElement(By.id("fromAccountId")).getText().trim();

        // Read the actual transaction ID and date from Account Activity
        driver.findElement(By.linkText("Accounts Overview")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(accountId))).click();
        WebElement transaction = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.linkText(description))
        );
        String transactionLink = transaction.getAttribute("href");
        Assertions.assertNotNull(transactionLink);

        transactionId = transactionLink.substring(transactionLink.lastIndexOf('=') + 1);
        WebElement row = transaction.findElement(By.xpath("ancestor::tr"));
        transactionDate = row.findElements(By.tagName("td")).get(0).getText().trim();

        // Open Find Transactions and select the payment account
        driver.findElement(By.linkText("Find Transactions")).click();
        wait.until(d -> new Select(d
            .findElement(By.id("accountId")))
            .getOptions()
            .stream().anyMatch(option -> accountId.equals(option.getAttribute("value")))
        );
        new Select(driver.findElement(By.id("accountId"))).selectByValue(accountId);
    }

    @Test
    @SpiraTestCase(testCaseId = 362)
    @Order(1)
    public void findTransactionById() {
        preparePayment();

        // Search using the payment transaction ID
        driver.findElement(By.id("transactionId")).sendKeys(transactionId);
        driver.findElement(By.id("findById")).click();

        // Verify the matching payment is the only result
        assertPaymentFound();
        assertEquals(1, driver.findElements(By.cssSelector("#transactionBody tr")).size());
    }

    @Test
    @SpiraTestCase(testCaseId = 363)
    @Order(2)
    public void findTransactionsByDate() {
        preparePayment();

        // Search using the date recorded by the website
        driver.findElement(By.id("transactionDate")).sendKeys(transactionDate);
        driver.findElement(By.id("findByDate")).click();

        // Verify the payment and every returned date
        assertPaymentFound();
        for (WebElement row : driver.findElements(By.cssSelector("#transactionBody tr"))) {
            assertEquals(transactionDate, row.findElements(By.tagName("td")).get(0).getText().trim());
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 364)
    @Order(3)
    public void findTransactionsByDateRange() {
        preparePayment();

        // Search a date range including the payment date at both boundaries
        driver.findElement(By.id("fromDate")).sendKeys(transactionDate);
        driver.findElement(By.id("toDate")).sendKeys(transactionDate);
        driver.findElement(By.id("findByDateRange")).click();

        // Verify the payment and that results stay within the selected day
        assertPaymentFound();
        for (WebElement row : driver.findElements(By.cssSelector("#transactionBody tr"))) {
            assertEquals(transactionDate, row.findElements(By.tagName("td")).get(0).getText().trim());
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 365)
    @Order(4)
    public void findTransactionsByAmount() {
        preparePayment();

        // Search using the payment amount
        driver.findElement(By.id("amount")).sendKeys("25.00");
        driver.findElement(By.id("findByAmount")).click();

        // Verify the payment and every returned amount
        assertPaymentFound();
        for (WebElement row : driver.findElements(By.cssSelector("#transactionBody tr"))) {
            assertEquals("$25.00", row.findElements(By.tagName("td")).get(2).getText().trim());
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 366)
    @Order(5)
    public void findTransactionsWithNoMatchingAmount() {
        // Search for an amount that does not match the test payments
        driver.findElement(By.id("amount")).sendKeys("987654321.09");
        driver.findElement(By.id("findByAmount")).click();

        // Verify the results table is empty without a server error
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultContainer")));
        assertTrue(driver.findElements(By.cssSelector("#transactionBody tr")).isEmpty());
        assertFalse(driver.findElement(By.id("errorContainer")).isDisplayed());
    }

    @Test
    @SpiraTestCase(testCaseId = 367)
    @Order(6)
    public void findTransactionsWithoutTransactionId() {
        assertMissingSearchValue("findById", "transactionIdError", "Invalid transaction ID");
    }

    @Test
    @SpiraTestCase(testCaseId = 368)
    @Order(7)
    public void findTransactionsWithoutDate() {
        assertMissingSearchValue("findByDate", "transactionDateError", "Invalid date format");
    }

    @Test
    @SpiraTestCase(testCaseId = 369)
    @Order(8)
    public void findTransactionsWithoutDateRange() {
        assertMissingSearchValue("findByDateRange", "dateRangeError", "Invalid date format");
    }

    @Test
    @SpiraTestCase(testCaseId = 370)
    @Order(9)
    public void findTransactionsWithoutAmount() {
        assertMissingSearchValue("findByAmount", "amountError", "Invalid amount");
    }

    private void assertMissingSearchValue(String buttonId, String errorId, String message) {
        // Search without entering the required value
        driver.findElement(By.id(buttonId)).click();

        // Verify field validation is shown instead of results
        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id(errorId))
        );
        assertEquals(message, error.getText().trim());
        assertFalse(driver.findElement(By.id("resultContainer")).isDisplayed());
        assertFalse(driver.findElement(By.id("errorContainer")).isDisplayed());
    }

    private void assertPaymentFound() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultContainer")));
        WebElement transaction = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.linkText(description))
        );
        List<WebElement> cells = transaction.findElement(By.xpath("ancestor::tr"))
            .findElements(By.tagName("td"));

        assertEquals(
            "Transaction Results",
            driver.findElement(By.cssSelector("#resultContainer h1")).getText().trim()
        );

        assertTrue(
            transaction.getAttribute("href").endsWith("id=" + transactionId),
            "The result should link to the payment created by this test."
        );

        assertEquals(transactionDate, cells.get(0).getText().trim());
        assertEquals("$25.00", cells.get(2).getText().trim());
        assertFalse(driver.findElement(By.id("errorContainer")).isDisplayed());
    }
}
