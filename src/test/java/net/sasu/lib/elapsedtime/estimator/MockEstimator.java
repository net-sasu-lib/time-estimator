package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.elapsedtime.time.MockStopwatch;

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
    public MockStopwatch saveCurrentTime() {
        return this.stopwatch;
    }

}
