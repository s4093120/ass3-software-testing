package au.edu.rmit.selenium;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// IMPORT DO NOT TOUCH, SETS ORDER OF TEST EXECUTION
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ParaBankSmokeTest {

    // Generate a unique username for the test
    
    private static final String username = "st_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    private static final String password = "password";

    // Register with valid details

    @Test
    @Order(1)
    public void RegisterValidDetails() throws InterruptedException {

        // 1. Open ParaBank in Chrome

        WebDriver driver = new ChromeDriver();

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Click on the "Register" link to go to the registration page
        // driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.linkText("Register")).click();

        // 3. Enter Registration Details

        driver.findElement(By.id("customer.firstName")).sendKeys("TestExample123");
        driver.findElement(By.id("customer.lastName")).sendKeys("Customer");
        driver.findElement(By.id("customer.address.street")).sendKeys("123 Example Street");
        driver.findElement(By.id("customer.address.city")).sendKeys("Melbourne");
        driver.findElement(By.id("customer.address.state")).sendKeys("VIC");
        driver.findElement(By.id("customer.address.zipCode")).sendKeys("3000");
        driver.findElement(By.id("customer.phoneNumber")).sendKeys("0400000000");
        driver.findElement(By.id("customer.ssn")).sendKeys("123456789");
        driver.findElement(By.id("customer.username")).sendKeys(username);
        driver.findElement(By.id("customer.password")).sendKeys(password);
        driver.findElement(By.id("repeatedPassword")).sendKeys(password);

        // 4. Register

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

        // 5. Verify successful registration

        String successMessage = "Your account was created successfully. You are now logged in.";

        String pageText = driver.findElement(By.id("rightPanel")).getText();
        assertTrue(pageText.contains(successMessage));

        driver.quit();

    }

    // Login with valid details test

    @Test
    @Order(2)
    void loginWithValidDetails() throws InterruptedException {

        // 1. Set valid login password

        // 2. Open ParaBank in Chrome
        WebDriver driver = new ChromeDriver();

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

        // 5. Verify successful login
        WebElement heading = driver.findElement(By.className("title"));

        assertEquals("h1", heading.getTagName());
        assertEquals("Accounts Overview", heading.getText().trim());

        // 6. Close the browser
        driver.quit();

    }

    // Check opening a new Checking account with valid details

    @Test
    @Order(3)
    void OpenNewAccountValidChecking() throws InterruptedException {

        // 1. Set valid login details

        // String username = "s3923470";
        // String password = "Password";

        // 2. Open ParaBank in Chrome
        WebDriver driver = new ChromeDriver();

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

        // 5. Open New Account page
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

        // 9. Close the browser
        driver.quit();

    }

    // Open a new Savings account with valid details

    @Test
    @Order(4)
    void OpenNewAccountValidSavings() throws InterruptedException {

        // 2. Open ParaBank in Chrome
        WebDriver driver = new ChromeDriver();

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

        // 5. Open New Account page
        driver.findElement(By.linkText("Open New Account")).click();

        Thread.sleep(2000);

        // 6.1 Locate the dropdown web element
        WebElement dropdownElement = driver.findElement(By.id("type"));

        // 6.2. Wrap it in a Select object
        Select accountType = new Select(dropdownElement);

        // 6.3. Choose your selection method
        accountType.selectByVisibleText("SAVINGS");

        // 7. Make a new Savings account
        WebElement openNewAccount = driver.findElement(By.id("openAccountForm"));
        openNewAccount.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 8. Verify account opened successfully

        WebElement heading = driver.findElement(By.id("openAccountResult")).findElement(By.className("title"));

        assertEquals("Account Opened!", heading.getText().trim());

        // 9. Close the browser
        driver.quit();

    }

    // INVALID TESTS

    // Registering with same username

    @Test
    @Order(30)
    public void RegisterDuplicateUsername() throws InterruptedException {

        // 1. Open ParaBank in Chrome

        WebDriver driver = new ChromeDriver();

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Click on the "Register" link to go to the registration page
        // driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.linkText("Register")).click();

        // 3. Enter Registration Details

        driver.findElement(By.id("customer.firstName")).sendKeys("TestExample123");
        driver.findElement(By.id("customer.lastName")).sendKeys("Customer");
        driver.findElement(By.id("customer.address.street")).sendKeys("123 Example Street");
        driver.findElement(By.id("customer.address.city")).sendKeys("Melbourne");
        driver.findElement(By.id("customer.address.state")).sendKeys("VIC");
        driver.findElement(By.id("customer.address.zipCode")).sendKeys("3000");
        driver.findElement(By.id("customer.phoneNumber")).sendKeys("0400000000");
        driver.findElement(By.id("customer.ssn")).sendKeys("123456789");
        driver.findElement(By.id("customer.username")).sendKeys(username);
        driver.findElement(By.id("customer.password")).sendKeys(password);
        driver.findElement(By.id("repeatedPassword")).sendKeys(password);

        // 4. Register

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

        // 5. Verify registration failed due to duplicate username

        String errorMessage = "This username already exists.";

        WebElement error = driver.findElement(By.id("customer.username.errors"));
        assertTrue(error.isDisplayed(), "Duplicate username error should be visible");
        assertTrue(errorMessage.equals(error.getText().trim()),
                "Expected duplicate username error, but got: " + error.getText());

        driver.quit();

    }

        @Test
    @Order(31)
    public void RegisterDifferentPasswords() throws InterruptedException {

        // 1. Open ParaBank in Chrome

        WebDriver driver = new ChromeDriver();

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Click on the "Register" link to go to the registration page
        // driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.linkText("Register")).click();

        // 3. Enter Registration Details

        driver.findElement(By.id("customer.firstName")).sendKeys("TestExample123");
        driver.findElement(By.id("customer.lastName")).sendKeys("Customer");
        driver.findElement(By.id("customer.address.street")).sendKeys("123 Example Street");
        driver.findElement(By.id("customer.address.city")).sendKeys("Melbourne");
        driver.findElement(By.id("customer.address.state")).sendKeys("VIC");
        driver.findElement(By.id("customer.address.zipCode")).sendKeys("3000");
        driver.findElement(By.id("customer.phoneNumber")).sendKeys("0400000000");
        driver.findElement(By.id("customer.ssn")).sendKeys("123456789");
        driver.findElement(By.id("customer.username")).sendKeys(username);
        driver.findElement(By.id("customer.password")).sendKeys("a");
        driver.findElement(By.id("repeatedPassword")).sendKeys("b");

        // 4. Register

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

        // 5. Verify registration failed due to different passwords

        String errorMessage = "Passwords did not match.";

        WebElement error = driver.findElement(By.id("repeatedPassword.errors"));
        assertTrue(error.isDisplayed(), "Password mismatch error should be visible");
        assertTrue(errorMessage.equals(error.getText().trim()),
                "Expected password mismatch error, but got: " + error.getText());

        driver.quit();

    }

    // Login Test with invalidUsername

    @Test
    @Order(32)
    void loginWithInvalidUsername() throws InterruptedException {

        // 1. Set valid login password

        // 2. Open ParaBank in Chrome
        WebDriver driver = new ChromeDriver();

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 3. Enter login details
        
        driver.findElement(By.name("username")).sendKeys("invalidUsername");
        driver.findElement(By.name("password")).sendKeys(password);
        Thread.sleep(2000);

        // 4. Login

        WebElement loginForm = driver.findElement(By.name("login"));
        loginForm.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 5. Verify login failed
        WebElement heading = driver.findElement(By.className("title"));

        // 6. Check that the login failed and an error message is displayed, and NOT the accounts overview page

        assertEquals("h1", heading.getTagName());
        assertNotEquals("Accounts Overview", heading.getText().trim())

        // 7. Close the browser
        driver.quit();

    }

    // Login Test with invalidPassword

    @Test
    @Order(33)
    void loginWithInvalidPassword() throws InterruptedException {

        // 1. Set valid login password

        // 2. Open ParaBank in Chrome
        WebDriver driver = new ChromeDriver();

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 3. Enter login details
        
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys("invalidPassword");
        Thread.sleep(2000);

        // 4. Login

        WebElement loginForm = driver.findElement(By.name("login"));
        loginForm.findElement(By.className("button")).click();

        Thread.sleep(2000);

        // 5. Verify login failed
        WebElement heading = driver.findElement(By.className("title"));

        // 6. Check that the login failed and an error message is displayed, and NOT the accounts overview page

        assertEquals("h1", heading.getTagName());
        assertNotEquals("Accounts Overview", heading.getText().trim());

        // 7. Close the browser
        driver.quit();

    }

}
