package au.edu.rmit.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// IMPORT DO NOT TOUCH, SETS ORDER OF TEST EXECUTION
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginTest extends BaseTest {

    // Login with valid details test

    @Test
    @Order(2)
    void loginWithValidDetails() throws InterruptedException {

        // 1. Set valid login password

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

        // 5. Verify successful login
        WebElement heading = driver.findElement(By.className("title"));

        assertEquals("h1", heading.getTagName());
        assertEquals("Accounts Overview", heading.getText().trim());

        // 6. Close the browser


    }

    // Login Test with invalidUsername

    @Test
    @Order(32)
    void loginWithInvalidUsername() throws InterruptedException {

        // 1. Set valid login password

        // 2. Open ParaBank in Chrome


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
        WebElement result = driver.findElement(By.id("rightPanel"));
        WebElement heading = result.findElement(By.className("title"));
        WebElement error = result.findElement(By.className("error"));

        // 6. Check that the login failed and an error message is displayed, and NOT the accounts overview page

        assertEquals("h1", heading.getTagName());
        assertEquals("Error!", heading.getText().trim());
        assertTrue(error.isDisplayed(), "Login rejection message should be visible");
        assertEquals("The username and password could not be verified.",
                error.getText().trim());
        assertNotEquals("Accounts Overview", heading.getText().trim());

        // 7. Close the browser


    }

    // Login Test with invalidPassword

    @Test
    @Order(33)
    void loginWithInvalidPassword() throws InterruptedException {

        // 1. Set valid login password

        // 2. Open ParaBank in Chrome


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
        WebElement result = driver.findElement(By.id("rightPanel"));
        WebElement heading = result.findElement(By.className("title"));
        WebElement error = result.findElement(By.className("error"));

        // 6. Check that the login failed and an error message is displayed, and NOT the accounts overview page

        assertEquals("h1", heading.getTagName());
        assertEquals("Error!", heading.getText().trim());
        assertTrue(error.isDisplayed(), "Login rejection message should be visible");
        assertEquals("The username and password could not be verified.",
                error.getText().trim());
        assertNotEquals("Accounts Overview", heading.getText().trim());

        // 7. Close the browser


    }

}
