package com.onlinerautomation.utils;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.hamcrest.FeatureMatcher;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.htmlelements.element.Named;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class ElementsUtil {

    private static final int DEFAULT_EXPLICIT_WAIT = 30;

    private static final Function<Supplier<WebElement>, Boolean> tryElement = r -> Try.of(() -> r.get().isDisplayed()).getOrElse(() -> false);
    private static final String ELEMENT_IS_NOT_CLICKABLE = "Element is not clickable";
    private static final String ELEMENT_HAS_CLASS = "Element has given class";
    private static final String ELEMENT_NO_CLASS = "Element does not have given class";
    private static final String ELEMENT_IS_NOT_DISPLAYED = "Element is not displayed";
    private static final String ELEMENT_IS_NOT_HIDDEN = "Element is not hidden";

    // region Accessors
    public static boolean isDisplayed(WebDriver driver, By by) {
        return Try.of(() -> driver.findElement(by).isDisplayed()).getOrElse(false);
    }

    public static boolean isDisplayed(WebElement element) {
        return tryElement.apply(() -> element);
    }

    public static boolean isDisplayed(WebElement element, int seconds) {
        return Try.of(() -> {
            waitFor(seconds).until(() -> isDisplayed(element));
            return true;
        }).getOrElse(() -> false);
    }

    public static boolean isDisplayed(WebElement parentElement, By by, int seconds) {
        return Try.of(() -> {
            waitFor(seconds).until(() -> isDisplayed((Supplier<WebElement>) parentElement.findElement(by)));
            return true;
        }).getOrElse(() -> false);
    }

    public static boolean isDisplayed(WebElement parentElement, By by) {
        return tryElement.apply(() -> parentElement.findElement(by));
    }

    public static boolean isDisplayed(Supplier<WebElement> supplier) {
        return Try.of(() -> supplier.get().isDisplayed()).getOrElse(false);
    }

    public static <T extends WebElement> boolean isAllDisplayed(List<T> elements) {
        return isAllDisplayed(elements.toArray(new WebElement[0]));
    }

    public static boolean isAllDisplayed(WebElement... elements) {
        return Stream.of(elements).allMatch(b -> tryElement.apply(() -> b));
    }

    public static boolean isAllDisplayed(WebDriver driver, By... selectors) {
        return Stream.of(selectors).allMatch(b -> tryElement.apply(() -> driver.findElement(b)));
    }

    public static boolean isAnyDisplayed(List<WebElement> elements) {
        return isAnyDisplayed(elements.toArray(new WebElement[0]));
    }

    public static boolean isAnyDisplayed(WebElement... elements) {
        return Stream.of(elements).anyMatch(e -> tryElement.apply(() -> e));
    }

    public static boolean isAnyDisplayed(WebDriver driver, By... selectors) {
        return Stream.of(selectors).anyMatch(b -> tryElement.apply(() -> driver.findElement(b)));
    }

    public static boolean isAnyDisplayed(WebElement parentElement, By... selectors) {
        return Stream.of(selectors).anyMatch(b -> tryElement.apply(() -> parentElement.findElement(b)));
    }

    public static boolean isClickable(WebElement element) {
        return Try.of(() -> (element.isDisplayed() && element.isEnabled())).getOrElse(false);
    }

    public static boolean isClickable(WebDriver driver, By by) {
        return Try.of(() -> {
            WebElement webElement = driver.findElement(by);
            return webElement.isDisplayed() && webElement.isEnabled();
        }).getOrElse(false);
    }

    public static boolean isElementPresented(WebDriver driver, By by) {
        return !driver.findElements(by).isEmpty();
    }

    /**
     * Wait until an element is no longer attached to the DOM.
     * This modified version from Selenium ExpectedConditions, but without stupid ExpectedCondition<Boolean> as return
     *
     * @param element The element to wait for.
     * @return false is the element is still attached to the DOM, true otherwise.
     */
    public static boolean isStale(WebElement element) {
        try {
            // Calling any method forces a staleness check
            element.isEnabled();
            return false;
        } catch (StaleElementReferenceException expected) {
            return true;
        }
    }

    public static boolean isAlertPresent(WebDriver driver) {
        return Try.of(() -> {
            driver.switchTo().alert();
            return true;
        }).getOrElse(false);
    }

    public static boolean isClassPresent(String className, WebElement element) {
        return Arrays.asList(element.getAttribute("class").split(" ")).contains(className);
    }

    public static boolean isTextPresent(WebElement element, String text) {
        return Try.of(() -> element.getText().contains(text)).getOrElse(false);
    }

    public static Boolean elementHasStoppedMoving(WebElement element) {
        Point initialLocation = ((Locatable) element).getCoordinates().inViewPort();
        sleepUninterruptibly(250, MILLISECONDS);
        Point finalLocation = ((Locatable) element).getCoordinates().inViewPort();
        return initialLocation.equals(finalLocation);
    }

    // endregion Accessors

    // region Waiters
    public static void waitForVisible(WebElement element) {
        waitForVisible(element, DEFAULT_EXPLICIT_WAIT);
    }

    public static void waitForVisible(WebElement element, By childElem, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(element, ELEMENT_IS_NOT_DISPLAYED))
                .until(() -> isDisplayed((Supplier<WebElement>) element.findElement(childElem)));
    }

    public static void waitForVisible(WebDriver driver, By by) {
        waitForVisible(driver, by, DEFAULT_EXPLICIT_WAIT);
    }

    public static WebElement waitForVisible(WebElement element, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(element, ELEMENT_IS_NOT_DISPLAYED))
                .until(() -> isDisplayed(element));
        return element;
    }

    private static String addName(WebElement element, String message) {
        String postfix = (element instanceof Named) ? ": [" + ((Named) element).getName() + "]" : "";
        return message + postfix;
    }

    private static String addName(By by, String message) {
        return by.toString() + message;
    }


    public static void waitForVisible(WebDriver driver, By by, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(by, ELEMENT_IS_NOT_DISPLAYED))
                .until(() -> isDisplayed(driver, by));
    }

    public static void waitForNotVisible(WebDriver driver, By selector, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(selector, ELEMENT_IS_NOT_DISPLAYED))
                .until(() -> !isDisplayed(driver, selector));
    }

    public static void waitForNotVisible(WebElement element) {
        waitForNotVisible(element, DEFAULT_EXPLICIT_WAIT);
    }

    public static void waitForNotVisible(WebElement element, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(element, ELEMENT_IS_NOT_HIDDEN))
                .until(() -> !isDisplayed(element));
    }

    public static WebElement waitForClickable(WebElement element) {
        return waitForClickable(element, DEFAULT_EXPLICIT_WAIT);
    }

    public static void waitForClickable(WebElement element, By childElem, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(element, ELEMENT_IS_NOT_CLICKABLE))
                .until(() -> isClickable(element.findElement(childElem)));
    }

    public static void waitForClickable(WebDriver driver, By by) {
        waitForClickable(driver, by, DEFAULT_EXPLICIT_WAIT);
    }

    public static WebElement waitForClickable(WebElement element, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(element, ELEMENT_IS_NOT_CLICKABLE))
                .until(() -> isClickable(element));
        return element;
    }

    public static void waitForClickable(WebDriver driver, By by, int seconds) {
        waitFor(seconds)
                .withException(() -> addName(by, ELEMENT_IS_NOT_CLICKABLE))
                .until(() -> isClickable(driver, by));
    }

    public static void waitForClassPresent(String className, WebElement element, int seconds) {
        waitFor(seconds)
                .withException(() -> className + " - " + addName(element, ELEMENT_NO_CLASS))
                .until(() -> isClassPresent(className, element));
    }

    public static void waitForClassNotPresent(String className, WebElement element, int seconds) {
        waitFor(seconds)
                .withException(() -> className + " - " + addName(element, ELEMENT_HAS_CLASS))
                .until(() -> !isClassPresent(className, element));
    }

