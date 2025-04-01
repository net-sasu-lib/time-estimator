package net.sasu.lib.elapsedtime.estimator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

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
    void remainingDuration_WithNoWorkCompleted_ShouldReturnMaxDuration() {
        BasicEstimator estimator = new BasicEstimator(3, 100);
        estimator.start();
        assertEquals(Estimator.MAX_DURATION, estimator.remainingDuration());
    }

    @Test
    void getRemainingTime_WithNoRemainingWork_ShouldReturnZero() {
        BasicEstimator estimator = new BasicEstimator(3, 10);
        estimator.start();
        estimator.completeWorkUnits(10);
        assertEquals(Duration.ZERO, estimator.remainingDuration());
    }

    @Test
    void completeWorkUnits_ShouldUpdateMeasurements() {
        BasicEstimator estimator = new BasicEstimator(3, 100);
        estimator.start();

        // Simulate work completions with known durations
        Instant startTime = Instant.now();

        // Complete first batch
        estimator.completeWorkUnits(20);
        assertEquals(0, estimator.getCurrentMeasurementCount()); // First completion doesn't add measurement

        // Complete second batch
        estimator.completeWorkUnits(20);
        assertEquals(1, estimator.getCurrentMeasurementCount());

        // Complete third batch
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
    void remainingDuration_WithConstantWorkRate_ShouldProvideAccurateEstimate() throws InterruptedException {
        BasicEstimator estimator = new BasicEstimator(3, 100);

        estimator.start();

        Instant startTime = Instant.now();

        // Complete work with constant rate (0.1 second sper 20 units)
        Thread.sleep(100);
        estimator.completeWorkUnits(20);

        Thread.sleep(100);
        estimator.completeWorkUnits(20);

        // After 2 seconds and 40 units complete, should estimate 3 seconds remaining for 60 units
        Duration remainingTime = estimator.remainingDuration();
        assertTrue(remainingTime.getNano() >= 2000000 && remainingTime.getSeconds() <= 4000000,
                "Actual seconds: " + remainingTime.getSeconds());
    }

}