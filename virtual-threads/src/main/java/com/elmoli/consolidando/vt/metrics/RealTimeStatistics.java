/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.metrics;


import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class RealTimeStatistics
{

    private static final AtomicLong minimumRequestTimeInNs = new AtomicLong(1000_000_000L);
    private static final AtomicLong maximumRequestTimeInNs = new AtomicLong(0L);
    private final DoubleAdder sum = new DoubleAdder(); // Sum of all values
    private final DoubleAdder sumOfSquares = new DoubleAdder(); // Sum of squares of all values
    private final LongAdder count = new LongAdder(); // Count of values
    
    
    public void reset()
    {
        minimumRequestTimeInNs.set(1000_000_000L);
        maximumRequestTimeInNs.set(0L);
        sum.reset();
        sumOfSquares.reset();
        count.reset();
    }

    // Method to register a value during a request concurrently
    public void addValue(long requestTimeInNs)
    {
        sum.add(requestTimeInNs);
        sumOfSquares.add(requestTimeInNs * requestTimeInNs);
        count.increment();

        minimumRequestTimeInNs.updateAndGet(current -> Math.min(current, requestTimeInNs));
        maximumRequestTimeInNs.updateAndGet(current -> Math.max(current, requestTimeInNs));
    }
    
    public long getMaximumRequestTime()
    {
        return(maximumRequestTimeInNs.get());
    }
    
    public long getMinimumRequestTime()
    {
        return(minimumRequestTimeInNs.get());
    }
    
    public long getCount()
    {
        return(count.sum());
    }

    // Method to calculate the mean
    public double calculateMean()
    {
        long totalCount = count.sum();
        if (totalCount == 0)
        {
            return Double.NaN;
        }

        return sum.sum() / totalCount;
    }

    // Method to calculate the standard deviation
    public double calculateStandardDeviation()
    {
        long totalCount = count.sum();
        if (totalCount == 0)
        {
            return Double.NaN;
        }

        double mean = sum.sum() / totalCount;
        double variance = sumOfSquares.sum() / totalCount - mean * mean;
        return Math.sqrt(variance);
    }
}


