/*******************************************************************************
 * Filename: LoggerDemoTest.java
 * Author: David N.
 * Version: Dec 2015
 * Description: To demonstrate the use of the TestNG dataprovider feature. This feature includes the use
 * of the JXL API that allows us to read data from an Excel spreadsheet (.xls).
 * Note: File has not been optimized with abstraction. I had to troublseshoot the code for it to run. In
 * addition, I needed to add explicit sleeps because of a timing issue.
 * Original Source: http://bit.ly/1P9xZ3W
 *******************************************************************************/

package com.myautomation.test;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadExcelDataProvider {
	public WebDriver driver;
	public WebDriverWait wait;
	String appURL = "https://www.linkedin.com/uas/login?goback=&trk=hb_signin";
	
	private By byEmail = By.id("session_key-login");
	private By byPassword = By.id("session_password-login");
	private By bySubmit = By.id("btn-primary");
	private By byError = By.id("session_key-login-error");

	
	@BeforeClass
	public void testSetup() {
		driver=new FirefoxDriver();
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, 30);
	}

    @AfterClass
	public void tearDown() {
		driver.close();
        driver.quit();
	}

	@Test(dataProvider="empLogin")
	public void VerifyInvalidLogin(String userName, String password) throws InterruptedException {
		driver.navigate().to(appURL);
        //Added sleep(s) because of timing issue.
        Thread.sleep(1000);


		driver.findElement(byEmail).sendKeys(userName);
		driver.findElement(byPassword).sendKeys(password);

		//Wait for the element to ve visible. Added sleep(s) because of timing issue.
		wait.until(ExpectedConditions.visibilityOfElementLocated(bySubmit));
        //Thread.sleep(1000);
		driver.findElement(bySubmit).click();
		
		//Check for error message
		wait.until(ExpectedConditions.presenceOfElementLocated(byError));
        Thread.sleep(1000);
		String actualErrorDisplayed = driver.findElement(byError).getText();
        String requiredErrorMessage = "Please enter a valid email address.";

		Assert.assertEquals(requiredErrorMessage, actualErrorDisplayed);
		
	}
	
	@DataProvider(name="empLogin")
	public Object[][] loginData() {
		Object[][] arrayObject = getExcelData("d:/sampledoc.xls","Sheet1");
		return arrayObject;
	}

	/**
	 * @param File Name
	 * @param Sheet Name
	 * @return
	 */
	public String[][] getExcelData(String fileName, String sheetName) {
		String[][] arrayExcelData = null;
		try {
			FileInputStream fs = new FileInputStream(fileName);
			Workbook workbook = Workbook.getWorkbook(fs);
			Sheet sheet = workbook.getSheet(sheetName);

			int totalNoOfCols = sheet.getColumns();
			int totalNoOfRows = sheet.getRows();
			
			arrayExcelData = new String[totalNoOfRows-1][totalNoOfCols];
			
			for (int i= 1 ; i < totalNoOfRows; i++) {

				for (int j=0; j < totalNoOfCols; j++) {
					arrayExcelData[i-1][j] = sheet.getCell(j, i).getContents();
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
		return arrayExcelData;
	}
}