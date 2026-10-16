package au.edu.rmit.selenium;

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
    
    // Ensures the customer is registered before running tests.
    // If the customer is not registered, it will perform the registration process.
    
    static void ensureRegistered() throws InterruptedException {

        // If registered, no need to register again.

        if (registered) {

            return;
        
        }

        // Else, perform the registration process (effectively performing registration tests before every test)

        WebDriver driver = new ChromeDriver();

        try {

            driver.get("https://parabank.parasoft.com/parabank/register.htm");
            Thread.sleep(2000);

            fillRegistrationDetails(driver, USERNAME, PASSWORD, PASSWORD);
            driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

            Thread.sleep(2000);

            String successMessage = "Your account was created successfully. You are now logged in.";

            String pageText = driver.findElement(By.id("rightPanel")).getText();

            assertTrue(pageText.contains(successMessage), "Shared customer registration failed: " + pageText);

            registered = true;
            
        } finally {

            driver.quit();

        }

    }

}
