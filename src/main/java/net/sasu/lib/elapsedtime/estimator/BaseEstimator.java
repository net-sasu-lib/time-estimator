/**
 * 
 */
package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.Stopwatch;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.fraction.BigFraction;

import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base estimator for handling common tasks. If you don't want to implement
 * your Estimator class from scratch, you can use this as a base.
 * 
 * @author Sasu
 *
 */
public abstract class BaseEstimator<
        EstimatorType extends BaseEstimator<EstimatorType, StopwatchType>,
        StopwatchType extends Stopwatch<StopwatchType>
        > implements Estimator<EstimatorType, StopwatchType> {

    private long totalWorkUnits;
    private long completedWorkUnits;

    StopwatchType stopwatch;

    final List<Instant> allTimePoints = new ArrayList<>();

    public BaseEstimator(StopwatchType stopwatch) {
        this(stopwatch, 0, 0);
    }

    public BaseEstimator(StopwatchType stopwatch, long totalWorkUnits, long completedWorkUnits) {
        Objects.requireNonNull(stopwatch);
        if(completedWorkUnits < 0 || completedWorkUnits > totalWorkUnits){
            throw new IllegalArgumentException("completedWorkUnits must be between 0 and " + totalWorkUnits);
        }

        this.stopwatch = stopwatch;
        this.totalWorkUnits = totalWorkUnits;
        this.completedWorkUnits = completedWorkUnits;
    }

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

    public void setTotalWorkUnits(long totalWorkUnits) {
        this.totalWorkUnits = totalWorkUnits;
    }

    @Override
    public Duration getElapsedTime() {
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
        if(this.totalWorkUnits == 0) {
            throw new IllegalStateException("To start estimator totalWorkUnits must be greater than zero");
        }
        return this.stopwatch.start();
    }

    @Override
    public StopwatchType stop() {
        this.stopwatch.stop();
        return (StopwatchType) this;
    }

    public long getRemainingWorkUnits() {
        return this.totalWorkUnits - this.completedWorkUnits;
    }

    public long getTotalWorkUnits() {
        return totalWorkUnits;
    }

    /**
     * Returns string containing estimated remaining time in seconds
     * with a millisecond precision, e.g. "5.002 seconds"
     */
    @Override
    public String getRemainingTimeAsString() {
        Duration remainingTime = this.getRemainingTime();
        if(remainingTime.equals(MAX_DURATION)){
            return Estimator.INFINITY_STRING;

        }
        return DurationFormatUtils.formatDuration(remainingTime.toMillis(), "HH:mm:ss", true);
    }

    @Override
    public EstimatorType initAndStart(long totalWorkUnitsArg) {
        if(this.isRunning()){
            throw new IllegalStateException("Estimator has already been started");
        }
        if(totalWorkUnitsArg <= 0){
            throw new IllegalArgumentException("totalWorkUnitsArg must be greater than zero");
        }
        this.setTotalWorkUnits(totalWorkUnitsArg);
        this.start();
        return (EstimatorType) this;
    }

    @Override
    public Duration getRemainingTime() {
        final long remainingWorkUnits = this.getRemainingWorkUnits();
        if (this.getTotalWorkUnits() == 0 || remainingWorkUnits == 0) {
            return Duration.ZERO;
        }

        long completedWorkUnits = this.getCompletedWorkUnits();
        if(completedWorkUnits == 0){
            return MAX_DURATION;
        }

        BigFraction ratioRemaining = new BigFraction(remainingWorkUnits, completedWorkUnits);
        BigFraction remainingTimeInNanos = ratioRemaining.multiply(this.getElapsedTime().toNanos());

        return Duration.ofNanos(remainingTimeInNanos.longValue());
    }

    public long getCompletedWorkUnits() {
        return completedWorkUnits;
    }

    @Override
    public InstantSource getInstantSource() {
        return this.stopwatch.getInstantSource();
    }

    @Override
    public Instant getInstant() {
        return this.stopwatch.getInstant();
    }

    @Override
    public boolean isRunning() {
        return this.stopwatch.isRunning();
    }

    @Override
    public List<Instant> getAllTimePoints() {
        return this.stopwatch.getAllTimePoints();
    }

    public StopwatchType getStopwatch() {
        return stopwatch;
    }
}
