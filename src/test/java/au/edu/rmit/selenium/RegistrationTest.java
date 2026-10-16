package au.edu.rmit.selenium;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

// IMPORT DO NOT TOUCH, SETS ORDER OF TEST EXECUTION
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegistrationTest extends BaseTest {

    // Register with valid details

    @Test
    @Order(1)
    public void RegisterValidDetails() throws InterruptedException {

        String username = TestCustomer.newUsername();

        // 1. Open ParaBank in Chrome



        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Click on the "Register" link to go to the registration page
        // driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.linkText("Register")).click();
        Thread.sleep(2000);

        // 3. Enter Registration Details

        TestCustomer.fillRegistrationDetails(driver, username, password, password);

        // 4. Register

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();
        Thread.sleep(2000);

        // 5. Verify successful registration

        String successMessage = "Your account was created successfully. You are now logged in.";

        new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.or(
                        ExpectedConditions.textToBePresentInElementLocated(
                                By.id("rightPanel"), successMessage),
                        ExpectedConditions.visibilityOfElementLocated(By.className("error"))));
        String pageText = driver.findElement(By.id("rightPanel")).getText();
        assertTrue(pageText.contains(successMessage),
                "Registration did not succeed. Actual page text: " + pageText);



    }

    // INVALID TESTS

    // Registering with same username

    @Test
    @Order(30)
    public void RegisterDuplicateUsername() throws InterruptedException {

        // 1. Open ParaBank in Chrome



        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Click on the "Register" link to go to the registration page
        // driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.linkText("Register")).click();
        Thread.sleep(2000);

        // 3. Enter Registration Details

        TestCustomer.fillRegistrationDetails(driver, username, password, password);

        // 4. Register

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();
        Thread.sleep(2000);

        // 5. Verify registration failed due to duplicate username

        String errorMessage = "This username already exists.";

        WebElement error = driver.findElement(By.id("customer.username.errors"));
        assertTrue(error.isDisplayed(), "Duplicate username error should be visible");
        assertTrue(errorMessage.equals(error.getText().trim()),
                "Expected duplicate username error, but got: " + error.getText());



    }

        @Test
    @Order(31)
    public void RegisterDifferentPasswords() throws InterruptedException {

        String username = TestCustomer.newUsername();

        // 1. Open ParaBank in Chrome



        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        // 2. Click on the "Register" link to go to the registration page
        // driver.findElement(By.linkText("Register")).click();
        driver.findElement(By.linkText("Register")).click();
        Thread.sleep(2000);

        // 3. Enter Registration Details

        TestCustomer.fillRegistrationDetails(driver, username, "a", "b");

        // 4. Register

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();
        Thread.sleep(2000);
        // 5. Verify registration failed due to different passwords

        String errorMessage = "Passwords did not match.";

        WebElement error = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.visibilityOfElementLocated(By.id("repeatedPassword.errors")));
        assertTrue(error.isDisplayed(), "Password mismatch error should be visible");
        assertTrue(errorMessage.equals(error.getText().trim()),
                "Expected password mismatch error, but got: " + error.getText());



    }

}
