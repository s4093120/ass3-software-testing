package au.edu.rmit.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

final class TestCustomer {

    // Generates a unique username with a fixed  password and ensures registration.

    static final String USERNAME = newUsername();
    static final String PASSWORD = "Password";
    private static boolean registered;

    // Indicates whether the shared test customer has been registered.

    private TestCustomer() {}

    // Generates a new unique username for the test customer.

    static String newUsername() {

        return "st_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    
    }

    // Helper method to fill in the registration form with the provided details.

    static void fillRegistrationDetails(WebDriver driver, String username, String password, String repeatedPassword) {

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
        driver.findElement(By.id("repeatedPassword")).sendKeys(repeatedPassword);

    }

    // Check if the shared test customer is registered and register if not.
    
    static void ensureRegistered() throws InterruptedException {

        if (registered) {

            return;

        }

        WebDriver driver = new ChromeDriver();

        try {

            Thread.sleep(2000);
            registerCustomer(driver);

        } finally {

            driver.quit();

        }

    }

    // Ensures the shared test customer is logged in, registering if necessary.

    static void ensureLoggedIn(WebDriver driver) throws InterruptedException {

        ensureRegistered();

        // Login with the shared test customer

        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        Thread.sleep(2000);

        if (!driver.findElements(By.linkText("Log Out")).isEmpty()) {

            return;

        }

        driver.findElement(By.name("username")).sendKeys(USERNAME);
        driver.findElement(By.name("password")).sendKeys(PASSWORD);
        driver.findElement(By.name("login")).findElement(By.className("button")).click();

        Thread.sleep(2000);

        if (!driver.findElements(By.linkText("Log Out")).isEmpty()) {

            return;

        }

        // Recreate the user if the server no longer recognises it
        String loginError = driver.findElement(By.cssSelector("#rightPanel .error")).getText().trim();

        assertEquals("The username and password could not be verified.", loginError, "Shared customer login failed: " + loginError);
        
        registered = false;
        
        try {

            registerCustomer(driver);

        } catch (AssertionError registrationError) {

            // Ignore registration errors, as the user might already exist

        }

    }

    // Logs in the shared test customer, registering if necessary.

    private static void registerCustomer(WebDriver driver) throws InterruptedException {

        driver.get("https://parabank.parasoft.com/parabank/register.htm");
        Thread.sleep(2000);
        fillRegistrationDetails(driver, USERNAME, PASSWORD, PASSWORD);

        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

        Thread.sleep(2000);

        String pageText = driver.findElement(By.id("rightPanel")).getText();

        assertTrue(

            pageText.contains("Your account was created successfully. You are now logged in."),
            "Shared customer registration failed: " + pageText

        );

        registered = true;

    }

}
