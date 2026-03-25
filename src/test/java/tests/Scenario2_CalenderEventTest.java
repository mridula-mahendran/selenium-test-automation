package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.util.Map;

/**
 * Scenario 2: Add two Event tasks for yourself on Canvas Calendar.
 * Steps:
 *   a) Log in to Canvas and open the Calendar. Click on the "+" button.
 *   b) Create 2 events iteratively and submit them.
 *      All data (Title, Date, Time, Calendar, Details) read from Excel.
 */
public class Scenario2_CalendarEventTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario2_CalendarEvents";

    @Test(description = "Add two calendar events on Canvas")
    public void addCalendarEvents() throws InterruptedException {

        ExtentTest test = ReportManager.createTest("Scenario 2: Add Two Calendar Events");

        // Read login data from Excel
        Map<String, String> loginData = ExcelReader.getRowData(TEST_DATA_PATH, "LoginData", 0);
        String username = loginData.get("Username");
        String password = loginData.get("Password");

        try {
            // Step a) Log in to Canvas
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Login");
            driver.get("https://northeastern.instructure.com/");
            ReportManager.logInfo(test, "Navigated to Canvas.");

            // NEU SSO login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.name("_eventId_proceed")).click();
            ReportManager.logInfo(test, "Entered credentials and clicked login.");

            // Handle Duo 2FA
            ReportManager.logInfo(test, "Waiting for Duo 2FA approval...");
            Thread.sleep(20000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Login");

            // Navigate to Calendar
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Calendar");
            driver.get("https://northeastern.instructure.com/calendar");
            wait.until(ExpectedConditions.urlContains("calendar"));
            ReportManager.logInfo(test, "Navigated to Canvas Calendar.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Calendar");

            // Step b) Create 2 events iteratively from Excel data
            int eventCount = ExcelReader.getRowCount(TEST_DATA_PATH, "CalendarEvents");

            for (int i = 0; i < eventCount; i++) {
                // Read event data from Excel
                Map<String, String> eventData = ExcelReader.getRowData(TEST_DATA_PATH, "CalendarEvents", i);
                String title = eventData.get("Title");
                String date = eventData.get("Date");
                String startTime = eventData.get("StartTime");
                String endTime = eventData.get("EndTime");
                String calendar = eventData.get("Calendar");
                String details = eventData.get("Details");

                ReportManager.logInfo(test, "Creating Event " + (i + 1) + ": " + title);

                // Click the "+" button to open the Edit Event dialog
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_ClickPlus");
                WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("create_new_event_link")));
                addButton.click();
                Thread.sleep(1000);
                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_ClickPlus");

                // Wait for the Edit Event dialog to appear
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//span[text()='Edit Event']/..")));

                // Fill in Title
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_FillTitle");
                WebElement titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@placeholder='Input Event Title...']")));
                titleField.clear();
                titleField.sendKeys(title);
                ReportManager.logInfo(test, "Entered title: " + title);

                // Fill in Date
                WebElement dateField = driver.findElement(
                        By.xpath("//input[contains(@placeholder,'Date') or @type='text' and ancestor::*[contains(.,'Date')]]"));
                dateField.clear();
                dateField.sendKeys(date);
                dateField.sendKeys(Keys.TAB);
                ReportManager.logInfo(test, "Entered date: " + date);

                // Select Start Time
                WebElement startTimeDropdown = driver.findElement(
                        By.xpath("//select[contains(@aria-label,'Start Time') or ancestor::*[contains(.,'From')]/select]"));
                Select startSelect = new Select(startTimeDropdown);
                startSelect.selectByVisibleText(startTime);
                ReportManager.logInfo(test, "Selected start time: " + startTime);

                // Select End Time
                WebElement endTimeDropdown = driver.findElement(
                        By.xpath("//select[contains(@aria-label,'End Time') or ancestor::*[contains(.,'To')]/select]"));
                Select endSelect = new Select(endTimeDropdown);
                endSelect.selectByVisibleText(endTime);
                ReportManager.logInfo(test, "Selected end time: " + endTime);

                // Select Calendar
                WebElement calendarDropdown = driver.findElement(
                        By.xpath("//select[ancestor::*[contains(.,'Calendar')]]"));
                Select calendarSelect = new Select(calendarDropdown);
                calendarSelect.selectByVisibleText(calendar);
                ReportManager.logInfo(test, "Selected calendar: " + calendar);

                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_FillTitle");

                // Click "More Options" to access the Details field
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_MoreOptions");
                WebElement moreOptionsButton = driver.findElement(
                        By.xpath("//button[contains(text(),'More Options')] | //a[contains(text(),'More Options')]"));
                moreOptionsButton.click();
                Thread.sleep(2000);
                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_MoreOptions");

                // Fill in Details using the rich text editor
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_FillDetails");

                // Switch to the rich text editor iframe
                WebElement editorIframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("iframe[id*='editor']")));
                driver.switchTo().frame(editorIframe);

                // Type details into the editor body
                WebElement editorBody = driver.findElement(By.id("tinymce"));
                editorBody.clear();
                editorBody.sendKeys(details);
                ReportManager.logInfo(test, "Entered details: " + details);

                // Switch back to main content
                driver.switchTo().defaultContent();

                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_FillDetails");

                // Click "Create Event" button to submit
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_Submit");
                WebElement createEventButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Create Event')]")));
                createEventButton.click();
                ReportManager.logInfo(test, "Clicked Create Event for: " + title);
                Thread.sleep(3000);
                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_Submit");

                // Verify event was added to calendar
                wait.until(ExpectedConditions.urlContains("calendar"));
                String pageSource = driver.getPageSource();
                Assert.assertTrue(pageSource.contains(title),
                        "Event '" + title + "' was not found on the calendar.");
                ReportManager.logInfo(test, "Verified event '" + title + "' is on the calendar.");
            }

            // Final verification screenshot
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "AllEventsAdded");
            ReportManager.logPass(test, "2 events added to calendar", "2 events successfully created and visible");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "2 events added to calendar", "Failed: " + e.getMessage());
            Assert.fail("Scenario 2 failed: " + e.getMessage());
        }
    }
}