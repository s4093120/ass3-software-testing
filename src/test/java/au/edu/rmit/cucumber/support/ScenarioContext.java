package au.edu.rmit.cucumber.support;

import au.edu.rmit.cucumber.pages.ParaBankPage;
import java.math.BigDecimal;
import java.util.Map;
import org.openqa.selenium.WebDriver;

/** PicoContainer supplies a fresh instance per scenario; nothing is shared between scenarios. */
public class ScenarioContext {
    public WebDriver driver;
    public ParaBankPage bank;
    public String sourceAccount;
    public String destinationAccount;
    public String loanAccount;
    public BigDecimal sourceBefore;
    public BigDecimal destinationBefore;
    public BigDecimal transferredAmount;
    public Map<String, BigDecimal> accountsBeforeLoan;
}
