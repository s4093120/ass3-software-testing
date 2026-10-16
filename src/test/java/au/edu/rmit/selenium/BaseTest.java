package au.edu.rmit.selenium;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

abstract class BaseTest {

    // Generates password and username for the test customer

    protected static final String username = TestCustomer.USERNAME;
    protected static final String password = TestCustomer.PASSWORD;


    WebDriver driver;

    // Before all tests, ensure a test customer is registered.

    @BeforeAll
    static void prepareCustomer() throws InterruptedException {

        TestCustomer.ensureRegistered();

    }

    // Before each test, set up the WebDriver.

    // Includes setting a page load timeout for the WebDriver (to prevent tests from hanging indefinitely)

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

    }

    // After each test, quit the WebDrivers.

    @AfterEach
    void quitDriver() {

        if (driver != null) {

            driver.quit();

        }

    }
    
}
