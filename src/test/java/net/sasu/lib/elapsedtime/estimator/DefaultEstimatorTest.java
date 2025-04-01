package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.elapsedTime.ElapsedTime;
import net.sasu.lib.time.stopwatch.Stopwatch;
import net.sasu.lib.time.stopwatch.mock.MockStopwatch;
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
    void remainingDurationTest(){
        final MockStopwatch mockStopwatch = new MockStopwatch();
        DefaultEstimator<MockStopwatch> defaultEstimator = new DefaultEstimator<>(mockStopwatch);
        final long totalWorkUnits = 10;

        defaultEstimator.initAndStart(totalWorkUnits);

        Assertions.assertEquals(ElapsedTime.ZERO, defaultEstimator.getElapsedTime());
        Assertions.assertEquals(MAX_DURATION, defaultEstimator.remainingDuration());

        long timeFactor = 3; //simply incrementing by one does not catch all bugs

        for(int i = 1; i < totalWorkUnits; i++) {
            mockStopwatch.incrementMilliseconds(timeFactor);
            defaultEstimator.completeWorkUnits(1);

            ElapsedTime expectedElapsedTime = ElapsedTime.of(i * timeFactor, ChronoUnit.MILLIS);
            Assertions.assertEquals(expectedElapsedTime, defaultEstimator.getElapsedTime(),
                    "Error when getting remaining time on round " + i);

            /*
            System.out.println("Elapsed time is " + defaultEstimator.getElapsedTime() +
                    " after completing " + defaultEstimator.getCompletedWorkUnits() + " work units.");
            */

            long expectedRemainingTimeInMs = totalWorkUnits - i;
            Duration expectedRemainingTime = Duration.of(expectedRemainingTimeInMs * timeFactor, ChronoUnit.MILLIS);
            Duration remainingTime = defaultEstimator.remainingDuration();
            Assertions.assertEquals(expectedRemainingTime, remainingTime);
        }

        mockStopwatch.incrementMilliseconds(timeFactor);
        defaultEstimator.completeWorkUnits(1);

        Assertions.assertEquals(ElapsedTime.of(totalWorkUnits * timeFactor, ChronoUnit.MILLIS), defaultEstimator.getElapsedTime());
        Assertions.assertEquals(Duration.ZERO , defaultEstimator.remainingDuration());
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
        DefaultEstimator<Stopwatch> defaultEstimator = DefaultEstimator.createInstanceAndStart(1);
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
