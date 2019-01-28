package com.onlinerautomation.utils;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.awaitility.core.*;
import org.awaitility.pollinterval.FixedPollInterval;
import org.awaitility.pollinterval.PollInterval;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.hamcrest.Matchers.isA;
import static org.testng.Assert.fail;

/**
 * Customized implementation of Awaitility library
 *
 * @see <a href="https://github.com/awaitility">Awaitility</a>
 */
public class Await {
    private ConditionFactory conditionFactory;
    private String errorMessage;
    private Callable<String> callableErrorMessage;
    private String exceptionMessage;
    private Callable<String> callableExceptionMessage;

    public Await() {
        conditionFactory = Awaitility.await()
                .ignoreException(StaleElementReferenceException.class)
                .ignoreException(NoSuchElementException.class);
    }

    /**
     * Create Await with auto pollInterval logic
     *
     * @param timeoutInMs is milliseconds
     */
    public Await(long timeoutInMs) {
        long pollInterval;
        if (timeoutInMs >= 10000) {
            pollInterval = 1000;
        } else if (timeoutInMs > 3000) {
            pollInterval = 500;
        } else if (timeoutInMs > 1000) {
            pollInterval = 300;
        } else {
            pollInterval = 200;
        }
        conditionFactory = Awaitility.await().timeout(new Duration(timeoutInMs, MILLISECONDS))
                .pollInterval(new FixedPollInterval(new Duration(pollInterval, MILLISECONDS)))
                .ignoreException(StaleElementReferenceException.class)
                .ignoreException(NoSuchElementException.class);
    }

    /**
     * Create Await instance with atMost wait time in seconds
     *
     * @param seconds timeout in seconds
     * @return the Await instance
     */
    public static Await waitFor(int seconds) {
        if (seconds > 1000 || seconds == 0) {
            throw new RuntimeException("Waiter timeout set to " + seconds + "s. Is it a typo?");
        }
        return new Await(seconds * 1000);
    }

    /**
     * Instruct Await that in case of waiting fail create TestNG fail (java.lang.AssertionError) with such static message
     *
     * @param failMessage failure message
     * @return the Await instance
     */
    public Await withError(String failMessage) {
        forgetErrorMessages();
        errorMessage = failMessage;
        return this;
    }

    /**
     * Instruct Await that in case of waiting fail create TestNG fail (java.lang.AssertionError) dynamically by execution provided callable
     * if String errorMessage is empty
     *
     * @param callableFailMessage dynamically generated failure message, will be called if waiters condition fails
     * @return the Await instance
     */
    public Await withError(Callable<String> callableFailMessage) {
        forgetErrorMessages();
        callableErrorMessage = callableFailMessage;
        return this;
    }

    /**
     * Instruct Await that in case of waiting fail throw org.awaitility.core.ConditionTimeoutException
     * with custom message
     *
     * @param customExceptionMessage custom message which is added to ConditionTimeoutException in case of waiter failure
     * @return the Await instance
     */
    public Await withException(String customExceptionMessage) {
        forgetErrorMessages();
        exceptionMessage = customExceptionMessage;
        return this;
    }

    /**
     * Instruct Await that in case of waiting fail throw org.awaitility.core.ConditionTimeoutException
     * with custom message
     *
     * @param callableCustomExceptionMessage callable which return custom message which is called in case waiter fails
     * @return the Await instance
     */
    public Await withException(Callable<String> callableCustomExceptionMessage) {
        forgetErrorMessages();
        callableExceptionMessage = callableCustomExceptionMessage;
        return this;
    }

    /**
     * Condition has to be evaluated not earlier than <code>timeout</code> before throwing a timeout exception.
     *
     * @param timeout the timeout
     * @return the Await instance
     */
    public Await atLeast(Duration timeout) {
        conditionFactory = conditionFactory.atLeast(timeout);
        return this;
    }

    /**
     * Condition has to be evaluated not earlier than <code>timeout</code> before throwing a timeout exception.
     *
     * @param timeout timeout amount
     * @param unit    timeout units
     * @return the Await instance
     */
    public Await atLeast(long timeout, TimeUnit unit) {
        conditionFactory = conditionFactory.atLeast(timeout, unit);
        return this;
    }

