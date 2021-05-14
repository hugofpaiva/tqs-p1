package com.hugopaiva.airqualityservice.selenium;

import static io.github.bonigarcia.seljup.BrowserType.FIREFOX;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

// SpringBootTest to run the REST API
@ExtendWith(SeleniumJupiter.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ActualMeasurementTest {

    private String webApplicationBaseUrl = "172.17.0.1";

    @Test
    void testGetActualMeasurementCoordinates(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
        driver.get("http://" + webApplicationBaseUrl + ":4200/");
        driver.manage().window().setSize(new Dimension(1792, 1025));
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h3")));
        }
        assertEquals("Air Quality", driver.getTitle());
        assertEquals("Search by Coordinates", driver.findElement(By.cssSelector("h3")).getText());
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .ng-pristine")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .ng-pristine")).sendKeys("42");
        driver.findElement(By.cssSelector(".form-group:nth-child(2) > .form-control")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(2) > .form-control")).sendKeys("56");
        driver.findElement(By.cssSelector(".fa-search")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".card:nth-child(1) > .ng-star-inserted")));
        }
        assertThat(driver.findElement(By.cssSelector(".card:nth-child(1) > .ng-star-inserted")).getText(), is("42º, 56º"));
        assertThat(Integer.parseInt(driver.findElement(By.cssSelector(".main-content > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > h5:nth-child(2)"))
                .getText()), is(greaterThanOrEqualTo(0)));
    }

    @Test
    void testGetActualMeasurementInvalidCoordinates(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
        driver.get("http://" + webApplicationBaseUrl + ":4200/");
        driver.manage().window().setSize(new Dimension(1792, 1020));
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h3")));
        }
        assertEquals("Air Quality", driver.getTitle());
        assertEquals("Search by Coordinates", driver.findElement(By.cssSelector("h3")).getText());
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .ng-pristine")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(1) > .ng-pristine")).sendKeys("-96");
        driver.findElement(By.cssSelector(".form-group:nth-child(2) > .form-control")).click();
        driver.findElement(By.cssSelector(".form-group:nth-child(2) > .form-control")).sendKeys("195");
        assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(1) > .invalid-feedback > .ng-star-inserted")).getText(), is("Latitude must be between -90 and 90"));
        assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(2) > .invalid-feedback > .ng-star-inserted")).getText(), is("Longitude must be between -180 and 180"));
        assertThat(driver.findElement(By.cssSelector(".btn")).isEnabled(), is(Boolean.FALSE));
    }

    @Test
    void testGetActualMeasurementLocation(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
        driver.get("http://" + webApplicationBaseUrl + ":4200/");
        driver.manage().window().setSize(new Dimension(1792, 1022));
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h3")));
        }
        assertEquals("Air Quality", driver.getTitle());
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-control:nth-child(1)"));
            dropdown.findElement(By.xpath("//option[. = 'Search by Location']")).click();
        }
        assertEquals("Search by Location", driver.findElement(By.cssSelector("h3")).getText());
        driver.findElement(By.cssSelector("option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".form-control:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".form-control:nth-child(2)")).sendKeys("Lisboa");
        driver.findElement(By.cssSelector(".fa-search")).click();
        {
            WebElement element = driver.findElement(By.cssSelector(".fa-search"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h5.ng-star-inserted")));
        }
        assertThat(driver.findElement(By.cssSelector("h5.ng-star-inserted")).getText(), is("38.7167º, -9.1333º - Lisbon, PT"));
        assertThat(Integer.parseInt(driver.findElement(By.cssSelector(".main-content > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > h5:nth-child(2)"))
                .getText()), is(greaterThanOrEqualTo(0)));
    }

    @Test
    void testGetActualMeasurementInvalidLocation(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
        driver.get("http://" + webApplicationBaseUrl + ":4200/");
        driver.manage().window().setSize(new Dimension(1792, 1022));
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h3")));
        }
        assertEquals( "Air Quality", driver.getTitle());
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-control:nth-child(1)"));
            dropdown.findElement(By.xpath("//option[. = 'Search by Location']")).click();
        }
        assertEquals("Search by Location", driver.findElement(By.cssSelector("h3")).getText());
        driver.findElement(By.cssSelector("option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".form-control:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".form-control:nth-child(2)")).sendKeys("sadasdasdasdads");
        driver.findElement(By.cssSelector(".fa-search")).click();
        {
            WebElement element = driver.findElement(By.cssSelector(".fa-search"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.ng-star-inserted:nth-child(1) > h3:nth-child(1)")));
        }
        assertThat(driver.findElement(By.cssSelector("div.ng-star-inserted:nth-child(1) > h3:nth-child(1)")).getText(), is("Location not found!"));
    }

}
