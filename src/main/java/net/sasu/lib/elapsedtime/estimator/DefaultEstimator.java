package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.DefaultStopwatch;
import net.sasu.lib.time.stopwatch.Stopwatch;

/**
 * Basic implementation for estimations with low variability.
 * The remaining time estimated is based on the assumption
 * that all work units take approximately the same amount of 
 * time to complete.
 * 
 * @author Sasu
 *
 */
public class DefaultEstimator<StopwatchType extends Stopwatch<StopwatchType>>
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

    public static DefaultEstimator<DefaultStopwatch> createInstanceAndStart(long totalWorkUnitsArg) {
        DefaultEstimator<DefaultStopwatch> defaultEstimator = new DefaultEstimator<>(new DefaultStopwatch(), totalWorkUnitsArg);
        defaultEstimator.start();
        return defaultEstimator;
    }

    @Override
    public StopwatchType saveCurrentTime() {
        return this.getStopwatch();
    }

}
