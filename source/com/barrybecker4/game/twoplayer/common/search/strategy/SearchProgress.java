// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;

/**
 * Interface for something that can be queries for current search progress toward finding next move.
 *
 * @author Barry Becker
 */
public interface SearchProgress {

    /**
     * @return the number of moves considered in the search so far.
     */
    long getNumMovesConsidered();

    /**
     * Approximate percent completed for the search.
     * Approximate because pruning can cause the search to speed up considerably toward the end.
     *
     * @return the approximate percentage of total search time that has been completed.
     */
    int getPercentDone();

}
