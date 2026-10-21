package au.edu.rmit.selenium;

import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
        testSetId = 11
)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
class RegistrationTest extends BaseTest {

    // Helper function for each registration field to assert that an ampersand is rejected (special characters shouldn't be used in the form).

        private void assertAmpersandRejected(String fieldId) throws InterruptedException {

        // 1. Open ParaBank registration page in Chrome
        driver.get("https://parabank.parasoft.com/parabank/register.htm");

        // 2. Enter valid registration details
        TestCustomer.fillRegistrationDetails(driver, TestCustomer.newUsername(), password, password);

        // 3. Replace the selected detail with an ampersand
        WebElement invalidField = driver.findElement(By.id(fieldId));
        invalidField.clear();
        invalidField.sendKeys("&");
        assertEquals("&", invalidField.getDomProperty("value"),
                "The selected field should contain only an ampersand");

        // 4. Submit the registration form
        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();
        Thread.sleep(2000);

        // 5. Verify registration failed with an error for the selected field
        // Checks false to ensure the registration did not succeed
        // Checks true to see if an error message is displayed for the selected field
        String pageText = driver.findElement(By.id("rightPanel")).getText();

        assertFalse(pageText.contains("Your account was created successfully."), "Registration should reject '&' in " + fieldId + ", but succeeded.");
        WebElement[] errors = driver.findElements(By.id(fieldId + ".errors")).toArray(new WebElement[0]);
        boolean hasVisibleError = false;

        for (int i = 0; i < errors.length; i++) {
            if (errors[i].isDisplayed() && !errors[i].getText().trim().isEmpty()) {
                hasVisibleError = true;
                break;
            }
        }

        assertTrue(hasVisibleError,
                "Expected a validation error for " + fieldId + ", not a duplicate username or unrelated error. Page: " + pageText);
    
    }

    // Register with valid details

    @Test
    @SpiraTestCase(testCaseId = 316)
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
        Thread.sleep(2000);

        // 5. Verify successful registration

        String successMessage = "Your account was created successfully. You are now logged in.";

