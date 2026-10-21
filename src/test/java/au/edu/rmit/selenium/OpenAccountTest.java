package au.edu.rmit.selenium;

import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// IMPORT DO NOT TOUCH, SETS ORDER OF TEST EXECUTION
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@SpiraTestConfiguration(
        url = "https://demo-au.spiraservice.net/runaria",
        login = "s3923470",
        password = "123456",
        projectId = 24,
        testSetId = 13
)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
class OpenAccountTest extends BaseTest {

    @Test
    @SpiraTestCase(testCaseId = 324)
    @Order(1)
    public void OpenNewAccountValidChecking() throws InterruptedException {

        // 1. Set valid login details

        // String username = "s3923470";
        // String password = "Password";

        // 2. Open ParaBank in Chrome

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 3. Enter login details

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        Thread.sleep(2000);

        // 4. Login

        WebElement loginForm = driver.findElement(By.name("login"));
        loginForm.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 5. Save details of the current accounts before opening a new one

        assertEquals(1, driver.findElements(By.cssSelector("#accountTable tbody a")).size(),
                "The customer should start with one account");
        WebElement originalAccount = driver.findElement(By.cssSelector("#accountTable tbody tr:has(td > a)"));
        String originalAccountId = originalAccount.findElement(By.tagName("a")).getText().trim();
        assertEquals("$515.50", originalAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());

        // 5.1. Open New Account page
        driver.findElement(By.linkText("Open New Account")).click();

        Thread.sleep(2000);

        // 6.1 Locate the dropdown web element
        WebElement dropdownElement = driver.findElement(By.id("type"));

        // 6.2. Wrap it in a Select object
        Select accountType = new Select(dropdownElement);

        // 6.3. Choose your selection method
        accountType.selectByVisibleText("CHECKING");

        // 7. Make a new Checking account
        WebElement openNewAccount = driver.findElement(By.id("openAccountForm"));
        openNewAccount.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 8. Verify account opened successfully

        WebElement heading = driver.findElement(By.id("openAccountResult")).findElement(By.className("title"));

        assertEquals("Account Opened!", heading.getText().trim());

        // 8.1. Retrieve the new account ID

        String newAccountId = driver.findElement(By.id("newAccountId")).getText().trim();

        // 8.2. Assert that the new account ID is displayed

        assertFalse(newAccountId.isEmpty(), "The new account number should be displayed");

        // 8.3. Follow the generated account link before checking Accounts Overview

        assertTrue(driver.findElement(By.id("openAccountResult")).getText()
                .contains("Congratulations, your account is now open."));

        driver.findElement(By.id("newAccountId")).click();

        Thread.sleep(2000);

        // 8.4. Check the account details and transaction history for the new account

        assertEquals("Account Details", driver.findElement(By.cssSelector("#accountDetails .title")).getText().trim());
        assertEquals(newAccountId, driver.findElement(By.id("accountId")).getText().trim());
        assertEquals("CHECKING", driver.findElement(By.id("accountType")).getText().trim());
        assertEquals("$100.00", driver.findElement(By.id("balance")).getText().trim());
        assertEquals("$100.00", driver.findElement(By.id("availableBalance")).getText().trim());
        assertEquals("Account Activity", driver.findElement(By.cssSelector("#accountActivity .title")).getText().trim());

        // 8.4.1. Locate the deposit transaction row

        WebElement deposit = driver.findElement(By.cssSelector("#transactionTable tbody tr:has(td > a)"));
        assertEquals("Funds Transfer Received", deposit.findElement(By.tagName("a")).getText().trim());
        assertEquals("", deposit.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());
        assertEquals("$100.00", deposit.findElement(By.cssSelector("td:nth-child(4)")).getText().trim());

        // 8.5. Retrieve the transaction ID from the Funds Transfer Received link

        WebElement transactionLink = deposit.findElement(By.tagName("a"));
        String transactionHref = transactionLink.getDomAttribute("href");
        String transactionId = transactionHref.substring(transactionHref.indexOf("id=") + 3);

        // 8.6. Open the Transaction Details page

        transactionLink.click();

        Thread.sleep(2000);

        // 8.7. Check the transaction details for the opening deposit

        WebElement transactionDetails = driver.findElement(By.id("rightPanel"));

        assertEquals("Transaction Details", transactionDetails.findElement(By.className("title")).getText().trim());
        assertEquals(transactionId, transactionDetails.findElement(By.cssSelector("table tr:nth-child(1) td:nth-child(2)")).getText().trim());
        assertTrue(transactionDetails.findElement(By.cssSelector("table tr:nth-child(2) td:nth-child(2)")).getText().trim().matches("[0-9]{2}-[0-9]{2}-[0-9]{4}"),
                "The transaction date should be displayed as MM-dd-yyyy");
        assertEquals("Funds Transfer Received", transactionDetails.findElement(By.cssSelector("table tr:nth-child(3) td:nth-child(2)")).getText().trim());
        assertEquals("Credit", transactionDetails.findElement(By.cssSelector("table tr:nth-child(4) td:nth-child(2)")).getText().trim());
        assertEquals("$100.00", transactionDetails.findElement(By.cssSelector("table tr:nth-child(5) td:nth-child(2)")).getText().trim());

        // 9. Compare the accounts after opening the new account
        driver.findElement(By.linkText("Accounts Overview")).click();
        Thread.sleep(2000);

        // 9.1. Locate the account table

        WebElement accountTable = driver.findElement(By.id("accountTable"));

        // 9.2. Verify that there are exactly two accounts listed

        assertEquals(2, accountTable.findElements(By.cssSelector("tbody a")).size(), "The original and new accounts should be listed");

        // 9.3. Locate the original and new account rows

        originalAccount = findAccountRow(originalAccountId);
        WebElement newAccount = findAccountRow(newAccountId);

        // 9.4. Verify the account balances

        assertEquals("$415.50", originalAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim(),"The original account should have funded the $100 opening deposit");
        assertEquals("$100.00", newAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
        assertEquals("$100.00", newAccount.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());

    }

    @Test
    @SpiraTestCase(testCaseId = 325)
    @Order(2)
    public void OpenNewAccountValidSavings() throws InterruptedException {

        // 1. Set valid login details

        // String username = "s3923470";
        // String password = "Password";

        // 2. Open ParaBank in Chrome

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 3. Enter login details

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        Thread.sleep(2000);

        // 4. Login

        WebElement loginForm = driver.findElement(By.name("login"));
        loginForm.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 5. Save details of the current accounts before opening a new one

        assertEquals(2, driver.findElements(By.cssSelector("#accountTable tbody a")).size(),"The original and previously opened checking account should exist");
        WebElement originalAccount = driver.findElement(By.cssSelector("#accountTable tbody tr:has(td > a)"));
        String originalAccountId = originalAccount.findElement(By.tagName("a")).getText().trim();

        assertEquals("$415.50", originalAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());

        String checkingAccountId = driver.findElements(By.cssSelector("#accountTable tbody a")).get(1).getText().trim();
        WebElement checkingAccount = findAccountRow(checkingAccountId);
        String checkingBalance = checkingAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
        String checkingAvailable = checkingAccount.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();

        // 5.1. Open New Account page
        driver.findElement(By.linkText("Open New Account")).click();

        Thread.sleep(2000);

        // 6.1 Locate the dropdown web element
        WebElement dropdownElement = driver.findElement(By.id("type"));

        // 6.2. Wrap it in a Select object
        Select accountType = new Select(dropdownElement);

        // 6.3. Choose your selection method
        accountType.selectByVisibleText("SAVINGS");

        // 6.4. Select the original account as the funding source

        Select fundingAccounts = new Select(driver.findElement(By.id("fromAccountId")));
        assertEquals(2, fundingAccounts.getOptions().size());
        fundingAccounts.selectByIndex(0);
        String sourceAccountId = fundingAccounts.getFirstSelectedOption().getAttribute("value");
        assertEquals(originalAccountId, sourceAccountId);

        // 7. Make a new Savings account
        WebElement openNewAccount = driver.findElement(By.id("openAccountForm"));
        openNewAccount.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 8. Verify account opened successfully

        WebElement heading = driver.findElement(By.id("openAccountResult")).findElement(By.className("title"));

        assertEquals("Account Opened!", heading.getText().trim());

        // 8.1. Retrieve the new account ID

        String newAccountId = driver.findElement(By.id("newAccountId")).getText().trim();

        // 8.2. Assert that the new account ID is displayed

        assertFalse(newAccountId.isEmpty(), "The new account number should be displayed");

        // 8.3. Follow the generated account link before checking Accounts Overview

        assertTrue(driver.findElement(By.id("openAccountResult")).getText()
                .contains("Congratulations, your account is now open."));

        driver.findElement(By.id("newAccountId")).click();

        Thread.sleep(2000);

        // 8.4. Check the account details and transaction history for the new account

        assertEquals("Account Details", driver.findElement(By.cssSelector("#accountDetails .title")).getText().trim());
        assertEquals(newAccountId, driver.findElement(By.id("accountId")).getText().trim());
        assertEquals("SAVINGS", driver.findElement(By.id("accountType")).getText().trim());
        assertEquals("$100.00", driver.findElement(By.id("balance")).getText().trim());
        assertEquals("$100.00", driver.findElement(By.id("availableBalance")).getText().trim());
        assertEquals("Account Activity", driver.findElement(By.cssSelector("#accountActivity .title")).getText().trim());

        // 8.4.1. Locate the deposit transaction row

        WebElement deposit = driver.findElement(By.cssSelector("#transactionTable tbody tr:has(td > a)"));
        assertEquals("Funds Transfer Received", deposit.findElement(By.tagName("a")).getText().trim());
        assertEquals("", deposit.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());
        assertEquals("$100.00", deposit.findElement(By.cssSelector("td:nth-child(4)")).getText().trim());

        // 8.5. Retrieve the transaction ID from the Funds Transfer Received link

        WebElement transactionLink = deposit.findElement(By.tagName("a"));
        String transactionHref = transactionLink.getDomAttribute("href");
        String transactionId = transactionHref.substring(transactionHref.indexOf("id=") + 3);

        // 8.6. Open the Transaction Details page

        transactionLink.click();

        Thread.sleep(2000);

        // 8.7. Check the transaction details for the opening deposit

        WebElement transactionDetails = driver.findElement(By.id("rightPanel"));

        assertEquals("Transaction Details", transactionDetails.findElement(By.className("title")).getText().trim());
        assertEquals(transactionId, transactionDetails.findElement(By.cssSelector("table tr:nth-child(1) td:nth-child(2)")).getText().trim());
        assertTrue(transactionDetails.findElement(By.cssSelector("table tr:nth-child(2) td:nth-child(2)")).getText().trim().matches("[0-9]{2}-[0-9]{2}-[0-9]{4}"),
                "The transaction date should be displayed as MM-dd-yyyy");
        assertEquals("Funds Transfer Received", transactionDetails.findElement(By.cssSelector("table tr:nth-child(3) td:nth-child(2)")).getText().trim());
        assertEquals("Credit", transactionDetails.findElement(By.cssSelector("table tr:nth-child(4) td:nth-child(2)")).getText().trim());
        assertEquals("$100.00", transactionDetails.findElement(By.cssSelector("table tr:nth-child(5) td:nth-child(2)")).getText().trim());

        // 9. Compare the accounts after opening the new account
        driver.findElement(By.linkText("Accounts Overview")).click();
        Thread.sleep(2000);

        // 9.1. Locate the account table

        WebElement accountTable = driver.findElement(By.id("accountTable"));

        // 9.2. Verify that there are exactly three accounts listed

        assertEquals(3, accountTable.findElements(By.cssSelector("tbody a")).size(), "The original, checking and savings accounts should be listed");

        // 9.3. Locate the original and new account rows

        originalAccount = findAccountRow(originalAccountId);
        WebElement newAccount = findAccountRow(newAccountId);

        // 9.4. Verify the account balances

        assertEquals("$315.50", originalAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim(),
                "The original account should have funded both $100 opening deposits");
        assertEquals("$100.00", newAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
        assertEquals("$100.00", newAccount.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());
        checkingAccount = findAccountRow(checkingAccountId);
        assertEquals(checkingBalance, checkingAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
        assertEquals(checkingAvailable, checkingAccount.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());

    }

    @Test
    @SpiraTestCase(testCaseId = 326)
    @Order(3)
    public void OpenAccountsUntilNegative() throws InterruptedException {

        // 1. Open ParaBank in Chrome

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Enter login details

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);

        // 3. Login

        WebElement loginForm = driver.findElement(By.name("login"));
        loginForm.findElement(By.className("button")).click();

        Thread.sleep(2000);

        assertEquals("Accounts Overview", driver.findElement(By.className("title")).getText().trim());

        // 4. Save details of the current accounts before opening new ones

        String[][] before = readAccounts();
        assertEquals(3, before.length,"Expected the original account plus the checking and savings accounts");
        String sourceAccountId = before[0][0];
        int startingBalance = cents(before[0][1]);
        assertTrue(startingBalance >= 0, "The first account should not already be overdrawn");
        int sourceBalance = startingBalance;
        int openings = 0;
        int maximumOpenings = startingBalance / 10000 + 1;

        // 5. Repeat account opening until the original account has a negative balance

        while (sourceBalance >= 0 && openings < maximumOpenings) {

            String[][] openingBalances = readAccounts();

            // 5.1. Open New Account page

            driver.findElement(By.linkText("Open New Account")).click();

            Thread.sleep(2000);

            // 6.1. Locate the dropdown web element

            WebElement dropdownElement = driver.findElement(By.id("type"));

            // 6.2. Wrap it in a Select object

            Select accountType = new Select(dropdownElement);

            // 6.3. Choose your selection method

            accountType.selectByVisibleText("CHECKING");

            // 6.4. Select the original account as the funding source

            Select fundingAccounts = new Select(driver.findElement(By.id("fromAccountId")));
            assertEquals(openingBalances.length, fundingAccounts.getOptions().size());
            fundingAccounts.selectByValue(sourceAccountId);

            // 7. Make a new Checking account

            WebElement openNewAccount = driver.findElement(By.id("openAccountForm"));
            openNewAccount.findElement(By.className("button")).click();

            Thread.sleep(2000);

            // 8. Verify account opened successfully

            WebElement heading = driver.findElement(By.id("openAccountResult")).findElement(By.className("title"));

            assertEquals("Account Opened!", heading.getText().trim());

            // 8.1. Retrieve the new account ID

            String newAccountId = driver.findElement(By.id("newAccountId")).getText().trim();

            // 8.2. Follow the generated account link before checking Accounts Overview

            driver.findElement(By.id("newAccountId")).click();

            Thread.sleep(2000);

            // 8.3. Check the account details and transaction history for the new account

            assertEquals("Account Details", driver.findElement(By.cssSelector("#accountDetails .title")).getText().trim());
            assertEquals(newAccountId, driver.findElement(By.id("accountId")).getText().trim());
            assertEquals("CHECKING", driver.findElement(By.id("accountType")).getText().trim());
            assertEquals("$100.00", driver.findElement(By.id("balance")).getText().trim());
            assertEquals("$100.00", driver.findElement(By.id("availableBalance")).getText().trim());
            assertEquals("Account Activity", driver.findElement(By.cssSelector("#accountActivity .title")).getText().trim());

            // 8.3.1. Locate the deposit transaction row

            WebElement deposit = driver.findElement(By.cssSelector("#transactionTable tbody tr:has(td > a)"));
            assertEquals("Funds Transfer Received", deposit.findElement(By.tagName("a")).getText().trim());
            assertEquals("", deposit.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());
            assertEquals("$100.00", deposit.findElement(By.cssSelector("td:nth-child(4)")).getText().trim());

            // 9. Compare the accounts after opening the new account

            checkAccountBalances(openingBalances, sourceAccountId, newAccountId);
            openings++;

            // 9.1. Read the first account balance from Accounts Overview

            WebElement firstAccount = driver.findElement(
                    By.cssSelector("#accountTable tbody tr:has(td > a)"));
            assertEquals(sourceAccountId, firstAccount.findElement(By.tagName("a")).getText().trim(),
                    "The first row should still be the original funding account");
            sourceBalance = cents(firstAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
        }

        // 10. Verify the final account count

        String[][] after = readAccounts();
        assertEquals(before.length + openings, after.length,
                "Each opening should add exactly one account");

        // 10.1. Verify the first account is overdrawn and has no available funds

        WebElement sourceAccount = findAccountRow(sourceAccountId);
        int finalBalance = cents(sourceAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
        assertTrue(finalBalance < 0, "The first account must have a negative balance");
        assertEquals(Math.subtractExact(startingBalance, Math.multiplyExact(openings, 10000)), finalBalance,
                "The first account balance should reflect every $100 opening deposit");
        assertEquals("$0.00", sourceAccount.findElement(By.cssSelector("td:nth-child(3)")).getText().trim(),
                "The overdrawn account should have no available funds");

    }

    @Test
    @SpiraTestCase(testCaseId = 328)
    @Order(4)
    public void OpenNewAccountUsingSecondFundingAccount() throws InterruptedException {

        // 1. Open ParaBank in Chrome

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Enter login details

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);

        // 3. Login

        WebElement loginForm = driver.findElement(By.name("login"));
        loginForm.findElement(By.className("button")).click();

        Thread.sleep(2000);

        assertEquals("Accounts Overview", driver.findElement(By.className("title")).getText().trim());

        // 4. Save details of the current accounts before opening a new one

        String[][] before = readAccounts();

        // 5. Open New Account page

        driver.findElement(By.linkText("Open New Account")).click();

        Thread.sleep(2000);

        // 6.1. Locate the dropdown web element

        WebElement dropdownElement = driver.findElement(By.id("type"));

        // 6.2. Wrap it in a Select object

        Select accountType = new Select(dropdownElement);

        // 6.3. Choose your selection method

        accountType.selectByVisibleText("CHECKING");

        // 6.4. Select the second funding account without hard-coding its number

        Select fundingAccounts = new Select(driver.findElement(By.id("fromAccountId")));
        assertEquals(before.length, fundingAccounts.getOptions().size());
        assertTrue(fundingAccounts.getOptions().size() > 1, "A second funding account must exist");
        fundingAccounts.selectByIndex(1);
        String sourceAccountId = fundingAccounts.getFirstSelectedOption().getAttribute("value");

        // 7. Make a new Checking account

        WebElement openNewAccount = driver.findElement(By.id("openAccountForm"));
        openNewAccount.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 8. Verify account opened successfully

        WebElement heading = driver.findElement(By.id("openAccountResult")).findElement(By.className("title"));

        assertEquals("Account Opened!", heading.getText().trim());

        // 8.1. Retrieve the new account ID

        String newAccountId = driver.findElement(By.id("newAccountId")).getText().trim();

        // 8.2. Follow the generated account link before checking Accounts Overview

        driver.findElement(By.id("newAccountId")).click();

        Thread.sleep(2000);

        // 8.3. Check the account details and transaction history for the new account

        assertEquals("Account Details", driver.findElement(By.cssSelector("#accountDetails .title")).getText().trim());
        assertEquals(newAccountId, driver.findElement(By.id("accountId")).getText().trim());
        assertEquals("CHECKING", driver.findElement(By.id("accountType")).getText().trim());
        assertEquals("$100.00", driver.findElement(By.id("balance")).getText().trim());
        assertEquals("$100.00", driver.findElement(By.id("availableBalance")).getText().trim());
        assertEquals("Account Activity", driver.findElement(By.cssSelector("#accountActivity .title")).getText().trim());

        // 8.3.1. Locate the deposit transaction row

        WebElement deposit = driver.findElement(By.cssSelector("#transactionTable tbody tr:has(td > a)"));
        assertEquals("Funds Transfer Received", deposit.findElement(By.tagName("a")).getText().trim());
        assertEquals("", deposit.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());
        assertEquals("$100.00", deposit.findElement(By.cssSelector("td:nth-child(4)")).getText().trim());

        // 9. Verify the second account funded the opening and other accounts are unchanged

        checkAccountBalances(before, sourceAccountId, newAccountId);

    }

    // HELPER METHODS

    // Depending on if I should move them to a seperate utility class to keep it tidy.

    // Read the account information from the Accounts Overview page
    // Used to retrieve the current state of all accounts for comparison purposes

    private String[][] readAccounts() throws InterruptedException {

        // 1. Open Accounts Overview

        driver.findElement(By.linkText("Accounts Overview")).click();
        Thread.sleep(2000);

        // 2. Read account rows, excluding the Total row

        WebElement[] rows = driver.findElements(By.cssSelector("#accountTable tbody tr:has(td > a)")).toArray(new WebElement[0]);
        assertTrue(rows.length > 0, "Accounts Overview should contain customer accounts");

        // 3. Save text rather than elements, which become stale after navigation

        String[][] accounts = new String[rows.length][3];

        for (int i = 0; i < rows.length; i++) {

            accounts[i][0] = rows[i].findElement(By.tagName("a")).getText().trim();
            accounts[i][1] = rows[i].findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
            accounts[i][2] = rows[i].findElement(By.cssSelector("td:nth-child(3)")).getText().trim();

        }

        return accounts;

    }

    // Find the table row corresponding to a specific account ID
    // Throws an exception if the account row cannot be found

    private WebElement findAccountRow(String accountId) {

        assertTrue(accountId.matches("[0-9]+"), "Expected a numeric account number: " + accountId);

        // Match the row by its account link rather than its position in the table.

        return driver.findElement(By.cssSelector(

                "#accountTable tbody tr:has(td > a[href='activity.htm?id=" + accountId + "'])"));
                
    }

    // Convert a currency string (e.g., "$100.00") to integer cents
    // Literally just convert dollars to cents

    private int cents(String text) {

        String amount = text.replace("$", "").replace(",", "").trim();

        assertTrue(amount.matches("-?[0-9]+\\.[0-9]{2}"), "Unexpected currency amount: " + text);

        // Integer cents avoid rounding errors when comparing the $100 debit.

        return Integer.parseInt(amount.replace(".", ""));

    }

    // Check that account balances are updated correctly after opening a new account

    private void checkAccountBalances(String[][] before, String sourceAccountId, String newAccountId) throws InterruptedException {

        // 1. Confirm the selected funding account existed before the opening

        boolean sourceExists = false;

        for (int i = 0; i < before.length; i++) {

            if (before[i][0].equals(sourceAccountId)) {

                sourceExists = true;

            }

        }

        assertTrue(sourceExists, "The selected funding account must appear in Accounts Overview");

        // 2. Verify the new account number is displayed

        assertFalse(newAccountId.isEmpty(), "The new account number should be displayed");

        // 3. Return to Accounts Overview and check the new account

        String[][] after = readAccounts();

        assertEquals(before.length + 1, after.length, "Exactly one account should be created");

        WebElement newAccount = findAccountRow(newAccountId);
        assertEquals("$100.00", newAccount.findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
        assertEquals("$100.00", newAccount.findElement(By.cssSelector("td:nth-child(3)")).getText().trim());

        // 4. Compare the funding debit and verify other accounts are unchanged

        for (int i = 0; i < before.length; i++) {

            WebElement row = findAccountRow(before[i][0]);
            String balance = row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
            String available = row.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();

            if (before[i][0].equals(sourceAccountId)) {

                assertEquals(Math.subtractExact(cents(before[i][1]), 10000), cents(balance),"The selected funding account should be debited exactly $100");
                        
            } else {

                assertEquals(before[i][1], balance, "Balance should remain unchanged: " + before[i][0]);
                assertEquals(before[i][2], available, "Available amount should remain unchanged: " + before[i][0]);

            }

        }

    }

}
