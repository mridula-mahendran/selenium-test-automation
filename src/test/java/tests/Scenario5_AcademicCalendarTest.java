package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.util.Map;

/**
 * Scenario 5: Update the Academic Calendar.
 * Steps:
 *   a) Navigate to Student Hub and click on Resources
 *   b) Click on Academics, Classes & Registration
 *   c) Click on Academic Calendar
 *   d) Click on Academic Calendar under Northeastern University Registrar
 *   e) Scroll down and navigate to the calendars on the right side
 *   f) Uncheck any one checkbox from the right section
 *   g) Scroll down and verify the Add to My Calendar button is displayed
 */
public class Scenario5_AcademicCalendarTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario5_AcademicCalendar";

    @Test(description = "Update the Academic Calendar")
    public void updateAcademicCalendar() throws InterruptedException {

        ExtentTest test = ReportManager.createTest("Scenario 5: Update the Academic Calendar");

        // Read login data from Excel
        Map<String, String> loginData = ExcelReader.getRowData(TEST_DATA_PATH, "LoginData", 0);
        String username = loginData.get("Username");
        String password = loginData.get("Password");

        try {
            // Step a) Navigate to Student Hub
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_NavigateHub");
            driver.get("https://me.northeastern.edu");
            ReportManager.logInfo(test, "Navigated to NEU portal.");

            // NEU SSO Login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.name("_eventId_proceed")).click();
            ReportManager.logInfo(test, "Entered credentials and clicked login.");

            // Handle Duo 2FA
            ReportManager.logInfo(test, "Waiting for Duo 2FA approval...");
            Thread.sleep(20000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_NavigateHub");

            // Navigate to Student Hub
            driver.get("https://student.me.northeastern.edu/");
            wait.until(ExpectedConditions.titleContains("Student"));
            ReportManager.logInfo(test, "Navigated to Student Hub.");

            // Click on Resources
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_ClickResources");
            WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Resources')]")));
            resourcesTab.click();
            ReportManager.logInfo(test, "Clicked on Resources tab.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_ClickResources");

            // Step b) Click on Academics, Classes & Registration
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_ClickAcademics");
            WebElement academicsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Academics')]")));
            academicsLink.click();
            ReportManager.logInfo(test, "Clicked on Academics, Classes & Registration.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_ClickAcademics");

            // Step c) Click on Academic Calendar
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_C_ClickCalendar");
            WebElement calendarLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Academic Calendar')]")));
            calendarLink.click();
            ReportManager.logInfo(test, "Clicked on Academic Calendar.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_C_ClickCalendar");

            // Switch to new window if it opened in a new tab
            String mainWindow = driver.getWindowHandle();
            for (String window : driver.getWindowHandles()) {
                if (!window.equals(mainWindow)) {
                    driver.switchTo().window(window);
                    break;
                }
            }

            // Step d) Click on Academic Calendar under Registrar
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_D_ClickRegistrarCalendar");
            wait.until(ExpectedConditions.urlContains("registrar"));
            WebElement academicCalendarLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Academic Calendar') and contains(@href,'academic-calendar')]")));
            academicCalendarLink.click();
            ReportManager.logInfo(test, "Clicked on Academic Calendar under Registrar.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_D_ClickRegistrarCalendar");

            // Step e) Scroll down to the Calendars section on the right side
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_E_ScrollToCalendars");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Scroll to the Calendars checkboxes section
            WebElement calendarsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[text()='Calendars']")));
            js.executeScript("arguments[0].scrollIntoView(true);", calendarsSection);
            ReportManager.logInfo(test, "Scrolled to Calendars section.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_E_ScrollToCalendars");

            // Step f) Uncheck one checkbox (Quarter - CPS Graduate)
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_F_UncheckCalendar");
            WebElement qtrCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='checkbox' and following-sibling::*[contains(text(),'Quarter')] or @id[contains(.,'qtr')]]")));

            // If checkbox is checked, uncheck it
            if (qtrCheckbox.isSelected()) {
                qtrCheckbox.click();
                ReportManager.logInfo(test, "Unchecked Quarter - CPS Graduate (QTR) checkbox.");
            } else {
                // Try clicking the label/text instead
                WebElement qtrLabel = driver.findElement(
                        By.xpath("//*[contains(text(),'Quarter - CPS Graduate')]"));
                qtrLabel.click();
                ReportManager.logInfo(test, "Clicked on Quarter - CPS Graduate label to toggle.");
            }
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_F_UncheckCalendar");

            // Verify the checkbox is now unchecked
            Assert.assertFalse(qtrCheckbox.isSelected(),
                    "Quarter - CPS Graduate checkbox should be unchecked.");
            ReportManager.logInfo(test, "Verified checkbox is unchecked.");

            // Step g) Scroll down and verify Add to My Calendar button is displayed
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_G_VerifyAddButton");
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(2000);

            WebElement addToCalendarButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@value='Add to My Calendar'] | //button[contains(text(),'Add to My Calendar')]")));
            Assert.assertTrue(addToCalendarButton.isDisplayed(),
                    "Add to My Calendar button should be displayed.");
            ReportManager.logInfo(test, "Verified Add to My Calendar button is displayed.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_G_VerifyAddButton");

            ReportManager.logPass(test, "Academic Calendar updated and Add to My Calendar button visible",
                    "Checkbox unchecked and button verified successfully");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "Academic Calendar updated and Add to My Calendar button visible",
                    "Failed: " + e.getMessage());
            Assert.fail("Scenario 5 failed: " + e.getMessage());
        }
    }
}