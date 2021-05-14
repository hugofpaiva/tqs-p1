package com.hugopaiva.airqualityservice.selenium;

import static io.github.bonigarcia.seljup.BrowserType.FIREFOX;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

// SpringBootTest to run the REST API
@ExtendWith(SeleniumJupiter.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheReportTest {

    private String webApplicationBaseUrl = "172.17.0.1";

    @Test
    @Order(1)
    void testGetRequestAndFoundNone(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
        driver.get("http://" + webApplicationBaseUrl + ":4200");
        driver.manage().window().setSize(new Dimension(1792, 1022));
        driver.findElement(By.cssSelector(".nav-item:nth-child(2) p")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".col-md-3:nth-child(1) h5")));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table > .ng-star-inserted")));
        }
        assertThat(driver.findElement(By.cssSelector(".table > .ng-star-inserted")).getText(), is("There is no requests history"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(1) h5")).getText(), is("0"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(2) h5")).getText(), is("0"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(3) h5")).getText(), is("0"));
    }

    @Test
    @Order(2)
    void testGetActualMeasurementCoordinatesAndCheckRequests(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
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
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".main-content > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > h5:nth-child(2)")));
        }
        driver.findElement(By.cssSelector(".nav-item:nth-child(2) p")).click();
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(1) h5")).getText(), is("0"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(2) h5")).getText(), is("1"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(3) h5")).getText(), is("1"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(3)")).getText(), is("MISS"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(4)")).getText(), is("42"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(5)")).getText(), is("56"));
        driver.findElement(By.cssSelector("li.nav-item:nth-child(1) > a:nth-child(1)")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".modal-body > div:nth-child(1) > input")));

        }
        driver.findElement(By.cssSelector(".modal-body > div:nth-child(1) > input")).click();
        driver.findElement(By.cssSelector(".modal-body > div:nth-child(1) > input")).sendKeys("42");
        driver.findElement(By.cssSelector(".modal-body > div:nth-child(2) > input")).click();
        driver.findElement(By.cssSelector(".modal-body > div:nth-child(2) > input")).sendKeys("56");
        driver.findElement(By.cssSelector(".fa-search")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".main-content > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > h5:nth-child(2)")));
        }

        driver.findElement(By.cssSelector(".nav-item:nth-child(2) p")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".col-md-3:nth-child(1) h5")));
        }
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(1) h5")).getText(), is("1"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(2) h5")).getText(), is("2"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(3) h5")).getText(), is("1"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(3)")).getText(), is("HIT"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(4)")).getText(), is("42"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(5)")).getText(), is("56"));

    }

    @Test
    @Order(3)
    void testGetActualMeasurementLocationAndCheckRequests(@DockerBrowser(type = FIREFOX, version = "84") RemoteWebDriver driver) {
        driver.get("http://" + webApplicationBaseUrl + ":4200/");
        driver.manage().window().setSize(new Dimension(1792, 1025));
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
        driver.findElement(By.cssSelector(".nav-item:nth-child(2) p")).click();
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(1) h5")).getText(), is("1"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(2) h5")).getText(), is("3"));
        assertThat(driver.findElement(By.cssSelector(".col-md-3:nth-child(3) h5")).getText(), is("2"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(3)")).getText(), is("MISS"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(4)")).getText(), is("38.7167"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(5)")).getText(), is("-9.1333"));
        assertThat(driver.findElement(By.cssSelector("table.table > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(6)")).getText(), is("Lisbon, PT"));

    }

}

