# Time Estimator Library

A Java library for estimating remaining time in iterative processes. This library provides accurate time estimations for tasks with known total work units, using different estimation strategies including moving averages.

## Features

- Easy-to-use API with good developer UX
- Simple default estimator implementation good for most use cases
- Extensible for implementing a better estimator for your specific use case , like variable-speed processes

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.sasu.lib.time</groupId>
    <artifactId>time-estimator</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

## Quick Start

Here's a simple example of how to use the BasicEstimator:

```java
// Create an estimator for 100 work units
BasicEstimator estimator = new BasicEstimator();
estimator.initAndStart(100);

// As work progresses:
while (workRemains()) {
    doSomeWork();
    estimator.completeWorkUnits(1);
    
    // Get estimation
    Duration remainingTime = estimator.getRemainingTime();
    System.out.println("Estimated time remaining: " + 
            estimator.getRemainingTimeAsString());
}
```

## Outputting time from the estimator

`time-estimator` builds upon the [elapsedtime](https://github.com/net-sasu-lib/elapsedtime) library for displaying
durations. This enables a flexible and easy-to-use fluent interface for outputting the remaing time, as seen in 
these examples:
```java
    public static void main(String[] args) throws InterruptedException {
        BasicEstimator estimator = new BasicEstimator();
        estimator.initAndStart(100);
    
        // As work progresses:
        while (workRemains()) {
            doSomeWork();
            estimator.completeWorkUnits(20);
            ElapsedTime elapsedTime = estimator.remaining();
    
            //four examples of outputting time
    
            //prints elapsed time to default output target (system out) in format HH:mm:ss.SSS
            //this prints "00:00:01.111"
            elapsedTime.println();
    
            //or maybe you want to use a Logger (e.g. SLF4J) instead?
            //(or any other method implementing java.util.function.Consumer<String>)
            Logger logger = LoggerFactory.getLogger("myLoggerName");
            elapsedTime.printTo(logger::info);
    
            //you can apply individual formatting (see Apache DurationFormatUtils for syntax)
            //this prints "1 seconds and 111 milliseconds"
            elapsedTime.formatDuration("s' seconds and 'S' milliseconds'").println();
    
            //this prints "1.111 s"
            elapsedTime.formatDuration("s'.'SSS' s'").println();
    
            // Get estimation
            Duration remainingTime = estimator.remainingDuration();
            System.out.println("Estimated time remaining: " +
                    estimator.getRemainingTimeAsString());
        }
    }
```

## Available Estimators

### BasicEstimator

Uses a moving average approach to provide accurate estimations based on recent performance.

```java
// Create with default window size (3)
BasicEstimator estimator = new BasicEstimator();

// Or specify custom window size
BasicEstimator estimator = new BasicEstimator(5);

// Initialize with total work units
estimator.initAndStart(totalWorkUnits);
```

### DefaultEstimator

Provides simple time estimation based on overall average completion time. You can use your own
[StopWatch](https://github.com/net-sasu-lib/stopwatch) implementation with DefaultEstimator if so desired.

```java
DefaultEstimator<Stopwatch> estimator = 
    DefaultEstimator.createInstanceAndStart(totalWorkUnits);
```

## Moving Average Concept

The BasicEstimator uses a moving average approach with a configurable window size. This means:

- Only the N most recent measurements are used (where N is the window size)
- Provides better adaptation to changing execution speeds
- Smooths out occasional outliers
- More accurate estimates based on recent performance

Example with window size 3:
1. First work unit: 1000ms → [1000ms]
2. Second work unit: 1050ms → [1000ms, 1100ms]
3. Third work unit: 1000ms → [1000ms, 1100ms, 900ms]
4. Fourth work unit: 1017ms → [1100ms, 900ms, 1050ms]

## Usage Tips

1. Choose appropriate window size:
   - Smaller window (2-3): Better for variable-speed processes
   - Larger window (5+): Better for consistent-speed processes

2. Initialize with accurate total work units:
   ```java
   estimator.initAndStart(totalWorkUnits);
   ```

3. Update progress regularly:
   ```java
   estimator.completeWorkUnits(completedUnits);
   ```

## Requirements

- Java 17 or higher
- Maven 3.x

## License

This project is licensed under the [MIT License](LICENSE). Feel free to use or modify it as appropriate for your own needs.

## Contributing

Contributions are welcome. If you find a bug or want to improve the library please open an issue before creating a pull request.

## Changelog

See [CHANGELOG.md](CHANGELOG.md)
