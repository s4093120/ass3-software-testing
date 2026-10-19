package au.edu.rmit.cucumber.pages;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/** All Selenium selectors and browser interactions live here, outside business scenarios. */
public class ParaBankPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public ParaBankPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(Long.getLong("wait.seconds", 35L)));
        this.baseUrl = System.getProperty("base.url", "https://parabank.parasoft.com/parabank/")
                .replaceAll("/+$", "") + "/";
    }

    public void registerNewCustomer() {
        driver.get(baseUrl + "register.htm");
        // Synthetic data only. Do not replace these values with personal identity information.
        fill(By.id("customer.firstName"), "Demo");
        fill(By.id("customer.lastName"), "Student");
        fill(By.id("customer.address.street"), "1 Test Street");
        fill(By.id("customer.address.city"), "Test City");
        fill(By.id("customer.address.state"), "VIC");
        fill(By.id("customer.address.zipCode"), "3000");
        fill(By.id("customer.phoneNumber"), "0400000000");
        fill(By.id("customer.ssn"), "000000000");
        fill(By.id("customer.username"),
                "bdd_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        String password = "Demo_" + UUID.randomUUID().toString().substring(0, 8);
        fill(By.id("customer.password"), password);
        fill(By.id("repeatedPassword"), password);
        click(By.cssSelector("input[value='Register']"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Log Out")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("rightPanel"),
                "Your account was created successfully"));
    }

    public Map<String, BigDecimal> accountBalances() {
        click(By.linkText("Accounts Overview"));
        wait.until(d -> {
            List<WebElement> rows = d.findElements(By.cssSelector("#accountTable tbody tr"));
            return !rows.isEmpty() && !d.findElements(By.cssSelector("#accountTable tbody tr td a")).isEmpty()
                    && rows.get(rows.size() - 1).getText().contains("Total");
        });
        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        for (WebElement row : driver.findElements(By.cssSelector("#accountTable tbody tr"))) {
            List<WebElement> links = row.findElements(By.cssSelector("td a"));
            if (!links.isEmpty()) {
                balances.put(links.get(0).getText().trim(), money(row.findElements(By.tagName("td")).get(1).getText()));
            }
        }
        return balances;
    }

    public String openSecondAccount(String fromAccount) {
        click(By.linkText("Open New Account"));
        selectAccount(By.id("fromAccountId"), fromAccount);
        new Select(visible(By.id("type"))).selectByVisibleText("SAVINGS");
        click(By.cssSelector("input[value='Open New Account']"));
        return waitForNonEmptyText(By.id("newAccountId"));
    }

    public void transfer(String amount, String source, String destination) {
        click(By.linkText("Transfer Funds"));
        selectAccount(By.id("fromAccountId"), source);
        selectAccount(By.id("toAccountId"), destination);
        fill(By.id("amount"), amount);
        click(By.cssSelector("input[value='Transfer']"));
        wait.until(d -> isVisible("showResult") || isVisible("showError") || !visibleErrors().isEmpty());
    }

    public String transferHeading() {
        return visible(By.cssSelector("#showResult h1")).getText().trim();
    }

    public String transferError() {
        wait.until(d -> isVisible("showError") || !visibleErrors().isEmpty());
        return String.join("\n", visibleErrors());
    }

    public void billPayment(String source, String payee, String amount, String problem) {
        click(By.linkText("Bill Pay"));
        fill(By.name("payee.name"), payee);
        fill(By.name("payee.address.street"), "2 Demo Street");
        fill(By.name("payee.address.city"), "Test City");
        fill(By.name("payee.address.state"), "VIC");
        fill(By.name("payee.address.zipCode"), "3000");
        fill(By.name("payee.phoneNumber"), "0400000000");
        fill(By.name("payee.accountNumber"), "12345");
        fill(By.name("verifyAccount"), "12345");
        fill(By.name("amount"), amount);
        selectAccount(By.name("fromAccountId"), source);
        switch (problem) {
            case "valid" -> { }
            case "a missing payee name" -> fill(By.name("payee.name"), "");
            case "mismatched payee account numbers" -> fill(By.name("verifyAccount"), "54321");
            case "a missing payment amount" -> fill(By.name("amount"), "");
            case "a non-numeric payment amount" -> fill(By.name("amount"), "one hundred");
            default -> throw new IllegalArgumentException("Unknown bill payment condition: " + problem);
        }
        click(By.cssSelector("input[value='Send Payment']"));
        wait.until(d -> isVisible("billpayResult") || isVisible("billpayError")
                || !visibleBillValidationMessages().isEmpty());
    }

    public String billPaymentHeading() {
        return visible(By.cssSelector("#billpayResult h1")).getText().trim();
    }

    public List<String> visibleBillValidationMessages() {
        return driver.findElements(By.cssSelector("#billpayForm .error")).stream()
                .filter(WebElement::isDisplayed).map(e -> e.getText().trim()).filter(s -> !s.isBlank()).toList();
    }

    public void requestLoan(String source, String amount, String downPayment) {
        click(By.linkText("Request Loan"));
        selectAccount(By.id("fromAccountId"), source);
        fill(By.id("amount"), amount);
        fill(By.id("downPayment"), downPayment);
        click(By.cssSelector("input[value='Apply Now']"));
        wait.until(d -> !textIfVisible(By.id("loanStatus")).isBlank() || isVisible("requestLoanError"));
    }

    public String loanStatus() {
        return waitForNonEmptyText(By.id("loanStatus"));
    }

    public String loanDenialExplanation() {
        return waitForNonEmptyText(By.cssSelector("#loanRequestDenied .error"));
    }

    public String resultText(String id) {
        return waitForNonEmptyText(By.id(id));
    }

    public BigDecimal resultMoney(String id) {
        return money(resultText(id));
    }

    public boolean isVisible(String id) {
        return driver.findElements(By.id(id)).stream().anyMatch(WebElement::isDisplayed);
    }

    private List<String> visibleErrors() {
        return driver.findElements(By.cssSelector("#rightPanel .error")).stream()
                .filter(WebElement::isDisplayed).map(e -> e.getText().trim()).filter(s -> !s.isBlank()).toList();
    }

    private void selectAccount(By locator, String account) {
        wait.until(d -> {
            try {
                Select select = new Select(d.findElement(locator));
                return select.getOptions().stream().anyMatch(option -> account.equals(option.getAttribute("value")));
            } catch (org.openqa.selenium.NoSuchElementException | StaleElementReferenceException e) {
                return false;
            }
        });
        new Select(visible(locator)).selectByValue(account);
    }

    private WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void fill(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        if (!text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private String waitForNonEmptyText(By locator) {
        return wait.until(d -> {
            String text = textIfVisible(locator);
            return text.isBlank() ? null : text;
        });
    }

    private String textIfVisible(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.isEmpty() || !elements.get(0).isDisplayed() ? "" : elements.get(0).getText().trim();
    }

    private static BigDecimal money(String text) {
        String clean = text.replace("$", "").replace(",", "").trim();
        if (clean.startsWith("(") && clean.endsWith(")")) {
            clean = "-" + clean.substring(1, clean.length() - 1);
        }
        return new BigDecimal(clean);
    }
}
