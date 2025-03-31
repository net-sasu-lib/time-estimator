package net.sasu.lib.elapsedtime.estimator;

import java.time.Duration;

import net.sasu.lib.time.elapsedTime.ElapsedTime;
import net.sasu.lib.time.stopwatch.StopwatchInterface;
/**
 * An interface defining the contract for time estimation implementations.
 * Estimators track work progress and provide estimates for remaining time
 * based on elapsed time and completed work units.
 *
 * <p>Implementations can be optimized for specific types of work patterns
 * or estimation requirements. The interface provides methods for managing
 * the estimation lifecycle, tracking progress, and retrieving time information.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@literal
 * Estimator<MyEstimator, MyStopwatch> estimator = new MyEstimator();
 * estimator.initAndStart(100); // Initialize with 100 work units
 * estimator.completeWorkUnits(25); // Complete 25 units
 * Duration remainingTime = estimator.getRemainingTime();
 * }
 * </pre>
 *
 * @param <EstimatorType> The specific type of estimator implementing this interface
 * @param <StopwatchType> The type of stopwatch used for time tracking
 * @author Sasu
 */
public interface Estimator<
        EstimatorType extends Estimator<EstimatorType, StopwatchType>,
        StopwatchType extends StopwatchInterface<StopwatchType>
        >
        extends StopwatchInterface<StopwatchType> {

    /**
     * The maximum duration that can be represented.
     * Defined as the maximum number of seconds with maximum nanoseconds.
     */
    Duration MAX_DURATION = Duration.ofSeconds(
            Long.MAX_VALUE,   // Max allowed seconds
            999999999L        // Max nanoseconds less than a second
    );

    /**
     * String representation of infinity, used when remaining time cannot be calculated.
     */
    String INFINITY_STRING = "∞";

    /**
     * Initializes and starts the estimator with the specified number of work units.
     * This method should be called before any other operations.
     *
     * @param remainingWorkUnits The total number of work units to be completed
     * @return The initialized estimator instance
     * @throws IllegalStateException if the estimator has already been started
     * @throws IllegalArgumentException if remainingWorkUnits is not positive
     */
    EstimatorType initAndStart(long remainingWorkUnits);

    /**
     * Starts the time tracking for the estimation process.
     *
     * @return The associated stopwatch instance
     * @throws IllegalStateException if the estimator is already started or not properly initialized
     */
    StopwatchType start();

    /**
     * Records the completion of work units and updates the progress tracking.
     *
     * @param workUnitsCompleted The number of work units that were completed
     * @throws IllegalArgumentException if workUnitsCompleted is negative
     * @throws IllegalStateException if more work units are completed than remaining
     */
    void completeWorkUnits(long workUnitsCompleted);

    /**
     * Returns the time elapsed since the estimator was started.
     *
     * @return The elapsed time as an ElapsedTime object
     */
    ElapsedTime getElapsedTime();

    /**
     * Calculates and returns the estimated remaining time based on
     * progress and elapsed time.
     *
     * @return The estimated remaining time as a Duration
     */
    Duration getRemainingTime();

    /**
     * Returns the estimated remaining time in a human-readable format.
     *
     * @return A formatted string representing the remaining time
     */
    String getRemainingTimeAsString();

    /**
     * Returns the elapsed time in a human-readable format.
     *
     * @return A formatted string representing the elapsed time
     */
    String getElapsedTimeAsString();

    /**
     * Stops the time tracking for the estimation process.
     *
     * @return The associated stopwatch instance
     * @throws IllegalStateException if the estimator is not started
     */
    StopwatchType stop();
}