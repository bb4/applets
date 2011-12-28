/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import junit.framework.Assert;

import java.util.List;

/**
 * Verify that all our neighbor analysis methods work.
 * @author Barry Becker
 */
public class TestGroupNeighborAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/neighbor/";

    /** instance under test */
    GroupNeighborAnalyzer groupAnalyzer_;
    GoBoard board_;

    public void testGroupNbrs_None() {
        initializeAnalyzer("groupNbr_none");
        verifyFriendGroupNbrs(5, 5, true, 0);
        verifyFriendGroupNbrs(5, 5, false, 0);
        verifyAllGroupNbrs(5, 5, true, 0);
        verifyAllGroupNbrs(5, 5, false, 0);
    }

    public void testGroupNbrs_OneDiagonalFriendPartialCut() {
        initializeAnalyzer("groupNbr_oneDiagonalFriendPartialCut");
        verifyFriendGroupNbrs(5, 5, true, 1);
        verifyFriendGroupNbrs(5, 8, false, 1);
    }

    public void testGroupNbrs_OneDiagonalFriendCut() {
        initializeAnalyzer("groupNbr_oneDiagonalFriendCut");
        verifyFriendGroupNbrs(5, 5, true, 0);
        verifyFriendGroupNbrs(5, 8, false, 0);
    }

    public void testGroupNbrs_OneDiagonalEnemy() {
        initializeAnalyzer("groupNbr_oneDiagonalEnemy");
        verifyAllGroupNbrs(5, 5, true, 2);
        verifyAllGroupNbrs(5, 8, false, 2);
    }

    public void testGroupNbrs_OneDiagonalEnemyCut() {
        initializeAnalyzer("groupNbr_oneDiagonalEnemyCut");
        verifyAllGroupNbrs(5, 5, true, 3);
        verifyAllGroupNbrs(5, 8, false, 2);
    }

    public void testGroupNbrs_OneOneSpaceJumpFriendCut() {
        initializeAnalyzer("groupNbr_oneOneSpaceJumpFriendCut");
        verifyFriendGroupNbrs(5, 5, true, 0);
        verifyFriendGroupNbrs(5, 8, false, 0);
    }

    public void testGroupNbrs_OneOneSpaceJumpFriendAtariCut() {
        initializeAnalyzer("groupNbr_oneOneSpaceJumpFriendAtariCut");
        verifyFriendGroupNbrs(5, 5, true, 1);
        verifyFriendGroupNbrs(5, 8, false, 1);
    }

    public void testGroupNbrs_OneKogeimaFriendCut() {
        initializeAnalyzer("groupNbr_oneKogeimaFriendCut");
        verifyFriendGroupNbrs(5, 5, true, 0);
        verifyFriendGroupNbrs(5, 8, false, 0);
    }

    /**
     *    x--
     *    O-X   this should not be a cut of the knights move.
     */
    public void testGroupNbrs_OneKogeimaFriendPartialCut() {
        initializeAnalyzer("groupNbr_oneKogeimaFriendPartialCut");
        verifyFriendGroupNbrs(5, 5, true, 1);
        verifyFriendGroupNbrs(5, 8, false, 1);
    }

    /**
     *   This should not be a cut of the knights move.
     */
    public void testGroupNbrs_OneKogeimaFriendPartialCutInCorner() {
        initializeAnalyzer("groupNbr_cornerGroupKogeima");
        verifyFriendGroupNbrs(1, 7, false, 1);
        verifyFriendGroupNbrs(2, 5, false, 1);
    }


    public void testGroupNbrs_OneFriend() {
        initializeAnalyzer("groupNbr_oneFriend");
        verifyFriendGroupNbrs(5, 5, true, 1);
        verifyFriendGroupNbrs(5, 8, false, 1);
        verifyAllGroupNbrs(5, 5, true, 1);
        verifyAllGroupNbrs(5, 8, false, 1);
    }

    public void testGroupNbrs_OneNotFriend() {
        initializeAnalyzer("groupNbr_oneNotFriend");
        verifyFriendGroupNbrs(5, 5, true, 0);
        verifyFriendGroupNbrs(5, 8, false, 0);
        verifyAllGroupNbrs(5, 5, true, 1);
        verifyAllGroupNbrs(5, 8, false, 1);
    }

    public void testGroupNbrs_Mixed() {
        initializeAnalyzer("groupNbr_mixed");
        verifyFriendGroupNbrs(5, 5, true, 2);
        verifyFriendGroupNbrs(5, 8, false, 2);
        verifyAllGroupNbrs(5, 5, true, 3);
        verifyAllGroupNbrs(5, 8, false, 3);
    }

    public void testGroupNbrs_nobiOnly() {
        initializeAnalyzer("groupNbr_nobiOnly");
        verifyFriendGroupNbrs(5, 5, true, 4);
        verifyFriendGroupNbrs(5, 8, false, 4);
    }
    
    // Only pure group neigbors present.
    public void testGroupNbrs_pureOnly() {
        initializeAnalyzer("groupNbr_pureOnly");
        verifyFriendGroupNbrs(5, 5, true, 16);
        verifyFriendGroupNbrs(9, 9, false, 16);
    }

    public void testGroupNbrs_all20Mixed() {
        initializeAnalyzer("groupNbr_all20Mixed");
        verifyFriendGroupNbrs(5, 5, true, 9);
        verifyFriendGroupNbrs(9, 9, false, 12);
        verifyAllGroupNbrs(5, 5, true, 10);
        verifyAllGroupNbrs(9, 9, false, 12);
    }

    public void testGroupNbrs_randomMixed() {
        initializeAnalyzer("groupNbr_randomMixed");
        verifyFriendGroupNbrs(5, 5, true, 1);
        verifyFriendGroupNbrs(9, 9, false, 2);
        verifyAllGroupNbrs(5, 5, true, 4);
        verifyAllGroupNbrs(9, 9, false, 3);
    }


    /**
     * Corner group on a 7*7 board from a real game.
     */
    public void testFindGroupFromInitialPosition() {
        initializeAnalyzer("groupNbr_cornerGroupKogeima");
        GoBoardPosition pos1 = (GoBoardPosition)board_.getPosition(2, 5);
        List<GoBoardPosition> group1 = groupAnalyzer_.findGroupFromInitialPosition(pos1, true);


        GoBoardPosition pos2 = (GoBoardPosition)board_.getPosition(1, 7);
        List<GoBoardPosition> group2 = groupAnalyzer_.findGroupFromInitialPosition(pos2, true);

        System.out.println("group1=" + group1);
        System.out.println("group2=" + group2);
        assertEquals("Group1 did not have expected size.", 2, group1.size());
        assertEquals("Groups did not have the same size", group1.size(), group2.size());
    }


    private void initializeAnalyzer(String file) {
        restore(PREFIX +file);
        board_ = getBoard();
        groupAnalyzer_ = new GroupNeighborAnalyzer(board_);
    }

    private void verifyFriendGroupNbrs(int row, int col, boolean friendP1, int expectedNumNbrs) {
        verifyGroupNbrs(row, col, friendP1, true, expectedNumNbrs);
    }

    private void verifyAllGroupNbrs(int row, int col, boolean friendP1, int expectedNumNbrs) {
        verifyGroupNbrs(row, col, friendP1, false, expectedNumNbrs);
    }

    private void verifyGroupNbrs(int row, int col, boolean friendP1, boolean samePlayerOnly,
                                      int expectedNumNbrs) {

        GoBoardPosition pos = (GoBoardPosition) board_.getPosition(row, col);
        int numNbrs = groupAnalyzer_.findGroupNeighbors(pos, friendP1, samePlayerOnly).size();
        Assert.assertEquals("Unexpected number of group neigbors found.",
                expectedNumNbrs, numNbrs);
    }
}