    /**
     * Condition has to be evaluated not earlier than <code>timeInMs/code> before throwing a timeout exception.
     *
     * @param timeInMs the timeout
     * @return the Await instance
     */
    public Await atLeast(int timeInMs) {
        return atLeast(new Duration(timeInMs, MILLISECONDS));
    }

    /**
     * Condition has to be evaluated not earlier than <code>timeInMs</code> before throwing a timeout exception.
     *
     * @param timeInMs the timeout
     * @return the Await instance
     */
    public Await atLeast(long timeInMs) {
        return atLeast(new Duration(timeInMs, MILLISECONDS));
    }

    /**
     * Await at most <code>timeout</code> before failing waiting.
     *
     * @param timeout the timeout
     * @return the Await instance
     */
    public Await atMost(Duration timeout) {
        conditionFactory = conditionFactory.atMost(timeout);
        return this;
    }

    /**
     * Await at most <code>timeout</code> before failing waiting.
     *
     * @param timeout timeout amount
     * @param unit    timeout units
     * @return the Await instance
     */
    public Await atMost(long timeout, TimeUnit unit) {
        conditionFactory = conditionFactory.atMost(timeout, unit);
        return this;
    }

    /**
     * Await at most <code>timeInMs</code> milliseconds before failing waiting.
     *
     * @param timeInMs the timeout
     * @return the Await instance
     */
    public Await atMost(int timeInMs) {
        return atMost(new Duration(timeInMs, MILLISECONDS));
    }

    /**
     * Await at most <code>timeInMs</code> milliseconds before failing waiting.
     *
     * @param timeInMs the timeout
     * @return the Await instance
     */
    public Await atMost(long timeInMs) {
        return atMost(new Duration(timeInMs, MILLISECONDS));
    }

    /**
     * Specifies the duration window which has to be satisfied during operation execution. In case operation is executed
     * before <code>atLeast</code> or after <code>atMost</code> timeout exception is thrown.
     *
     * @param atLeast lower part of execution window
     * @param atMost  upper part of execution window
     * @return the Await instance
     */
    public Await between(Duration atLeast, Duration atMost) {
        conditionFactory = conditionFactory.between(atLeast, atMost);
        return this;
    }

    /**
     * Specifies the duration window which has to be satisfied during operation execution. In case operation is executed
     * before <code>atLeastDuration</code> or after <code>atMostDuration</code> timeout exception is thrown.
     *
     * @param atLeastDuration lower part of execution window
     * @param atMostDuration  upper part of execution window
     * @return the Await instance
     */
    public Await between(long atLeastDuration, TimeUnit atLeastTimeUnit, long atMostDuration, TimeUnit atMostTimeUnit) {
        conditionFactory = conditionFactory.between(atLeastDuration, atLeastTimeUnit, atMostDuration, atMostTimeUnit);
        return this;
    }

    /**
     * Specify the delay that will be used before Await starts polling for
     * the result the first time. If you don't specify a poll delay explicitly
     * it'll be 0.
     *
     * @param pollDelay the poll delay
     * @return the Await instance
     */
    public Await pollDelay(Duration pollDelay) {
        conditionFactory = conditionFactory.pollDelay(pollDelay);
        return this;
    }

    /**
     * Specify the polling interval Await will use for this await
     * statement. This means the frequency in which the condition is checked for
     * completion.
     *
     * @param pollInterval the poll interval
     * @return the Await instance
     */
    public Await pollInterval(PollInterval pollInterval) {
        conditionFactory = conditionFactory.pollInterval(pollInterval);
        return this;
    }

    /**
     * Specify the polling interval Await will use for this await
     * statement. This means the frequency in which the condition is checked for
     * completion.
     *
     * @param pollInterval the poll interval in milliseconds
     * @return the Await instance
     */
    public Await pollInterval(long pollInterval) {
        return pollInterval(pollInterval, MILLISECONDS);
    }

