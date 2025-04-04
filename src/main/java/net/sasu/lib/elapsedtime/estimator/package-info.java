/**
 * Estimator classes, the "meat" of this project, define the common behaviour and traits of how remaining time is
 * estimated. The class {@link net.sasu.lib.elapsedtime.estimator.Estimator} is the base interface defining the
 * estimator. {@link net.sasu.lib.elapsedtime.estimator.BaseEstimator} builds upon that the interface
 * adding some methods commonly used by all specific implementations.
 * {@link net.sasu.lib.elapsedtime.estimator.DefaultEstimator} is the first concrete (non-abstract) class, which
 * can be used as-is or as a basis to extend for your own implementation.
 * {@link net.sasu.lib.elapsedtime.estimator.BasicEstimator} is an example of such an implementation, using
 * moving averages to calculate the remaining estimated time.
 */
package net.sasu.lib.elapsedtime.estimator;