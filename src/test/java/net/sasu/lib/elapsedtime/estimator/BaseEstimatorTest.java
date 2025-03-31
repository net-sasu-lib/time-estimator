package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.mock.MockStopwatch;
import net.sasu.lib.time.stopwatch.state.StopwatchState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseEstimatorTest {

	MockEstimator<DefaultEstimator<MockStopwatch>> mockEstimator;
	DefaultEstimator<MockStopwatch> defaultEstimator;
	MockStopwatch mockStopwatch;

	/**
	 * Tests that the init method
	 * <p>
	 * 1) returns a new instance of the BaseEstimator 2) calls the initAndStart
	 * method 3) sets the Stopwatch object
	 *
	 */
	@BeforeEach
	public void initTest() {
		this.mockStopwatch = new MockStopwatch();
		this.defaultEstimator = new DefaultEstimator<>(mockStopwatch);
		this.mockEstimator = new MockEstimator<>(this.defaultEstimator, this.mockStopwatch);
	}

	@Test
	void completeWorkUnitsExceptionIae() {
		Throwable exception = assertThrows(IllegalArgumentException.class, () -> this.mockEstimator.completeWorkUnits(-1));
		assertNotNull(exception);
	}

	@Test
	void completeWorkUnitsExceptionIse() {
		this.mockEstimator.initAndStart(10L);
		Throwable exception = assertThrows(IllegalStateException.class, () -> this.mockEstimator.completeWorkUnits(11));
		assertNotNull(exception);
	}

	@Test
	void getElapsedTimeAsStringTest() {
		this.mockStopwatch.start();
		this.mockStopwatch.increment();

		String elapsedTime = this.mockStopwatch.getElapsedTime().toString();
		String elapsedTimeAsString = this.mockEstimator.getElapsedTimeAsString();
		
		assertEquals(elapsedTime, elapsedTimeAsString);
	}
	
	@Test
	void stopTest() {
		this.mockStopwatch.start();
		this.mockEstimator.stop();

        assertNotEquals(StopwatchState.STARTED, this.mockStopwatch.getState());
	}
	
	@Test
	void startTest() {
		assertThrows(IllegalStateException.class, () -> this.mockEstimator.start());
	}

	@Test
	void startTest2() {
		this.mockEstimator.setTotalWorkUnits(10);
		this.mockEstimator.start();

		assertTrue(this.mockStopwatch.isRunning(), "State: " + this.mockStopwatch.getState());
	}
	
	@Test
	void getRemainingTimeAsStringTest() {
		this.mockEstimator.initAndStart(5);

		assertEquals(Estimator.INFINITY_STRING, this.mockEstimator.getRemainingTimeAsString());
	}
}