        String pageText = driver.findElement(By.id("rightPanel")).getText();
        assertTrue(pageText.contains(successMessage), "Registration did not succeed.");



    }

    @Test
    @SpiraTestCase(testCaseId = 344)
    @Order(4)
    public void RegisterWithEmptyDetails() throws InterruptedException {

        // 1. Open ParaBank in Chrome

        driver.get("https://parabank.parasoft.com/parabank/register.htm");

        // 2. Click submit without filling any details

        assertEquals("", driver.findElement(By.id("customer.phoneNumber")).getDomProperty("value"),
                "Phone number should be empty before submitting");
        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

        Thread.sleep(2000);

        // 3. Verify error messages for all required fields

        assertEquals("First name is required.", driver.findElement(By.id("customer.firstName.errors")).getText().trim());
        assertEquals("Last name is required.", driver.findElement(By.id("customer.lastName.errors")).getText().trim());
        assertEquals("Address is required.", driver.findElement(By.id("customer.address.street.errors")).getText().trim());
        assertEquals("City is required.", driver.findElement(By.id("customer.address.city.errors")).getText().trim());
        assertEquals("State is required.",  driver.findElement(By.id("customer.address.state.errors")).getText().trim());
        assertEquals("Zip Code is required.", driver.findElement(By.id("customer.address.zipCode.errors")).getText().trim());
        assertEquals("Social Security Number is required.", driver.findElement(By.id("customer.ssn.errors")).getText().trim());
        assertEquals("Username is required.", driver.findElement(By.id("customer.username.errors")).getText().trim());
        assertEquals("Password is required.", driver.findElement(By.id("customer.password.errors")).getText().trim());
        assertEquals("Password confirmation is required.", driver.findElement(By.id("repeatedPassword.errors")).getText().trim());
        assertFalse(driver.findElement(By.id("rightPanel")).getText().contains("Your account was created successfully."), "Empty registration must not succeed");
        assertEquals("", driver.findElement(By.id("customer.phoneNumber")).getDomProperty("value"),
                "Phone number should remain empty after submitting");
        WebElement[] errors = driver.findElements(By.id("customer.phoneNumber.errors")).toArray(new WebElement[0]);
        boolean hasPhoneError = false;

        for (int i = 0; i < errors.length; i++) {
            if (errors[i].isDisplayed() && !errors[i].getText().trim().isEmpty()) {
                hasPhoneError = true;
                break;
            }
        }

        assertEquals(true, hasPhoneError,
                "Expected a required-phone-number validation error for the empty phone field");
    }

    // INVALID TESTS

    @Test
    @SpiraTestCase(testCaseId = 345)
    @Order(5)
    public void RegisterWithSingleLetterDetails() throws InterruptedException {

        // 1. Open ParaBank registration page in Chrome
        driver.get("https://parabank.parasoft.com/parabank/register.htm");

        // 2. Fill in all required personal details with single-letter values

        driver.findElement(By.id("customer.firstName")).sendKeys("A");
        driver.findElement(By.id("customer.lastName")).sendKeys("A");
        driver.findElement(By.id("customer.address.street")).sendKeys("A");

        driver.findElement(By.id("customer.address.city")).sendKeys("A");
        driver.findElement(By.id("customer.address.state")).sendKeys("A");
        driver.findElement(By.id("customer.address.zipCode")).sendKeys("A");

        driver.findElement(By.id("customer.phoneNumber")).sendKeys("A");
        driver.findElement(By.id("customer.ssn")).sendKeys("A");

        driver.findElement(By.id("customer.username")).sendKeys(TestCustomer.newUsername());
        driver.findElement(By.id("customer.password")).sendKeys(password);
        driver.findElement(By.id("repeatedPassword")).sendKeys(password);

        // 3. Submit the registration form
        driver.findElement(By.cssSelector("#customerForm input[type='submit']")).click();

        Thread.sleep(2000);

        // 4. Check for visible validation errors regarding single-letter personal details
        WebElement[] errors = driver.findElements(By.cssSelector("#customerForm .error")).toArray(new WebElement[0]);
        boolean hasVisibleError = false;

        for (int i = 0; i < errors.length; i++) {
            if (errors[i].isDisplayed() && !errors[i].getText().trim().isEmpty()) {
                hasVisibleError = true;
                break;
            }
        }

        assertEquals(true, hasVisibleError, "Expected a validation error for single-letter personal details");

    }

    @Test
    @SpiraTestCase(testCaseId = 338)
    @Order(6)
    public void RegisterWithAmpersandFirstName() throws InterruptedException {

        // 1. Test registration with an ampersand in the first name
        assertAmpersandRejected("customer.firstName");
    }

    @Test
    @SpiraTestCase(testCaseId = 335)
    @Order(7)
    public void RegisterWithAmpersandLastName() throws InterruptedException {

        // 1. Test registration with an ampersand in the last name
        assertAmpersandRejected("customer.lastName");
    }

    @Test
    @SpiraTestCase(testCaseId = 341)
    @Order(8)
    public void RegisterWithAmpersandAddress() throws InterruptedException {

        // 1. Test registration with an ampersand in the street address
        assertAmpersandRejected("customer.address.street");
    }

    @Test
    @SpiraTestCase(testCaseId = 337)
    @Order(9)
    public void RegisterWithAmpersandCity() throws InterruptedException {

        // 1. Test registration with an ampersand in the city
        assertAmpersandRejected("customer.address.city");
    }

    @Test
    @SpiraTestCase(testCaseId = 339)
    @Order(10)
    public void RegisterWithAmpersandState() throws InterruptedException {

        // 1. Test registration with an ampersand in the state
        assertAmpersandRejected("customer.address.state");
    }

    @Test
    @SpiraTestCase(testCaseId = 340)
    @Order(11)
    public void RegisterWithAmpersandZipCode() throws InterruptedException {

        // 1. Test registration with an ampersand in the ZIP code
        assertAmpersandRejected("customer.address.zipCode");
    }

    @Test
    @SpiraTestCase(testCaseId = 342)
    @Order(12)
    public void RegisterWithAmpersandPhoneNumber() throws InterruptedException {

        // 1. Test registration with an ampersand in the phone number
        assertAmpersandRejected("customer.phoneNumber");
    }

    @Test
    @SpiraTestCase(testCaseId = 346)
    @Order(13)
    public void RegisterWithAmpersandSsn() throws InterruptedException {

        // 1. Test registration with an ampersand in the SSN
        assertAmpersandRejected("customer.ssn");
    }

    // Registering with same username

    @Test
    @SpiraTestCase(testCaseId = 317)
    @Order(2)
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
    @SpiraTestCase(testCaseId = 318)
    @Order(3)
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

        WebElement error = driver.findElement(By.id("repeatedPassword.errors"));
        assertTrue(error.isDisplayed(), "Password mismatch error should be visible");
        assertTrue(errorMessage.equals(error.getText().trim()),
                "Expected password mismatch error, but got: " + error.getText());



    }
    

}
