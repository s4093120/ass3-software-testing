package au.edu.rmit.cucumber.support;

import au.edu.rmit.cucumber.pages.ParaBankPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.time.Duration;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class Hooks {
    private final ScenarioContext context;

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    @Before
    public void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1400,1000");
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }
        if (Boolean.getBoolean("browser.noSandbox")) {
            // Only for isolated containers that cannot start Chrome with its normal sandbox.
            options.addArguments("--no-sandbox");
        }
        String binary = System.getProperty("chrome.binary");
        if (binary != null && !binary.isBlank()) {
            options.setBinary(binary);
        }
        String proxyAddress = System.getProperty("browser.proxy");
        if (proxyAddress != null && !proxyAddress.isBlank()) {
            options.setProxy(new Proxy().setHttpProxy(proxyAddress).setSslProxy(proxyAddress));
        }
        // Selenium Manager locates/downloads ChromeDriver by default.
        context.driver = new ChromeDriver(ChromeDriverService.createDefaultService(), options);
        context.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        context.driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        context.bank = new ParaBankPage(context.driver);
    }

    @After
    public void recordFailureAndClose(Scenario scenario) {
        if (context.driver == null) {
            return;
        }
        try {
            if (scenario.isFailed()) {
                try {
                    scenario.attach(((TakesScreenshot) context.driver).getScreenshotAs(OutputType.BYTES),
                            "image/png", "Failure - " + scenario.getName());
                    scenario.log("Page at failure: " + context.driver.getCurrentUrl());
                    scenario.log("Page title at failure: " + context.driver.getTitle());
                    String source = context.driver.getPageSource();
                    if (source.replaceAll("\\s+", "").equals("<html><head></head><body></body></html>")) {
                        scenario.log("The browser returned an empty HTML document at failure.");
                    }
                } catch (RuntimeException screenshotError) {
                    scenario.log("Could not capture failure screenshot: " + screenshotError.getMessage());
                }
            }
        } finally {
            context.driver.quit();
        }
    }
}