    /**
     * Specify the polling interval Awaitility will use for this await
     * statement. This means the frequency in which the condition is checked for
     * completion.
     *
     * @param pollInterval the poll interval
     * @param unit         the unit
     * @return the Await instance
     * @see FixedPollInterval
     */
    public Await pollInterval(long pollInterval, TimeUnit unit) {
        conditionFactory = conditionFactory.pollInterval(pollInterval, unit);
        return this;
    }

    /**
     * Instruct Await to ignore exceptions instance of the supplied exceptionType type.
     * Exceptions will be treated as evaluating to <code>false</code>.
     * This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     * <p/>
     * <p>If you want to ignore a specific exceptionType then use {@link #ignoreException(Class)}</p>
     *
     * @param exceptionType The exception type (hierarchy) to ignore
     * @return the Await instance
     */
    public Await ignoreExceptionsInstanceOf(Class<? extends Exception> exceptionType) {
        conditionFactory = conditionFactory.ignoreExceptionsInstanceOf(exceptionType);
        return this;
    }

    /**
     * Instruct Await to ignore a specific exception and <i>no</i> subclasses of this exception.
     * Exceptions will be treated as evaluating to <code>false</code>.
     * This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     * <p>If you want to ignore a subtypes of this exception then use {@link #ignoreExceptionsInstanceOf(Class)}} </p>
     *
     * @param exceptionType The exception type to ignore
     * @return the Await instance
     */
    public Await ignoreException(Class<? extends Exception> exceptionType) {
        conditionFactory = conditionFactory.ignoreException(exceptionType);
        return this;
    }

    /**
     * Instruct Await to ignore <i>all</i> exceptions that occur during evaluation.
     * Exceptions will be treated as evaluating to
     * <code>false</code>. This is useful in situations where the evaluated
     * conditions may temporarily throw exceptions.
     *
     * @return the Await instance
     */
    public Await ignoreExceptions() {
        conditionFactory = conditionFactory.ignoreExceptions();
        return this;
    }

    /**
     * Instruct Awaitility to not ignore any exceptions that occur during evaluation.
     * This is only useful if Awaitility is configured to ignore exceptions by default but you want to
     * have a different behavior for a single test case.
     *
     * @return the Await instance.
     */
    public Await ignoreNoExceptions() {
        return ignoreExceptionsMatching(e -> false);
    }

    /**
     * Instruct Await to ignore exceptions that occur during evaluation and matches the supplied Hamcrest matcher.
     * Exceptions will be treated as evaluating to
     * <code>false</code>. This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     *
     * @return the Await instance
     */
    public Await ignoreExceptionsMatching(Matcher<? super Throwable> matcher) {
        conditionFactory = conditionFactory.ignoreExceptionsMatching(matcher);
        return this;
    }

    /**
     * Instruct Await to ignore exceptions that occur during evaluation and matches the supplied <code>predicate</code>.
     * Exceptions will be treated as evaluating to
     * <code>false</code>. This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     *
     * @return the Await instance.
     */
    public Await ignoreExceptionsMatching(Predicate<? super Throwable> predicate) {
        conditionFactory = conditionFactory.ignoreExceptionsMatching(predicate);
        return this;
    }

    /**
     * Await for an asynchronous operation. This method returns the same
     * {@link Await} instance and is used only to get a more
     * fluent-like syntax.
     *
     * @return the Await instance
     */
/*    public Await await() {
        return this;
    }*/

    /**
     * A method to increase the readability of the Awaitility DSL. It simply
     * returns the same Await instance.
     *
     * @return the Await instance
     */
    public Await and() {
        return this;
    }

    /**
     * A method to increase the readability of the Awaitility DSL. It simply
     * returns the same Await instance.
     *
     * @return the Await instance
     */
    public Await with() {
        return this;
    }

    /**
     * A method to increase the readability of the Awaitility DSL. It simply
     * returns the same Await instance.
     *
     * @return the Await instance
     */
    public Await then() {
        return this;
    }

    /**
     * A method to increase the readability of the Awaitility DSL. It simply
     * returns the same Await instance.
     *
     * @return the Await instance
     */
    public Await given() {
        return this;
    }

