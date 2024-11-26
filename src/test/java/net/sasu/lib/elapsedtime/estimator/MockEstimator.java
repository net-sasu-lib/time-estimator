package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.mock.MockStopwatch;
import net.sasu.lib.time.stopwatch.state.StopwatchState;

import java.time.Instant;

class MockEstimator<EstimatorType extends BaseEstimator<EstimatorType, MockStopwatch>> extends BaseEstimator<EstimatorType, MockStopwatch> {

    boolean initAndStartMethodCalled = false;
    final MockStopwatch stopwatch;
    final EstimatorType estimator;

    public MockEstimator(EstimatorType estimator, MockStopwatch stopwatch) {
        super(stopwatch);
        this.stopwatch = stopwatch;
        this.estimator = estimator;
    }

    @Override
    public EstimatorType initAndStart(long remainingWorkUnitsArg) {
        initAndStartMethodCalled = true;
        this.setTotalWorkUnits(remainingWorkUnitsArg);
        this.start();

        return this.estimator;
    }

    public MockStopwatch getStopwatch() {
        return stopwatch;
    }

    @Override
    public Instant getStartTime() {
        return this.stopwatch.getStartTime();
    }

    @Override
    public Instant getStopTime() {
        return this.stopwatch.getStopTime();
    }

    @Override
    public StopwatchState getState() {
        return this.stopwatch.getState();
    }
}
