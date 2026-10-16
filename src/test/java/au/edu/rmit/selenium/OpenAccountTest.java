package au.edu.rmit.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;

// IMPORT DO NOT TOUCH, SETS ORDER OF TEST EXECUTION
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OpenAccountTest extends BaseTest {

    // Check opening a new Checking account with valid details

    @Test
    @Order(3)
    void OpenNewAccountValidChecking() throws InterruptedException {

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


    }

    // Open a new Savings account with valid details

    @Test
    @Order(4)
    void OpenNewAccountValidSavings() throws InterruptedException {

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


    }

}