//    public static boolean isVisibleInViewport(WebComponent webComponent) {
//        return isVisibleInViewport(webComponent, webComponent.getDriver());
//    }

    public static boolean isVisibleInViewport(WebElement element, WebDriver driver) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var elem = arguments[0],                 " +
                        "  box = elem.getBoundingClientRect(),    " +
                        "  cx = box.left + box.width / 2,         " +
                        "  cy = box.top + box.height / 2,         " +
                        "  e = document.elementFromPoint(cx, cy); " +
                        "for (; e; e = e.parentElement) {         " +
                        "  if (e === elem)                        " +
                        "    return true;                         " +
                        "}                                        " +
                        "return false;                            "
                , element);
    }

    /**
     * 3 seconds Waiter for animation to stop
     *
     * @param element - who's moving
     */
    public static void waitUntilElementStoppedMoving(WebElement element) {
        waitFor(3)
                .withException("Element is still moving")
                .until(() -> elementHasStoppedMoving(element));
    }

    public static WebElement waitFor(WebElement parentElem, By by, int seconds) {
        return waitFor(seconds)
                .withException("WebElement with selector [" + by + "] did not appear in " + seconds + "s")
                .untilGot(() -> parentElem.findElement(by));
    }

    public static WebElement waitFor(Supplier<WebElement> supplier, int seconds) {
        return waitFor(seconds)
                .withException("WebElement with did not appear in " + seconds + "s")
                .untilGot(supplier::get);
    }

    public static WebElement waitFor(WebDriver driver, By by, int seconds) {
        return waitFor(seconds)
                .withException("WebElement with selector [" + by + "] did not appear in " + seconds + "s")
                .untilGot(() -> driver.findElement(by));
    }

