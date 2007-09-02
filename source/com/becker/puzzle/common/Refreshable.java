package com.becker.puzzle.common;

import java.util.List;

/**
 * A UI element that can be refreshed to show the current state.
 *
 * Created on July 28, 2007, 7:00 AM
 * @author becker
 */
public interface Refreshable <P, M> {
    
    /**
     * Call when you want the UI to update.
     * @param done if true then the puzzle simulation has completed.
     */
    void refresh(P pos, long numTries);
    
    /**
     *Show the path to the solution at the end.
     *@param path list of moves that gets to the solution. If path is null then there was not solution found.
     *@param position the final board state.
     *@param numTries number of tries it took to find that final state.
     *@param millis number of milliseconds it took to find the solution.
     */
    void finalRefresh(List<M> path, P position, long numTries, long millis);
    
    /**
     *Make a sound of some sort
     */
    void makeSound();

}
