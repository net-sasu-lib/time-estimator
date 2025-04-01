package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.Stopwatch;
import net.sasu.lib.time.stopwatch.StopwatchInterface;
import net.sasu.lib.time.stopwatch.state.StopwatchState;

import java.time.Instant;

/**
 * A basic implementation of time estimation for processes with low variability in work unit completion times.
 * This estimator assumes that all work units take approximately the same amount of time to complete,
 * making it suitable for uniform, predictable processes.
 *
 * <p>This implementation extends BaseEstimator and provides a straightforward calculation
 * of remaining time based on the average time per work unit completed so far.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Create and start an estimator for 100 work units
 * {@literal
 * DefaultEstimator<Stopwatch> estimator = DefaultEstimator.createInstanceAndStart(100);
 *
 * // Complete some work units
 * estimator.completeWorkUnits(25);
 *
 * // Get remaining time estimate
 * Duration remainingTime = estimator.getRemainingTime();
 * }
 * </pre>
 *
 * @param <StopwatchType> The type of stopwatch used for time tracking
 * @author Sasu
 */
public class DefaultEstimator<StopwatchType extends StopwatchInterface<StopwatchType>>
        extends BaseEstimator<DefaultEstimator<StopwatchType>, StopwatchType> {

    /**
     * Constructs a new DefaultEstimator with the specified stopwatch.
     *
     * @param stopwatch The stopwatch to use for time tracking
     */
    public DefaultEstimator(StopwatchType stopwatch) {
        super(stopwatch);
    }

    /**
     * Constructs a new DefaultEstimator with the specified stopwatch and total work units.
     *
     * @param stopwatch The stopwatch to use for time tracking
     * @param totalWorkUnitsArg The total amount of work units to be completed
     */
    public DefaultEstimator(StopwatchType stopwatch, long totalWorkUnitsArg) {
        super(stopwatch, totalWorkUnitsArg, 0);
    }

    /**
     * Constructs a new DefaultEstimator with the specified stopwatch, total work units,
     * and completed work units.
     *
     * @param stopwatch The stopwatch to use for time tracking
     * @param totalWorkUnitsArg The total amount of work units to be completed
     * @param completedWorkUnitsArg The amount of work units already completed
     */
    public DefaultEstimator(StopwatchType stopwatch, long totalWorkUnitsArg, long completedWorkUnitsArg) {
        super(stopwatch, totalWorkUnitsArg, completedWorkUnitsArg);
    }

    /**
     * Creates and starts a new DefaultEstimator with a new Stopwatch instance.
     * This is a convenience factory method for quick initialization of the estimator.
     *
     * @param totalWorkUnitsArg The total amount of work units to be completed
     * @return A new, started DefaultEstimator instance
     */
    public static DefaultEstimator<Stopwatch> createInstanceAndStart(long totalWorkUnitsArg) {
        DefaultEstimator<Stopwatch> defaultEstimator = new DefaultEstimator<>(new Stopwatch(), totalWorkUnitsArg);
        defaultEstimator.start();
        return defaultEstimator;
    }

    /**
     * Returns the start time of the estimation process.
     *
     * @return The Instant when the estimator was started
     */
    @Override
    public Instant getStartTime() {
        return this.getStopwatch().getStartTime();
    }

    /**
     * Returns the stop time of the estimation process.
     *
     * @return The Instant when the estimator was stopped
     */
    @Override
    public Instant getStopTime() {
        return this.getStopwatch().getStopTime();
    }

    /**
     * Returns the current state of the estimator.
     *
     * @return The current StopwatchState of the estimator
     */
    @Override
    public StopwatchState getState() {
        return this.getStopwatch().getState();
    }
}