//    public static <T extends HtmlElement> void waitForAll(int seconds, List<T> elements) {
//        waitForAll(seconds, elements.toArray(new HtmlElement[0]));
//    }

//    public static void waitForAll(int seconds, WebElement... elements) {
//        Try.of(() -> {
//            waitFor(seconds).until(() -> isAllDisplayed(elements));
//            return true;
//        }).andFinally(() -> {
//                    SoftAssert softAssert = new SoftAssert();
//                    Arrays.asList(elements)
//                            .stream()
//                            .forEach(it -> {
//                                log.info("Check if [" + (it instanceof Named ? ((Named) it) : it) + "] is displayed");
//                                softAssert.assertTrue(isDisplayed(it), it + " is not displayed");
//                            });
//                    softAssert.assertAll();
//                }
//        );
//    }

    // endregion Waiters

    // region Actions
    public static Option<WebElement> getIfDisplayed(WebElement parentElem, By by) {
        try {
            WebElement elem = parentElem.findElement(by);
            if (elem.isDisplayed()) {
                return Option.of(elem);
            }
        } catch (Exception e) {
            // log.debug("Element '" + by + "' is NOT displayed");
        }

        return Option.none();
    }

    public static Option<WebElement> getIfDisplayed(WebDriver driver, By by) {
        try {
            WebElement elem = driver.findElement(by);
            if (elem.isDisplayed()) {
                return Option.of(elem);
            }
        } catch (Exception e) {
            //  log.debug("Element '" + by + "' is NOT displayed");
        }
        return Option.none();
    }

    public static Color parseColor(String input) {
        Pattern c = Pattern.compile("rgba*\\( *([0-9]+), *([0-9]+), *([0-9]+),.*\\)");
        Matcher m = c.matcher(input);
        if (m.matches()) {
            return new Color(Integer.valueOf(m.group(1)),  // r
                    Integer.valueOf(m.group(2)),  // g
                    Integer.valueOf(m.group(3))); // b
        }
        return null;
    }

    public static void assertNot(String errorMessage, Callable callable) {
        try {
            Object value = callable.call().toString();
            throw new Exception(errorMessage + ": Got value [" + value + "]");
        } catch (AssertionError | TimeoutException | NoSuchElementException | InvalidElementStateException e) {
            //    log.info("Conditions were not met: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void dragAndDropElements(WebDriver driver, WebElement from, WebElement to) {
        new Actions(driver).clickAndHold(from)
                .moveToElement(to)
                .release(to)
                .build().perform();
    }

    public static void dragAndDropElement(WebDriver driver, WebElement element, int offsetX, int offsetY) {
        new Actions(driver).clickAndHold(element)
                .moveByOffset(offsetX, offsetY)
                .release(element)
                .build().perform();
    }

    public static void dragAndDropElementMobile(WebDriver driver, WebElement element, int offsetX, int offsetY) {
        new Actions(driver).moveToElement(element)
                .dragAndDropBy(element, offsetX, offsetY)
                .build().perform();
    }

    public static boolean selectTextIgnoreCase(WebElement selectElement, String textToSelect) {
        WebElement option = new Select(selectElement).getOptions().stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(textToSelect))
                .findFirst().orElse(null);
        if (option != null) {
            option.click();
            return true;
        }
        return false;
    }

    public static boolean selectByValue(WebElement selectElement, String value) {
        waitForVisible(selectElement);

        return Try.of(() -> {
            Select select = new Select(selectElement);
            waitFor(20)
                    .withError("No options to select")
                    .until(() -> select.getOptions().size() > 0);

            select.selectByValue(value);
            return true;
        }).getOrElse(false);
    }

