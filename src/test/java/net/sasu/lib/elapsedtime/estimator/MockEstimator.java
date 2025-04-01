package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.mock.MockStopwatch;
import net.sasu.lib.time.stopwatch.state.StopwatchState;

import java.time.Instant;

/**
 * A mock implementation of BaseEstimator for testing purposes.
 * This class provides the ability to track method calls and control behavior
 * in a testing environment.
 *
 * <p>The mock estimator works in conjunction with a MockStopwatch to facilitate
 * testing of time-based estimation functionality without actual time dependencies.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * MockStopwatch mockStopwatch = new MockStopwatch();
 * MockEstimator<MyEstimator> mockEstimator = new MockEstimator<>(myEstimator, mockStopwatch);
 * mockEstimator.initAndStart(100);
 * assert mockEstimator.initAndStartMethodCalled;
 * </pre>
 *
 * @param <EstimatorType> The type of estimator being mocked, must extend BaseEstimator
 */
class MockEstimator<EstimatorType extends BaseEstimator<EstimatorType, MockStopwatch>>
        extends BaseEstimator<EstimatorType, MockStopwatch> {

    /**
     * Flag indicating whether the initAndStart method has been called.
     */
    boolean initAndStartMethodCalled = false;

    /**
     * The mock stopwatch instance used for time tracking simulation.
     */
    final MockStopwatch stopwatch;

    /**
     * The actual estimator instance being mocked.
     */
    final EstimatorType estimator;

    /**
     * Constructs a new MockEstimator with the specified estimator and mock stopwatch.
     *
     * @param estimator The actual estimator instance to be mocked
     * @param stopwatch The mock stopwatch to be used for time tracking
     */
    public MockEstimator(EstimatorType estimator, MockStopwatch stopwatch) {
        super(stopwatch);
        this.stopwatch = stopwatch;
        this.estimator = estimator;
    }

    /**
     * Initializes and starts the mock estimator, tracking that the method was called.
     *
     * @param remainingWorkUnitsArg The total amount of work units to be completed
     * @return The actual estimator instance
     */
    @Override
    public EstimatorType initAndStart(long remainingWorkUnitsArg) {
        initAndStartMethodCalled = true;
        this.setTotalWorkUnits(remainingWorkUnitsArg);
        this.start();
        return this.estimator;
    }

    /**
     * Returns the mock stopwatch instance used by this estimator.
     *
     * @return The MockStopwatch instance
     */
    public MockStopwatch getStopwatch() {
        return stopwatch;
    }

    /**
     * Returns the start time from the mock stopwatch.
     *
     * @return The simulated start time
     */
    @Override
    public Instant getStartTime() {
        return this.stopwatch.getStartTime();
    }

    /**
     * Returns the stop time from the mock stopwatch.
     *
     * @return The simulated stop time
     */
    @Override
    public Instant getStopTime() {
        return this.stopwatch.getStopTime();
    }

    /**
     * Returns the current state of the mock stopwatch.
     *
     * @return The current StopwatchState
     */
    @Override
    public StopwatchState getState() {
        return this.stopwatch.getState();
    }
}