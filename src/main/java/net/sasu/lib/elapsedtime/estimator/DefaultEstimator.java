package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.Stopwatch;
import net.sasu.lib.time.stopwatch.StopwatchInterface;
import net.sasu.lib.time.stopwatch.state.StopwatchState;

import java.time.Instant;

/**
 * Basic implementation for estimations with low variability.
 * The remaining time estimated is based on the assumption
 * that all work units take approximately the same amount of 
 * time to complete.
 * 
 * @author Sasu
 *
 */
public class DefaultEstimator<StopwatchType extends StopwatchInterface<StopwatchType>>
        extends BaseEstimator<DefaultEstimator<StopwatchType>, StopwatchType>{

    public DefaultEstimator(StopwatchType stopwatch) {
        super(stopwatch);
    }

    public DefaultEstimator(StopwatchType stopwatch, long totalWorkUnitsArg) {
        super(stopwatch, totalWorkUnitsArg, 0);
    }

    public DefaultEstimator(StopwatchType stopwatch, long totalWorkUnitsArg, long completedWorkUnitsArg) {
        super(stopwatch, totalWorkUnitsArg, completedWorkUnitsArg);
    }

    public static DefaultEstimator<Stopwatch> createInstanceAndStart(long totalWorkUnitsArg) {
        DefaultEstimator<Stopwatch> defaultEstimator = new DefaultEstimator<>(new Stopwatch(), totalWorkUnitsArg);
        defaultEstimator.start();
        return defaultEstimator;
    }

    @Override
    public Instant getStartTime() {
        return this.getStopwatch().getStartTime();
    }

    @Override
    public Instant getStopTime() {
        return this.getStopwatch().getStopTime();
    }

    @Override
    public StopwatchState getState() {
        return this.getStopwatch().getState();
    }
}