    /**
     * Don't catch uncaught exceptions in other threads. This will <i>not</i>
     * make the await statement fail if exceptions occur in other threads.
     *
     * @return the Await instance
     */
    public Await dontCatchUncaughtExceptions() {
        conditionFactory = conditionFactory.dontCatchUncaughtExceptions();
        return this;
    }

    /**
     * Handle condition evaluation results each time evaluation of a condition occurs. Works only with a Hamcrest matcher-based condition.
     *
     * @param conditionEvaluationListener the condition evaluation listener
     * @return the Await instance
     */
    public Await conditionEvaluationListener(ConditionEvaluationListener conditionEvaluationListener) {
        conditionFactory = conditionFactory.conditionEvaluationListener(conditionEvaluationListener);
        return this;
    }

    /**
     * Specify the condition that must be met when waiting for a method call.
     * E.g.
     * <p>&nbsp;</p>
     * <pre>
     * await().untilCall(to(orderService).size(), is(greaterThan(2)));
     * </pre>
     *
     * @param <T>     the generic type
     * @param ignore  the return value of the method call
     * @param matcher The condition that must be met when
     * @return a T object.
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period and withError is not set
     */
    public <T> T untilCall(T ignore, Matcher<? super T> matcher) {
        try {
            return conditionFactory.untilCall(ignore, matcher);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
            return null;
        }
    }

    /**
     * Await until a {@link Callable} supplies a value matching the specified
     *
     * @param supplier the supplier that is responsible for getting the value that
     *                 should be matched.
     * @param matcher  the matcher The hamcrest matcher that checks whether the
     *                 condition is fulfilled.
     * @return a T object.
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period and withError is not set
     **/
    public <T> T until(Callable<T> supplier, Matcher<? super T> matcher) {
        try {
            return conditionFactory.until(supplier, matcher);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
            return null;
        }
    }

