package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.DefaultStopwatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.sasu.lib.elapsedtime.estimator.Estimator.MAX_DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Tests DefaultEstimator
 */
class DefaultEstimatorTest {

    @Test
    void getRemainingTimeTest(){
        final MockStopwatch mockStopwatch = new MockStopwatch();
        DefaultEstimator<MockStopwatch> defaultEstimator = new DefaultEstimator<>(mockStopwatch);
        final long totalWorkUnits = 10;

        defaultEstimator.initAndStart(totalWorkUnits);

        Duration zero = Duration.of(0, ChronoUnit.MILLIS);
        Assertions.assertEquals(zero, defaultEstimator.getElapsedTime());
        Assertions.assertEquals(MAX_DURATION, defaultEstimator.getRemainingTime());

        long timeFactor = 3; //simply incrementing by one does not catch all bugs

        for(int i = 1; i < totalWorkUnits; i++) {
            mockStopwatch.increment(timeFactor);
            defaultEstimator.completeWorkUnits(1);

            Duration expectedElapsedTime = Duration.of(i * timeFactor, ChronoUnit.MILLIS);
            Assertions.assertEquals(expectedElapsedTime, defaultEstimator.getElapsedTime());

            System.out.println("Elapsed time is " + defaultEstimator.getElapsedTime() +
                    " after completing " + defaultEstimator.getCompletedWorkUnits() + " work units.");

            long expectedRemainingTimeInMs = totalWorkUnits - i;
            Duration expectedRemainingTime = Duration.of(expectedRemainingTimeInMs * timeFactor, ChronoUnit.MILLIS);
            Duration remainingTime = defaultEstimator.getRemainingTime();
            Assertions.assertEquals(expectedRemainingTime, remainingTime);
        }

        mockStopwatch.increment(timeFactor);
        defaultEstimator.completeWorkUnits(1);

        Assertions.assertEquals(Duration.of(totalWorkUnits * timeFactor, ChronoUnit.MILLIS), defaultEstimator.getElapsedTime());
        Assertions.assertEquals(Duration.ZERO , defaultEstimator.getRemainingTime());
    }
    
    /**
     * Tests method initAndStart(long remainingWorkUnitsArg, Stopwatch stopwatch)
     * (the other initAndStartTest is implicitly tested in method getRemainingTimeTest)
     */
    @Test
    void initAndStartTest() {
        final MockStopwatch mockStopwatch = new MockStopwatch();
        DefaultEstimator<MockStopwatch> defaultEstimator = new DefaultEstimator<>(mockStopwatch);
        final long totalWorkUnits = 10;

        defaultEstimator.initAndStart(totalWorkUnits);
        MockStopwatch stopwatch = defaultEstimator.getStopwatch();

        assertEquals(mockStopwatch, stopwatch);
    }

    @Test
    void createInstanceAndStart() {
        DefaultEstimator<DefaultStopwatch> defaultEstimator = DefaultEstimator.createInstanceAndStart(1);
        assertTrue(defaultEstimator.isRunning());
    }

    @Test
    void initAndStart() {
        DefaultEstimator<MockStopwatch> defaultEstimator = new DefaultEstimator<>(new MockStopwatch());
        defaultEstimator.initAndStart(11);

        assertEquals(11, defaultEstimator.getRemainingWorkUnits());
        assertEquals(0, defaultEstimator.getCompletedWorkUnits());
        assertEquals(11, defaultEstimator.getTotalWorkUnits());
        assertTrue(defaultEstimator.isRunning());
    }

    @Test
    void testInitAndStart() {
        MockStopwatch mockStopwatch = new MockStopwatch();
        DefaultEstimator<MockStopwatch> defaultEstimator = new DefaultEstimator<>(mockStopwatch);

        MockStopwatch defaultEstimatorStopwatch = defaultEstimator.getStopwatch();
        assertEquals(mockStopwatch, defaultEstimatorStopwatch);

        defaultEstimator.initAndStart(13);

        assertEquals(13, defaultEstimator.getRemainingWorkUnits());
        assertEquals(0, defaultEstimator.getCompletedWorkUnits());
        assertEquals(13, defaultEstimator.getTotalWorkUnits());
        assertTrue(defaultEstimator.isRunning());
    }

}
