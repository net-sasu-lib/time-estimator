package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.Stopwatch;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

public class PreciseEstimator extends DefaultEstimator<Stopwatch> {

    private final Queue<Long> recentDurations;
    private final int windowSize;
    private Instant lastCompletionTime;

    /**
     * Creates a new PreciseEstimator with default window size of 3
     */
    public PreciseEstimator() {
        this(3);
    }

    /**
     * Creates a new PreciseEstimator with specified window size
     *
     * @param windowSize The number of recent measurements to keep for averaging
     * @throws IllegalArgumentException if windowSize is less than 1
     */
    public PreciseEstimator(int windowSize) {
        super(new Stopwatch());
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least 1");
        }
        this.windowSize = windowSize;
        this.recentDurations = new LinkedList<>();
    }

    /**
     * Creates a new PreciseEstimator with specified window size and total work units
     *
     * @param windowSize The number of recent measurements to keep for averaging
     * @param totalWorkUnits The total number of work units to complete
     * @throws IllegalArgumentException if windowSize is less than 1 or totalWorkUnits is not positive
     */
    public PreciseEstimator(int windowSize, long totalWorkUnits) {
        super(new Stopwatch(), totalWorkUnits);
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size must be at least 1");
        }
        this.windowSize = windowSize;
        this.recentDurations = new LinkedList<>();
    }

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

    @Override
    public Duration getRemainingTime() {
        if (recentDurations.isEmpty() || getRemainingWorkUnits() == 0) {
            return Duration.ZERO;
        }

        // Calculate average duration per work unit
        double averageDurationNanos = recentDurations.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        // Calculate total remaining time
        long remainingNanos = (long) (averageDurationNanos * getRemainingWorkUnits());
        return Duration.ofNanos(remainingNanos);
    }

    /**
     * Returns the current window size used for averaging
     *
     * @return the number of measurements used in the moving average
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Returns the number of duration measurements currently stored
     *
     * @return the current number of measurements in the moving average window
     */
    public int getCurrentMeasurementCount() {
        return recentDurations.size();
    }
}