    /**
     * Wait until the given supplier matches the supplied predicate. For example:
     *
     * <pre>
     * await().until(myRepository::count, cnt -> cnt == 2);
     * </pre>
     *
     * @param supplier  The supplier that returns the object that will be evaluated by the predicate.
     * @param predicate The predicate that must match
     * @param <T>       the generic type
     * @return a T object.
     * @since 3.1.1
     */
    public <T> T until(final Callable<T> supplier, final Predicate<? super T> predicate) {
        return until(supplier, new TypeSafeMatcher<T>() {
            @Override
            protected void describeMismatchSafely(T item, Description description) {
                description.appendText("it returned <false> for input of ").appendValue(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("the predicate to return <true>");
            }

            @Override
            protected boolean matchesSafely(T item) {
                return predicate.matches(item);
            }
        });
    }

    /**
     * Await until a {@link Callable} returns <code>true</code>.
     *
     * @param conditionEvaluator the condition evaluator
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period and withError is not set
     */
    public void until(Callable<Boolean> conditionEvaluator) {
        try {
            conditionFactory.until(conditionEvaluator);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
        }
    }

    /**
     * Await until a {@link Runnable} supplier execution passes (ends without throwing an exception). E.g. with Java 8:
     * <p>&nbsp;</p>
     * <pre>
     * await().untilAsserted(() -&gt; Assertions.assertThat(personRepository.size()).isEqualTo(6));
     * </pre>
     * or
     * <pre>
     * await().untilAsserted(() -&gt; assertEquals(6, personRepository.size()));
     * </pre>
     * <p>&nbsp;</p>
     * This method is intended to benefit from lambda expressions introduced in Java 8. It allows to use standard AssertJ/FEST Assert assertions
     * (by the way also standard JUnit/TestNG assertions) to test asynchronous calls and systems.
     * <p>&nbsp;</p>
     * {@link AssertionError} instances thrown by the supplier are treated as an assertion failure and proper error message is propagated on timeout.
     * Other exceptions are rethrown immediately as an execution errors.
     * <p>&nbsp;</p>
     * Why technically it is completely valid to use plain Runnable class in Java 7 code, the resulting expression is very verbose and can decrease
     * the readability of the test case, e.g.
     * <p>&nbsp;</p>
     * <pre>
     * await().untilAsserted(new Runnable() {
     *     public void run() {
     *         Assertions.assertThat(personRepository.size()).isEqualTo(6);
     *     }
     * });
     * </pre>
     * <p>&nbsp;</p>
     * <b>NOTE:</b><br>
     * Be <i>VERY</i> careful so that you're not using this method incorrectly in languages (like Kotlin and Groovy) that doesn't
     * disambiguate between a {@link ThrowingRunnable} that doesn't return anything (void) and {@link Callable} that returns a value.
     * For example in Kotlin you can do like this:
     * <p>&nbsp;</p>
     * <pre>
     * await().untilAsserted { true == false }
     * </pre>
     * and the compiler won't complain with an error (as is the case in Java). If you were to execute this test in Kotlin it'll pass!
     *
     * @param assertion the supplier that is responsible for executing the assertion and throwing AssertionError on failure.
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period.
     * @since 1.6.0
     */
    public void untilAsserted(final ThrowingRunnable assertion) {
        try {
            conditionFactory.untilAsserted(assertion);
        } catch (ConditionTimeoutException ex) {
            processMessages(ex);
            throwAssertionError("", ex);
        }
    }

    /**
     * Await until a Atomic boolean becomes true.
     *
     * @param atomic the atomic variable
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period.
     */
    public void untilTrue(final AtomicBoolean atomic) {
        try {
            conditionFactory.untilTrue(atomic);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
        }
    }

    /**
     * Await until a Atomic boolean becomes false.
     *
     * @param atomic the atomic variable
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period.
     */
    public void untilFalse(final AtomicBoolean atomic) {
        try {
            conditionFactory.untilFalse(atomic);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
        }
    }

    /**
     * clean all messages, so that if you wish you could re-use your Await object
     */
    private void forgetErrorMessages() {
        errorMessage = null;
        callableErrorMessage = null;
        exceptionMessage = null;
        callableExceptionMessage = null;
    }

    private void conditionTimeoutExceptionHandling(ConditionTimeoutException ex) {
        processMessages(ex);
        throw ex;
    }

    private void processMessages(ConditionTimeoutException ex) {
        if (errorMessage != null) {
            throwAssertionError(errorMessage, ex);
        } else if (callableErrorMessage != null) {
            throwAssertionError(callableErrorMessage, ex);
        } else if (exceptionMessage != null) {
            throw getTimeoutException(exceptionMessage, ex);
        } else if (callableExceptionMessage != null) {
            throw getTimeoutException(callableExceptionMessage, ex);
        }
    }

    private ConditionTimeoutException getTimeoutException(String message, ConditionTimeoutException ex) {
        ConditionTimeoutException toThrow = new ConditionTimeoutException(message + "\n" + ex.getMessage());
        toThrow.setStackTrace(ex.getStackTrace());
        forgetErrorMessages();

        return toThrow;
    }

    private ConditionTimeoutException getTimeoutException(Callable<String> message, ConditionTimeoutException ex) {
        try {
            return getTimeoutException(message.call(), ex);
        } catch (Exception e) {
            forgetErrorMessages();
            throw new RuntimeException(e.getMessage() + ";\nCondition failed: " + ex.getMessage(), e.getCause());
        }
    }

    private void throwAssertionError(String message, ConditionTimeoutException ex) {
        String error = (isBlank(message) ? "" : message + ";\n") + ex.getMessage();
        forgetErrorMessages();
        fail(error);
    }

    private void throwAssertionError(Callable<String> message, ConditionTimeoutException ex) {
        try {
            throwAssertionError(message.call(), ex);
        } catch (Exception e) {
            forgetErrorMessages();
            throw new RuntimeException(e.getMessage() + ";\nCondition failed: " + ex.getMessage(), e.getCause());
        }
    }


    //DON'T use this in your tests!!, this is for internal use in ElementsUtil only!
    WebElement untilGot(Callable<WebElement> callable) {
        try {
            return conditionFactory.until(callable, isA(WebElement.class));
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
            return null;
        }
    }
}