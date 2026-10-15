# Software Testing Assignment 3

Java 17 Maven project for ParaBank Selenium and Cucumber tests.

The Maven configuration uses Selenium 4.50.0, Cucumber 7.18.1, and JUnit 5.10.3.
JUnit is aligned with Cucumber's JUnit Platform dependency through the JUnit BOM.

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- Google Chrome for browser tests

## Running tests on Windows

Open PowerShell in this repository and check the Java version Maven uses:

```powershell
mvn -version
mvn test
```

Maven's version output must show Java 17 or newer. Chey's terminal already
defaults to JDK 17, so no environment changes are needed. If a terminal was
previously switched to JDK 25, open a new terminal to use the default again.

Tests belong under `src/test/java`, with test data under `src/test/resources`.
Name JUnit test classes `*Test` so Maven discovers them. Test reports are
generated under `target/surefire-reports`.

`ProjectSetupTest` verifies the Java minimum, JUnit test discovery, and the Selenium
dependency without opening a browser. It is not a ParaBank functional test.

`ParaBankSmokeTest` opens Chrome, loads the public ParaBank home page, and
checks its title and login form. It closes Chrome after the test, including
when an assertion fails. Run just this browser test with:

```powershell
mvn "-Dtest=ParaBankSmokeTest" test
```

This follows the supplied Selenium ZIP's direct WebDriver and element-locator
style, with JUnit lifecycle methods, assertions, and an explicit wait added.
Selenium Manager resolves ChromeDriver instead of using the ZIP's fixed
`chromedriver.exe`. Internet access is required for ParaBank and may also be
required to download a matching driver on the first run.

## Login test

`LoginTest` signs in with an existing ParaBank account and checks that Accounts
Overview and the Log Out link appear. Set `PARABANK_USERNAME` and
`PARABANK_PASSWORD` in your terminal before running it:

```powershell
mvn clean test "-Dtest=LoginTest"
```

These variables are also required when running the complete suite. The test
fails explicitly if credentials are missing or the login is rejected. It closes
Chrome even if the test fails. ParaBank resets can remove the account; recreate
it through registration if necessary. Do not store the password in source code.

Full coverage of the six required Selenium scenarios and SpiraPlan integration
still needs to be completed.

Do not commit SpiraPlan credentials, API keys, or real personal information.
Supply credentials through environment variables when integration is added.
