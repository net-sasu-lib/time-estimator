package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.mock.MockStopwatch;
import net.sasu.lib.time.stopwatch.state.StopwatchState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

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
		this.mockStopwatch.incrementSecond();

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
	void remainingDurationAsStringTest() {
		this.mockEstimator.initAndStart(5);

		assertEquals(Estimator.INFINITY_STRING, this.mockEstimator.getRemainingTimeAsString());
	}

	@Test
	void constructor_WithNullStopwatch_ShouldThrowException() {
		assertThrows(NullPointerException.class,
				() -> new MockEstimator<>(defaultEstimator, null));
	}

	@Test
	void constructor_WithInvalidCompletedWorkUnits_ShouldThrowException() {
		assertThrows(IllegalArgumentException.class,
				() -> new MockEstimator<>(defaultEstimator, mockStopwatch, 10, -1));
		assertThrows(IllegalArgumentException.class,
				() -> new MockEstimator<>(defaultEstimator, mockStopwatch, 10, 11));
	}

	@Test
	void getRemainingWorkUnits_ShouldReturnCorrectValue() {
		mockEstimator.initAndStart(100);
		mockEstimator.completeWorkUnits(30);
		assertEquals(70, mockEstimator.getRemainingWorkUnits());
	}

	@Test
	void getTotalWorkUnits_ShouldReturnCorrectValue() {
		mockEstimator.setTotalWorkUnits(100);
		assertEquals(100, mockEstimator.getTotalWorkUnits());
	}

	@Test
	void initAndStart_WithInvalidWorkUnits_ShouldThrowException() {
		assertThrows(IllegalStateException.class, () -> mockEstimator.initAndStart(0));
		assertThrows(IllegalArgumentException.class, () -> mockEstimator.initAndStart(-1));
	}

	@Test
	void remainingDuration_WithNoWorkCompleted() {
		mockEstimator.initAndStart(100);
		assertEquals(Estimator.MAX_DURATION, mockEstimator.remainingDuration());
	}

	@Test
	void remainingDuration_WithAllWorkCompleted() {
		mockEstimator.initAndStart(10);
		mockEstimator.completeWorkUnits(10);
		assertEquals(Duration.ZERO, mockEstimator.remainingDuration());
	}

	@Test
	void remainingDuration_WithPartialCompletion() {
		mockEstimator.initAndStart(100);
		mockStopwatch.incrementSecond(); // 1 second elapsed
		mockEstimator.completeWorkUnits(50); // Half complete
		assertEquals(Duration.ofSeconds(1), mockEstimator.remainingDuration());
	}

	@Test
	void getCompletedWorkUnits_ShouldReturnCorrectValue() {
		mockEstimator.initAndStart(100);
		mockEstimator.completeWorkUnits(30);
		assertEquals(30, mockEstimator.getCompletedWorkUnits());
	}

	@Test
	void getInstantSource_ShouldReturnStopwatchSource() {
		assertEquals(mockStopwatch.getInstantSource(), mockEstimator.getInstantSource());
	}

	@Test
	void getStopwatch_ShouldReturnUnderlyingStopwatch() {
		assertSame(mockStopwatch, mockEstimator.getStopwatch());
	}

	@Test
	void remaining_ShouldReturnCorrectElapsedTime() {
		mockEstimator.initAndStart(100);
		mockStopwatch.incrementSecond();
		mockEstimator.completeWorkUnits(50);
		assertEquals(Duration.ofSeconds(1), mockEstimator.remaining().getDuration());
	}
}
