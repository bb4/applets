/** Barry Becker - copyright 2009 */
package com.becker.common.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Using this class you should be able to easily parallelize a set of long running tasks.
 * Immutable.
 * 
 * @author Barry Becker
 */
class CallableParallelizer<T> {

    /** The number of processors available on this computer */
    public static final int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /** Recycle threads so we do not create thousands and eventually run out of memory. */
    private ExecutorService executor;

    /**
     * By default, the number of threads we use is equal to the number of processors
     * (in some cases I read it may be better to add 1 to this, but I have not seen better results doing that.)
     */
    private static final int DEFAULT_NUM_THREADS = NUM_PROCESSORS;

    /** Number of thread to distribute the task among. */
    private int numThreads;

    /**
     * Constructs with default number of threads.
     */
    public CallableParallelizer() {

        this(DEFAULT_NUM_THREADS);
    }

    /**
     * Construct with specified number of threads.
     * @param numThreads number of thread. Must be 1 or greater. One means not parallelism.
     */
    public CallableParallelizer(int numThreads) {
        assert numThreads > 0;
        this.numThreads = numThreads;
        executor = Executors.newFixedThreadPool(numThreads);
    }
    
    public int getNumThreads() {
        return numThreads;
    }
    
    /**
     * Invoke all the workers at once and block until they are all done
     * Once all the separate threads have completed there assigned work, you may want to commit the results.
     * @return list of Future tasks.
     */
    public List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables)  {
            
        List<Future<T>> f = null;
        try {
            f = executor.invokeAll(callables);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return f;
    }
}