//    public static String selectAnyValue(WebElement selectElement) {
//        waitForVisible(selectElement);
//
//        Select select = new Select(selectElement);
//        waitFor(20)
//                .withError("No options to select")
//                .until(() -> select.getOptions().size() > 0);
//
//        List<WebElement> options = select.getOptions();
//        log.debug("Options to select:\n" + options.stream().map(WebElement::getText).collect(toList()));
//        String valueToSelect = options.get(new Random().nextInt(options.size())).getText();
//        select.selectByVisibleText(valueToSelect);
//        return valueToSelect;
//    }

    public static String selectByIndex(WebElement selectElement, int index) {
        waitForVisible(selectElement);

        Select select = new Select(selectElement);
        waitFor(20)
                .withError("No options to select")
                .until(() -> select.getOptions().size() > 0);

        String valueToSelect = select.getOptions().get(index).getText();
        select.selectByIndex(index);
        return valueToSelect;
    }

    public static void mouseOverElement(WebDriver driver, WebElement element) {
        //Mouseover the top left corner
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
    }

    public static void mouseOverElementCenter(WebDriver driver, WebElement element) {
        //Mouseover from the center of the element
        Actions actions = new Actions(driver);
        actions.moveToElement(element, 0, 0);
        actions.perform();
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollIntoView();", element);
    }

    public static String getTextUsingJquery(WebDriver driver, String cssSelector) {
        return ((JavascriptExecutor) driver).executeScript("return $(\"" + cssSelector + "\").text()").toString();
    }

    public static void waitForAjaxCompleted(WebDriver driver, int seconds) {
        waitFor(seconds).until(() -> (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0"));
    }

    public static String serialize(WebDriver driver, WebElement element) {
        return ((JavascriptExecutor) driver).executeScript("return arguments[0].outerHTML;", element).toString();
    }

    /**
     * The blur event occurs when an element loses focus. The blur() method triggers the blur event, or attaches a function to run when a blur event occurs.
     */
    public static void blur(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("return arguments[0].blur();", element);
    }

    // endregion Actions

    public static FeatureMatcher<WebElement, String> text(org.hamcrest.Matcher<String> matcher) {
        return new FeatureMatcher<WebElement, String>(matcher, "text", "text") {
            @Override
            protected String featureValueOf(WebElement actual) {
                return actual.getText();
            }
        };
    }

    public static FeatureMatcher<WebElement, String> attribute(String attribute, org.hamcrest.Matcher<String> matcher) {
        return new FeatureMatcher<WebElement, String>(matcher, "attribute", "attribute") {
            @Override
            protected String featureValueOf(WebElement actual) {
                return actual.getAttribute(attribute);
            }
        };
    }

    public static FeatureMatcher<WebElement, String> cssValue(String propertyName, org.hamcrest.Matcher<String> matcher) {
        return new FeatureMatcher<WebElement, String>(matcher, "cssValue", "cssValue") {
            @Override
            protected String featureValueOf(WebElement actual) {
                return actual.getCssValue(propertyName);
            }
        };
    }

    public static FeatureMatcher<WebElement, List<String>> classValue(org.hamcrest.Matcher<Iterable<? super String>> matcher) {
        return new FeatureMatcher<WebElement, List<String>>(matcher, "classValue", "classValue") {
            @Override
            protected List<String> featureValueOf(WebElement actual) {
                return Arrays.asList(actual.getAttribute("class").split(" "));
            }
        };
    }

    private static Await waitFor(int seconds) {
        return Await.waitFor(seconds)
                .ignoreException(StaleElementReferenceException.class)
                .ignoreException(NoSuchElementException.class);
    }
}