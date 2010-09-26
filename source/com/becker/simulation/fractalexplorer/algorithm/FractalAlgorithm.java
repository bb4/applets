package com.becker.simulation.fractalexplorer.algorithm;

import com.becker.common.concurrency.Parallelizer;
import com.becker.common.math.ComplexNumber;
import com.becker.common.math.ComplexNumberRange;
import com.becker.common.profile.ProfilerEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation common to all fractal algorithms.
 * Uses concurrency when parallelized is set.
 * This will give good speedup on multi-core machines.
 *
 * For core 2 Duo:
 *  - not-parallel 32.4 seconds
 *  - parallel     19.5 seconds
 * @author Barry Becker
 */
public abstract class FractalAlgorithm {

    public static final int DEFAULT_MAX_ITERATIONS = 500;

    protected final FractalModel model;

    /** range of bounding box in complex plane. */
    private ComplexNumberRange range;

    /** Manages the worker threads. */
    private Parallelizer<Worker> parallelizer_;

    private ProfilerEntry timer_;

    private int maxIterations_ = DEFAULT_MAX_ITERATIONS;

    private RowCalculator rowCalculator_;

    private boolean restartRequested = false;

    private History history_ = new History();


    public FractalAlgorithm(FractalModel model, ComplexNumberRange range) {
        this.model = model;
        setRange(range);
        setParallelized(true);
        rowCalculator_ = new RowCalculator(this);
    }

    public void setRange(ComplexNumberRange range)  {

        history_.addRangeToHistory(range);
        processRange(range);
    }

    /**
     * Back up one step in the history
     */
    public void goBack() {
        ComplexNumberRange newRange = history_.popLastRange();
        if (range.equals(newRange)) {
            newRange = history_.popLastRange();
        }
        processRange(newRange);
    }

    private void processRange(ComplexNumberRange range) {
        this.range = range;
        restartRequested = true;
    }

    public boolean isParallelized() {
        return (parallelizer_.getNumThreads() > 1);
    }

    public void setParallelized(boolean parallelized) {
        if (parallelizer_ == null || parallelized != isParallelized()) {

            parallelizer_ =
                 parallelized ? new Parallelizer<Worker>() : new Parallelizer<Worker>(1);
            model.setCurrentRow(0);
        }
    }

    public int getMaxIterations() {
        return maxIterations_;
    }

    public void setMaxIterations(int value) {
        if (value != maxIterations_)  {
            maxIterations_ = value;
            model.setCurrentRow(0);
        }
    }

    public boolean getUseRunLengthOptimization() {
        return rowCalculator_.getUseRunLengthOptimization();
    }

    public void setUseRunLengthOptimization(boolean value) {
        rowCalculator_.setUseRunLengthOptimization(value);
    }

    public FractalModel getModel() {
        return model;
    }

    /**
     * @param timeStep number of rows to comput on this timestep.
     * @return true when done computing whole model.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            model.setCurrentRow(0);
            restartRequested = false;
        }
        if (model.isDone()) {
            stopTiming();
            return true;  // we are done.
        }

        int numProcs = parallelizer_.getNumThreads();
        List<Runnable> workers = new ArrayList<Runnable>(numProcs);
        
        // we calculate a little more each "timestep"
        int currentRow = model.getCurrentRow();
        if (currentRow == 0) {
            startTiming();
        }

        int computeToRow = Math.min(model.getHeight(), currentRow + (int)timeStep * numProcs);

        int diff = computeToRow - currentRow;
        if (diff == 0) return true;

        int chunk = Math.max(1, diff / numProcs);


        for (int i = 0; i < numProcs; i++) {
            workers.add(new Worker(currentRow, currentRow + chunk));
            //System.out.println("creating worker ("+i+") to compute " + chunk +" rows.");
            currentRow += chunk;
        }

        // blocks until all Callables are done running.
        parallelizer_.invokeAllRunnables(workers);

        model.setCurrentRow(currentRow);

        return false;
    }


    /**
     * @return a number between 0 and 1.
     * Typically corresponds to the number times we had to iterate before the point escaped (or not).
     */
    public abstract double getFractalValue(ComplexNumber seed);

    /**
     * Converts from screen coordinates to data coordinates.
     * @param x
     * @param y
     * @return corresponding position in complex number plane represented by the model.
     */
    public ComplexNumber getComplexPosition(int x, int y) {
        return range.getInterpolatedPosition((double)x / model.getWidth(), (double)y / model.getHeight()); 
    }


    private void startTiming() {
        timer_ = new ProfilerEntry("Fractal");
        timer_.start();
    }

    private void stopTiming() {
         if (timer_ != null) {
              timer_.stop();
              timer_.print();
              timer_ = null;
         }
    }

    /**
     * Runs one of the chunks.
     */
    private class Worker implements Runnable {

        private int fromRow_;
        private int toRow_;

        public Worker(int fromRow, int toRow) {

            fromRow_ = fromRow;
            toRow_ = toRow;
        }

        public void run() {
            computeChunk(fromRow_, toRow_);
        }


        /**
         * Do a chunk of work (i.e. compute the specified rows)
         */
        private void computeChunk(int fromRow, int toRow) {

            int width = model.getWidth();
            for (int y = fromRow; y < toRow; y++)   {
                rowCalculator_.calculateRow(width, y);
            }
        }
    }
}
