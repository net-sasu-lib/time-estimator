package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.Stopwatch;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A basic implementation of time estimation that uses a moving average approach
 * to calculate remaining time for work unit completion.
 *
 * <p>This estimator uses a "window" of recent measurements to calculate average
 * duration per work unit. The window size determines how many recent measurements
 * are kept for the calculation. This moving average approach helps to:
 * <ul>
 *   <li>Adapt to changing execution speeds</li>
 *   <li>Smooth out occasional outliers</li>
 *   <li>Provide more accurate estimates based on recent performance</li>
 * </ul>
 *
 * <p><b>Moving Average Example:</b><br>
 * With window size 3 and work unit completion times [1000ms, 1100ms, 900ms, 1050ms]:
 * <ul>
 *   <li>After 3rd measurement: average of [1000ms, 1100ms, 900ms] = 1000ms</li>
 *   <li>After 4th measurement: average of [1100ms, 900ms, 1050ms] = 1017ms</li>
 * </ul>
 * The oldest measurement is dropped when the window is full.
 *
 * @author Sasu
 */
public class BasicEstimator extends DefaultEstimator<Stopwatch> {

    private final Queue<Long> recentDurations;
    private final int windowSize;
    private Instant lastCompletionTime;

    /**
     * Creates a new BasicEstimator with default window size of 3.
     * The window size determines how many recent measurements are used
     * for calculating the moving average.
     */
    public BasicEstimator() {
        this(3);
    }

    /**
     * Creates a new BasicEstimator with specified window size.
     * A larger window size provides more stable estimates but is slower
     * to adapt to changes in execution speed.
     *
     * @param windowSize The amount of recent measurements to keep for averaging
     * @throws IllegalArgumentException if windowSize is less than 1
     */
    public BasicEstimator(int windowSize) {
        super(new Stopwatch());
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least 1");
        }
        this.windowSize = windowSize;
        this.recentDurations = new LinkedList<>();
    }

    /**
     * Creates a new BasicEstimator with specified window size and total work units.
     *
     * @param windowSize The amount of recent measurements to keep for averaging
     * @param totalWorkUnits The total amount of work units to complete
     * @throws IllegalArgumentException if windowSize is less than 1 or totalWorkUnits is not positive
     */
    public BasicEstimator(int windowSize, long totalWorkUnits) {
        super(new Stopwatch(), totalWorkUnits);
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least 1");
        }
        this.windowSize = windowSize;
        this.recentDurations = new LinkedList<>();
    }

    /**
     * Records the completion of work units and updates the moving average window.
     * The duration per work unit is calculated and added to the recent measurements,
     * maintaining the specified window size by removing older measurements if necessary.
     *
     * @param workUnitsCompleted The amount of work units that were completed
     */
    @Override
    public void completeWorkUnits(long workUnitsCompleted) {
        Instant now = getInstantSource().instant();

        if (lastCompletionTime != null) {
            long durationNanos = Duration.between(lastCompletionTime, now).toNanos();

            // Add duration per work unit
            long durationPerUnit = durationNanos / workUnitsCompleted;
            recentDurations.offer(durationPerUnit);

            // Maintain window size
            while (recentDurations.size() > windowSize) {
                recentDurations.poll();
            }
        }

        lastCompletionTime = now;
        super.completeWorkUnits(workUnitsCompleted);
    }

    /**
     * Calculates the estimated remaining time based on the moving average
     * of recent work unit completion durations.
     *
     * @return The estimated remaining time as a Duration, or Duration.ZERO if
     *         no measurements are available or no work remains
     */
    @Override
    public Duration getRemainingTime() {
        if (getRemainingWorkUnits() == 0) {
            return Duration.ZERO;
        }

        // If no measurements yet but we have work to do, use elapsed time for estimation
        if (recentDurations.isEmpty()) {
            if (getCompletedWorkUnits() == 0) {
                return MAX_DURATION;
            }
            // Calculate based on elapsed time, similar to DefaultEstimator
            long elapsedNanos = getElapsedTime().getDuration().toNanos();
            double nanosPerUnit = (double) elapsedNanos / getCompletedWorkUnits();
            return Duration.ofNanos((long) (nanosPerUnit * getRemainingWorkUnits()));
        }

        // Calculate average duration per work unit from recent measurements
        double averageDurationNanos = recentDurations.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        // Calculate total remaining time
        long remainingNanos = (long) (averageDurationNanos * getRemainingWorkUnits());
        return Duration.ofNanos(remainingNanos);
    }

    /**
     * Returns the window size used for the moving average calculation.
     * The window size determines how many recent measurements are used
     * to calculate average duration per work unit.
     *
     * @return the amount of measurements used in the moving average
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Returns the current amount of duration measurements in the moving average window.
     * This will be less than or equal to the window size, depending on how many
     * work units have been completed.
     *
     * @return the current amount of measurements in the moving average window
     */
    public int getCurrentMeasurementCount() {
        return recentDurations.size();
    }
}