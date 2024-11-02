package net.sasu.lib.elapsedtime.estimator;

import net.sasu.lib.time.stopwatch.DefaultStopwatch;

public class DefaultEstimatorExample {

	public static void main(String[] args) throws InterruptedException{
        long unitsOfWork = 4L;

        DefaultEstimator defaultEstimator = DefaultEstimator.createInstanceAndStart(unitsOfWork);
        String remainingTimeAsString = defaultEstimator.getRemainingTimeAsString();
        System.out.println("Before starting work. Remaining time: " + remainingTimeAsString);

        final int sleepTime = 1000;
        for(int i = 0; i < unitsOfWork; i++){
            System.out.println("Starting to execute one unit of work. Work left: " + (unitsOfWork - i) + " units.");

            defaultEstimator.completeWorkUnits(1);
            Thread.sleep(sleepTime); //simulate work

            remainingTimeAsString = defaultEstimator.getRemainingTimeAsString();
            System.out.println("Executed work unit. Remaining time: " + remainingTimeAsString);

        }
    }
}
