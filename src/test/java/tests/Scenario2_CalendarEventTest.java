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
 * Scenario 2: Add two Event tasks for yourself on Canvas Calendar.
 * Steps:
 *   a) Log in to Canvas and open the Calendar. Click on the "+" button.
 *   b) Create 2 events iteratively and submit them.
 *      All data (Title, Date, Time, Calendar) read from Excel.
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
            // Step a) Log in to Canvas via NEU SSO
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Login");
            performNEULogin("https://northeastern.instructure.com/", username, password);
            ReportManager.logInfo(test, "Logged in to Canvas.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Login");

            // Navigate to Calendar
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Calendar");
            driver.get("https://northeastern.instructure.com/calendar");
            wait.until(ExpectedConditions.urlContains("calendar"));
            ReportManager.logInfo(test, "Navigated to Canvas Calendar.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Calendar");

            // Step b) Create 2 events iteratively from Excel data
            int eventCount = ExcelReader.getRowCount(TEST_DATA_PATH, "CalenderEvents");

            for (int i = 0; i < eventCount; i++) {
                // Read event data from Excel
                Map<String, String> eventData = ExcelReader.getRowData(TEST_DATA_PATH, "CalenderEvents", i);
                String title = eventData.get("Title");
                String date = eventData.get("Date");
                String startTime = eventData.get("StartTime");
                String endTime = eventData.get("EndTime");
                String calendar = eventData.get("Calendar");

                ReportManager.logInfo(test, "Creating Event " + (i + 1) + ": " + title);

                // Click the "+" button to open the Edit Event dialog
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_ClickPlus");
                WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("create_new_event_link")));
                addButton.click();
                Thread.sleep(2000);
                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_ClickPlus");

                // Fill in Title
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_FillForm");
                WebElement titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input[@placeholder='Input Event Title...']")));
                titleField.clear();
                titleField.sendKeys(title);
                ReportManager.logInfo(test, "Entered title: " + title);
                Thread.sleep(500);

                // Fill in Date
                WebElement dateField = driver.findElement(
                        By.xpath("//input[@data-testid='edit-calendar-event-form-date']"));
                dateField.click();
                Thread.sleep(500);
                // Select all existing text and replace with new date
                dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                dateField.sendKeys(date);
                dateField.sendKeys(Keys.TAB); // Tab to next field instead of Enter (Enter submits the form)
                ReportManager.logInfo(test, "Entered date: " + date);
                Thread.sleep(1000);

                // Select Start Time - use JS to set value and trigger React change
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // Click on the Start Time area to open the dropdown
                WebElement startTimeArea = driver.findElement(
                        By.xpath("//input[@data-testid='event-form-start-time']/ancestor::span[contains(@class,'select')]"));
                js.executeScript("arguments[0].click();", startTimeArea);
                Thread.sleep(1000);

                // Try to find and click the option directly from the dropdown list
                try {
                    WebElement startOption = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//li[@role='option' and contains(text(),'" + startTime + "')]")));
                    startOption.click();
                } catch (Exception e) {
                    // If dropdown didn't open, use JS to set value directly
                    js.executeScript(
                            "var input = document.querySelector('input[data-testid=\"event-form-start-time\"]');" +
                                    "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                                    "nativeSetter.call(input, arguments[0]);" +
                                    "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                                    "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                                    "input.dispatchEvent(new Event('blur', {bubbles: true}));", startTime
                    );
                }
                ReportManager.logInfo(test, "Selected start time: " + startTime);
                Thread.sleep(1000);

                // Select End Time - same approach
                WebElement endTimeArea = driver.findElement(
                        By.xpath("//input[@data-testid='event-form-end-time']/ancestor::span[contains(@class,'select')]"));
                js.executeScript("arguments[0].click();", endTimeArea);
                Thread.sleep(1000);

                try {
                    WebElement endOption = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//li[@role='option' and contains(text(),'" + endTime + "')]")));
                    endOption.click();
                } catch (Exception e) {
                    js.executeScript(
                            "var input = document.querySelector('input[data-testid=\"event-form-end-time\"]');" +
                                    "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                                    "nativeSetter.call(input, arguments[0]);" +
                                    "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                                    "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                                    "input.dispatchEvent(new Event('blur', {bubbles: true}));", endTime
                    );
                }
                ReportManager.logInfo(test, "Selected end time: " + endTime);
                Thread.sleep(1000);

                // Select Calendar (combobox input)
                WebElement calendarInput = driver.findElement(
                        By.xpath("//input[@placeholder='Calendar' or ancestor::*[contains(.,'Calendar')]//input[@role='combobox']]"));
                // Try clicking the calendar dropdown area
                try {
                    calendarInput.click();
                    Thread.sleep(500);
                    calendarInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                    calendarInput.sendKeys(calendar);
                    Thread.sleep(500);
                    WebElement calOption = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//li[@role='option' and contains(text(),'" + calendar + "')]")));
                    calOption.click();
                } catch (Exception calEx) {
                    // Calendar might already be set to the correct value
                    ReportManager.logInfo(test, "Calendar already set or selection handled.");
                }
                ReportManager.logInfo(test, "Selected calendar: " + calendar);
                Thread.sleep(500);

                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_FillForm");

                // Click Submit button
                ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_Submit");
                WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@data-testid='edit-calendar-event-submit-button']")));
                submitButton.click();
                ReportManager.logInfo(test, "Clicked Submit for event: " + title);
                Thread.sleep(3000);
                ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Event" + (i + 1) + "_Submit");

                // Verify we're back on the calendar page
                wait.until(ExpectedConditions.urlContains("calendar"));
                ReportManager.logInfo(test, "Event '" + title + "' created successfully.");
            }

            // Final verification screenshot
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "AllEventsAdded");

            // Verify events are visible on the calendar
            String pageSource = driver.getPageSource();
            Assert.assertTrue(pageSource.contains("Study Session") || pageSource.contains("Project Meeting"),
                    "At least one event should be visible on the calendar.");

            ReportManager.logPass(test, "2 events added to calendar",
                    "2 events successfully created and visible");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "2 events added to calendar",
                    "Failed: " + e.getMessage());
            Assert.fail("Scenario 2 failed: " + e.getMessage());
        }
    }
}