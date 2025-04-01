package net.sasu.lib.elapsedtime.estimator;

import static org.junit.jupiter.api.Assertions.*;

import net.sasu.lib.time.stopwatch.Stopwatch;
import net.sasu.lib.time.stopwatch.mock.MockStopwatch;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

class BasicEstimatorTest {

    @Test
    void constructor_WithDefaultWindowSize_ShouldCreateEstimator() {
        BasicEstimator estimator = new BasicEstimator();
        assertEquals(3, estimator.getWindowSize());
        assertEquals(0, estimator.getCurrentMeasurementCount());
    }

    @Test
    void constructor_WithCustomWindowSize_ShouldCreateEstimator() {
        BasicEstimator estimator = new BasicEstimator(5);
        assertEquals(5, estimator.getWindowSize());
        assertEquals(0, estimator.getCurrentMeasurementCount());
    }

    @Test
    void constructor_WithInvalidWindowSize_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new BasicEstimator(0));
        assertThrows(IllegalArgumentException.class, () -> new BasicEstimator(-1));
    }

    @Test
    void constructor_WithWindowSizeAndTotalWork_ShouldCreateEstimator() {
        BasicEstimator estimator = new BasicEstimator(5, 100);
        assertEquals(5, estimator.getWindowSize());
        assertEquals(100, estimator.getTotalWorkUnits());
        assertEquals(0, estimator.getCurrentMeasurementCount());
    }

    @Test
    void getRemainingTime_WithNoWorkCompleted_ShouldReturnMaxDuration() {
        BasicEstimator estimator = new BasicEstimator(3, 100);
        estimator.start();
        assertEquals(Estimator.MAX_DURATION, estimator.getRemainingTime());
    }

    @Test
    void getRemainingTime_WithNoRemainingWork_ShouldReturnZero() {
        BasicEstimator estimator = new BasicEstimator(3, 10);
        estimator.start();
        estimator.completeWorkUnits(10);
        assertEquals(Duration.ZERO, estimator.getRemainingTime());
    }

    @Test
    void completeWorkUnits_ShouldUpdateMeasurements() {
        MockStopwatch mockStopwatch = new MockStopwatch();
        BasicEstimator estimator = new BasicEstimator(3, 100);
        estimator.start();

        // Simulate work completions with known durations
        Instant startTime = Instant.now();
        mockStopwatch.setCurrentTime(startTime);

        // Complete first batch
        mockStopwatch.setCurrentTime(startTime.plus(1, ChronoUnit.SECONDS));
        estimator.completeWorkUnits(20);
        assertEquals(0, estimator.getCurrentMeasurementCount()); // First completion doesn't add measurement

        // Complete second batch
        mockStopwatch.setCurrentTime(startTime.plus(2, ChronoUnit.SECONDS));
        estimator.completeWorkUnits(20);
        assertEquals(1, estimator.getCurrentMeasurementCount());

        // Complete third batch
        mockStopwatch.setCurrentTime(startTime.plus(3, ChronoUnit.SECONDS));
        estimator.completeWorkUnits(20);
        assertEquals(2, estimator.getCurrentMeasurementCount());
    }

    @Test
    void completeWorkUnits_ShouldMaintainWindowSize() {
        BasicEstimator estimator = new BasicEstimator(2, 100);
        estimator.start();

        // Complete work units multiple times
        for (int i = 0; i < 5; i++) {
            estimator.completeWorkUnits(10);
            assertTrue(estimator.getCurrentMeasurementCount() <= estimator.getWindowSize());
        }
    }

    /**
     * TODO: rewrite test using MockStopwatch, rethink Stopwatch class structure to make it testable
     *  with MockStopwatch
     * @throws InterruptedException when something gets interrupted
     */
    @Test
    void getRemainingTime_WithConstantWorkRate_ShouldProvideAccurateEstimate() throws InterruptedException {
        BasicEstimator estimator = new BasicEstimator(3, 100);

        estimator.start();

        Instant startTime = Instant.now();

        // Complete work with constant rate (1 second per 20 units)
        Thread.sleep(1000);
        estimator.completeWorkUnits(20);

        Thread.sleep(1000);
        estimator.completeWorkUnits(20);

        // After 2 seconds and 40 units complete, should estimate 3 seconds remaining for 60 units
        Duration remainingTime = estimator.getRemainingTime();
        assertTrue(remainingTime.getSeconds() >= 2 && remainingTime.getSeconds() <= 4,
                "Actual seconds: " + remainingTime.getSeconds());
    }

    @Test
    void getRemainingTime_WithChangingWorkRate_ShouldAdapt() {
        MockStopwatch mockStopwatch = new MockStopwatch();
        BasicEstimator estimator = new BasicEstimator(2, 100);
        estimator.start();

        Instant startTime = Instant.now();
        mockStopwatch.setCurrentTime(startTime);

        // First completion - slow
        mockStopwatch.setCurrentTime(startTime.plus(2, ChronoUnit.SECONDS));
        estimator.completeWorkUnits(20);

        // Second completion - faster
        mockStopwatch.setCurrentTime(startTime.plus(3, ChronoUnit.SECONDS));
        estimator.completeWorkUnits(20);

        // Should use more recent (faster) rate for estimation
        Duration remainingTime = estimator.getRemainingTime();
        assertTrue(remainingTime.getSeconds() < 6); // Should be closer to faster rate
    }
}