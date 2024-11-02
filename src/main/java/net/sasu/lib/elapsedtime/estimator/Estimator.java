package net.sasu.lib.elapsedtime.estimator;

import java.time.Duration;

import net.sasu.lib.time.stopwatch.Stopwatch;

/**
 * Estimator performing the concrete estimation, either general or optimized
 * for certain types of inputs or desired outputs.
 * 
 * @author Sasu
 *
 * @param <EstimatorType> Estimator type
 */
public interface Estimator<
        EstimatorType extends Estimator<EstimatorType, StopwatchType>,
        StopwatchType extends Stopwatch<StopwatchType>
        >
        extends Stopwatch<StopwatchType> {

    Duration MAX_DURATION = Duration.ofSeconds(
            Long.MAX_VALUE,   // Max allowed seconds
            999999999L        // Max nanoseconds less than a second
    );
    String INFINITY_STRING = "∞";

    /**
     * Initializes Estimator instance and returns a concrete instance.
     * 
     * @param remainingWorkUnits number of total work units
     * @return instance of Estimator
     */
    EstimatorType initAndStart(long remainingWorkUnits);

      /**
     * Starts estimator
     */
    StopwatchType start();
    
    /**
     * Completes given amount of work units.
     * 
     * @param workUnitsCompleted number of completed work units
     */
    void completeWorkUnits(long workUnitsCompleted);
    
    /**
     * @return returns elapsed time in given time units
     */
    Duration getElapsedTime();
    
    /**
     *
     * @return remaining time as Duration
     */
    Duration getRemainingTime();
    
    /**
     * @return remaining time as a human-readable string
     */
    String getRemainingTimeAsString();
    
    /**
     * @return elapsed time as a human-readable string
     */
    String getElapsedTimeAsString();
    
    /**
     * Stops estimator
     */
    StopwatchType stop();

}
