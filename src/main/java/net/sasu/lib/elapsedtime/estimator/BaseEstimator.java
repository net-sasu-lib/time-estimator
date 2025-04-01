/**
 * 
 */
package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.elapsedTime.ElapsedTime;
import net.sasu.lib.time.stopwatch.StopwatchInterface;
import net.sasu.lib.time.stopwatch.state.StopwatchState;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.fraction.BigFraction;

import java.time.Duration;
import java.time.InstantSource;
import java.util.Objects;

/**
 * A base abstract class for time estimation implementations that provides common functionality
 * for tracking work progress and estimating remaining time. This class serves as a foundation
 * for specific estimator implementations.
 *
 * <p>The estimator keeps track of total work units, completed work units, and elapsed time
 * using a stopwatch. It can calculate remaining time based on work progress and elapsed time.</p>
 *
 * @param <EstimatorType> The specific type of estimator extending this base class
 * @param <StopwatchType> The type of stopwatch used for time tracking
 *
 * @author Sasu
 */
public abstract class BaseEstimator<
        EstimatorType extends BaseEstimator<EstimatorType, StopwatchType>,
        StopwatchType extends StopwatchInterface<StopwatchType>
        > implements Estimator<EstimatorType, StopwatchType> {

    private long totalWorkUnits;
    private long completedWorkUnits;

    StopwatchType stopwatch;

    /**
     * Constructs a new BaseEstimator with the specified stopwatch.
     *
     * @param stopwatch The stopwatch to use for time tracking
     */
    public BaseEstimator(StopwatchType stopwatch) {
        this(stopwatch, 0, 0);
    }

    /**
     * Constructs a new BaseEstimator with the specified stopwatch and work units.
     *
     * @param stopwatch The stopwatch to use for time tracking
     * @param totalWorkUnits The total amount of work units to be completed
     * @param completedWorkUnits The amount of work units already completed
     * @throws NullPointerException if stopwatch is null
     * @throws IllegalArgumentException if completedWorkUnits is negative or greater than totalWorkUnits
     */
    public BaseEstimator(StopwatchType stopwatch, long totalWorkUnits, long completedWorkUnits) {
        Objects.requireNonNull(stopwatch);
        if(completedWorkUnits < 0 || completedWorkUnits > totalWorkUnits){
            throw new IllegalArgumentException("completedWorkUnits must be between 0 and " + totalWorkUnits);
        }

        this.stopwatch = stopwatch;
        this.totalWorkUnits = totalWorkUnits;
        this.completedWorkUnits = completedWorkUnits;
    }

    /**
     * Records the completion of work units and updates the progress.
     *
     * @param workUnitsCompleted The amount of work units that were completed
     * @throws IllegalArgumentException if workUnitsCompleted is negative
     * @throws IllegalStateException if workUnitsCompleted is greater than remaining work units
     */
    @Override
    public void completeWorkUnits(long workUnitsCompleted) {
        if (workUnitsCompleted < 0) {
            throw new IllegalArgumentException("workUnitsCompleted may not be negative");
        }

        long remainingWorkUnits = getRemainingWorkUnits();
        if (workUnitsCompleted > remainingWorkUnits) {
            throw new IllegalStateException(
                    "More work than available completed. Remaining work units: " + remainingWorkUnits);
        }

        completedWorkUnits += workUnitsCompleted;
    }

    /**
     * Sets the total amount of work units to be completed.
     *
     * @param totalWorkUnits The total amount of work units
     */
    public void setTotalWorkUnits(long totalWorkUnits) {
        if(totalWorkUnits < 0){
            throw new IllegalArgumentException("totalWorkUnits may not be negative");
        }
        this.totalWorkUnits = totalWorkUnits;
    }

    @Override
    public ElapsedTime getElapsedTime() {
        return this.stopwatch.getElapsedTime();
    }

    @Override
    public String getElapsedTimeAsString() {
        return this.stopwatch.getElapsedTime().toString();
    }

    @Override
    public StopwatchType start() {
        if(this.stopwatch == null) {
            throw new IllegalStateException("Stopwatch may not be null");
        }
        if(this.totalWorkUnits < 1) {
            throw new IllegalStateException("To start estimator totalWorkUnits must be greater than zero");
        }
        return this.stopwatch.start();
    }

    @Override
    public StopwatchType stop() {
        this.stopwatch.stop();
        return (StopwatchType) this;
    }

    /**
     * Returns the amount of work units remaining to be completed.
     *
     * @return The amount of remaining work units
     */
    public long getRemainingWorkUnits() {
        return this.totalWorkUnits - this.completedWorkUnits;
    }

    /**
     * Returns totalWorkUnits
     * @return The amount of remaining work units
     */
    public long getTotalWorkUnits() {
        return totalWorkUnits;
    }

    /**
     * Returns the estimated remaining time formatted as a string in "HH:mm:ss" format.
     * Returns "∞" (infinity) if the remaining time cannot be calculated.
     *
     * @return A string representation of the remaining time
     */
    @Override
    public String getRemainingTimeAsString() {
        Duration remainingTime = this.remainingDuration();
        if(remainingTime.equals(MAX_DURATION)){
            return Estimator.INFINITY_STRING;

        }
        return DurationFormatUtils.formatDuration(remainingTime.toMillis(), "HH:mm:ss", true);
    }

    /**
     * Initializes the estimator with the specified total work units and starts the stopwatch.
     *
     * @param totalWorkUnitsArg The total amount of work units to be completed
     * @return This estimator instance
     * @throws IllegalStateException if the estimator has already been started
     * @throws IllegalArgumentException if totalWorkUnitsArg is not greater than zero
     */
    @Override
    public EstimatorType initAndStart(long totalWorkUnitsArg) {
        if(this.getState().equals(StopwatchState.STARTED)){
            throw new IllegalStateException("Estimator has already been started");
        }
        if(totalWorkUnitsArg <= 0){
            throw new IllegalArgumentException("totalWorkUnitsArg must be greater than zero");
        }
        this.setTotalWorkUnits(totalWorkUnitsArg);
        this.start();
        return (EstimatorType) this;
    }

    /**
     * Calculates and returns the estimated remaining time based on work progress and elapsed time.
     * Returns Duration.ZERO if no work remains or total work units is zero.
     * Returns MAX_DURATION if no work has been completed yet.
     *
     * @return The estimated remaining duration
     */
    @Override
    public Duration remainingDuration() {
        final long remainingWorkUnits = this.getRemainingWorkUnits();
        if (this.getTotalWorkUnits() == 0 || remainingWorkUnits == 0) {
            return Duration.ZERO;
        }

        long completedWorkUnits = this.getCompletedWorkUnits();
        if(completedWorkUnits == 0){
            return MAX_DURATION;
        }

        BigFraction ratioRemaining = new BigFraction(remainingWorkUnits, completedWorkUnits);
        BigFraction remainingTimeInNanos = ratioRemaining.multiply(this.getElapsedTime().getDuration().toNanos());

        return Duration.ofNanos(remainingTimeInNanos.longValue());
    }

    /**
     * @see #remainingDuration()
     * @return The estimated remaining time as ElapsedTime object
     */
    public ElapsedTime remaining() {
        return new ElapsedTime(this.remainingDuration());
    }

    /**
     * Returns completedWorkUnits
     * @return The amount of completed work units
     */
    public long getCompletedWorkUnits() {
        return completedWorkUnits;
    }

    @Override
    public InstantSource getInstantSource() {
        return this.stopwatch.getInstantSource();
    }

    /**
     * Returns the underlying stopwatch
     * @return StopwatchType
     */
    public StopwatchType getStopwatch() {
        return stopwatch;
    }
}
