package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.EncryptionHelper;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.util.Map;

/**
 * Scenario 5: Update the Academic Calendar.
 * Steps:
 *   a) Navigate to Student Hub and click on Resources
 *   b) Click on Academics, Classes & Registration
 *   c) Click on Academic Calendar from the expanded list
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
        // Decrypt the password from Excel (stored as Base64 encrypted)
        String password = EncryptionHelper.decrypt(loginData.get("Password"));

        try {
            // Step a) Log in and navigate to Student Hub
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Login");
            performNEULogin("https://me.northeastern.edu", username, password);
            ReportManager.logInfo(test, "Logged in to NEU portal.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Login");

            // Navigate to Student Hub
            driver.get("https://student.me.northeastern.edu/");
            wait.until(ExpectedConditions.urlContains("student.me.northeastern"));
            ReportManager.logInfo(test, "Navigated to Student Hub.");
            Thread.sleep(2000);

            // Click on Resources
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_ClickResources");
            WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Resources')]")));
            resourcesTab.click();
            ReportManager.logInfo(test, "Clicked on Resources tab.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_ClickResources");

            // Step b) Click on Academics, Classes & Registration tile
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_ClickAcademics");
            WebElement academicsTile = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'fui-Tab__content') and contains(text(),'Academics')]")));
            academicsTile.click();
            ReportManager.logInfo(test, "Clicked on Academics, Classes & Registration tile.");
            Thread.sleep(3000); // Wait for the expanded list to fully load
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_ClickAcademics");

            // Step c) Click on Academic Calendar from the EXPANDED LIST (not Recent Links)
            // The expanded list links have data-gtm-resources-link-section="Main"
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_C_ClickCalendar");
            WebElement calendarLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@data-gtm-resources-link='Academic Calendar' and @data-gtm-resources-link-section='Main']")));
            calendarLink.click();
            ReportManager.logInfo(test, "Clicked on Academic Calendar from expanded list.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_C_ClickCalendar");

            // Switch to new window if it opened in a new tab (target="_blank")
            String mainWindow = driver.getWindowHandle();
            for (String window : driver.getWindowHandles()) {
                if (!window.equals(mainWindow)) {
                    driver.switchTo().window(window);
                    break;
                }
            }

            // Step d) Click on "Academic Calendar" on the Registrar calendar page
            // We should now be on registrar.northeastern.edu/group/calendar/
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_D_ClickRegistrarCalendar");
            Thread.sleep(3000);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            WebElement academicCalendarLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href,'academic-calendar') and contains(@class,'__item')]")));
            academicCalendarLink.click();
            ReportManager.logInfo(test, "Clicked on Academic Calendar under Registrar.");
            Thread.sleep(5000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_D_ClickRegistrarCalendar");

            // Step e) Scroll down to the Calendars section on the right side
            // The calendar is inside an iframe (trumba.spud.7.iframe), so we need to switch into it
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_E_ScrollToCalendars");
            Thread.sleep(3000);

            // First scroll down to make the iframe visible
            js.executeScript("window.scrollBy(0, 1000)");
            Thread.sleep(2000);

            // Switch into the Calendar List iframe by name
            driver.switchTo().frame("trumba.spud.7.iframe");
            ReportManager.logInfo(test, "Switched to Calendar List iframe.");
            Thread.sleep(3000); // Wait for iframe content to fully load

            ReportManager.logInfo(test, "Scrolled to Calendars section.");

            // Switch back to main page for screenshot
            driver.switchTo().defaultContent();
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_E_ScrollToCalendars");

            // Step f) Uncheck one checkbox — the checkboxes are <a> tags with class "twCalendarListName" inside the iframe
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_F_UncheckCalendar");

            // Switch back into the iframe by name
            driver.switchTo().frame("trumba.spud.7.iframe");
            Thread.sleep(2000);

            // Click on the checkbox for "Quarter - CPS Graduate (QTR)" to uncheck it
            WebElement qtrCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='checkbox' and contains(@aria-label,'Quarter')]")));
            qtrCheckbox.click();
            ReportManager.logInfo(test, "Clicked on Quarter - CPS Graduate (QTR) to toggle.");
            Thread.sleep(2000);

            // Switch back to main page for screenshot
            driver.switchTo().defaultContent();
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_F_UncheckCalendar");
            ReportManager.logInfo(test, "Checkbox toggled successfully.");

            // Step g) Scroll down and verify Add to My Calendar button is displayed
            // The button is inside the "List Calendar View" iframe (trumba.spud.2.iframe)
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_G_VerifyAddButton");
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(2000);

            // Switch into the List Calendar View iframe
            driver.switchTo().frame("trumba.spud.2.iframe");
            Thread.sleep(2000);

            WebElement addToCalendarButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[.//span[contains(text(),'Add to My Calendar')]]")));

            Assert.assertTrue(addToCalendarButton.isDisplayed(),
                    "Add to My Calendar button should be displayed.");
            ReportManager.logInfo(test, "Verified Add to My Calendar button is displayed.");

            driver.switchTo().defaultContent();
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