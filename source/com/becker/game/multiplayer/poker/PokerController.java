package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.multiplayer.poker.ui.PokerGameViewer;
import com.becker.game.multiplayer.poker.ui.RoundOverDialog;
import com.becker.game.card.Card;
import com.becker.optimization.ParameterArray;

import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Defines everything the computer needs to know to play Poker.
 *
 * ToDo list
 * - use real faces for players
 * - add chip legend in info panel
 *
 *  - options dialog
 *     - Texas holdem
 *     - N card stud
 *          - num cards for each
 *          - whether to use jokers
 *          - allow n exchanges
 *          - raise limit (eg $20)
 *   - summary dlg
 *      - show who gets pot
 *      - show the pot
 *      - give option to start another round with same players
 *      - unless really done then you can only exit
 *
 *  possible bugs
 *    - ante getting subtracted twice
 *    - first player always going first (should rotate)
 *

 *  - lower high card beat higher high card   (fixed?)
 *  - asking folded player to play  (fixed?)
 *
 * @author Barry Becker
 */
public class PokerController extends GameController
{

    private static final int DEFAULT_NUM_ROWS = 32;
    protected static final int DEFAULT_NUM_COLS = 32;

    private static final int DEFAULT_ANTE = 2;
    private static final int DEFAULT_MAX_ABS_RAISE = 50;

    private int currentPlayerIndex_;
    private int ante_ = DEFAULT_ANTE;
    private int maxAbsoluteRaise_ = DEFAULT_MAX_ABS_RAISE;
    private int pot_;
    private int startingPlayerIndex_ = 0;

    /**
     *  Construct the Poker game controller
     */
    public PokerController()
    {
        board_ = new PokerTable( DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS );
        initializeData();
    }

    /**
     *  Construct the Poker game controller given an initial board size
     */
    public PokerController(int nrows, int ncols )
    {
        board_ = new PokerTable( nrows, ncols);
        initializeData();
    }


    /**
     * Return the game board back to its initial openning state
     */
    public void reset()
    {
        super.reset();
        initializeData();
        anteUp();
    }

    protected void initializeData()
    {
        pot_ = 0;
        initPlayers();
        ((PokerTable)board_).initPlayers((PokerPlayer[])players_, this);
    }

     /**
     * by default we start with one human and one robot player.
     */
    private void initPlayers()
    {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (players_ == null) {
            // create the default players. One human and one robot.
            players_ = new PokerPlayer[2];
            PokerPlayer[] gplayers = (PokerPlayer[])players_;
            //PokerHand hand = new PokerHand(null);
            players_[0] = PokerPlayer.createPokerPlayer("Player 1",
                                       100, PokerPlayer.getNewPlayerColor(gplayers), true);

            players_[1] = PokerPlayer.createPokerPlayer("Player 2",
                                       100, PokerPlayer.getNewPlayerColor(gplayers), false);

        }

        System.out.println(" init players dealcards");
        dealCardsToPlayers(5);
        currentPlayerIndex_ = 0;
    }

    /**
     * deat the casrds.
     * @param numCardsToDealToEachPlayer
     */
    private void dealCardsToPlayers(int numCardsToDealToEachPlayer) {
         // give the default players some cards.
        ArrayList<Card> deck = Card.newDeck();
        for (int i=0; i<players_.length; i++)  {
            if  (deck.size() < numCardsToDealToEachPlayer) {
                // ran out of cards. start a new shuffled deck.
                deck = Card.newDeck();
            }
            PokerPlayer player = ((PokerPlayer)players_[i]);
            player.setHand(new PokerHand(deck, numCardsToDealToEachPlayer));
            player.setOutOfGame(false);
        }
    }

    /**
     * collect the antes
     */
    public void anteUp() {
        // get players to ante up, if they have not already
        if (this.getPotValue() == 0) {
            for (int i=0; i<players_.length; i++)  {
                PokerPlayer player = ((PokerPlayer)players_[i]);
                // if a player does not have enough money to ante up, he is out of the game
                if (player.getCash() < getAnte())  {
                    player.setOutOfGame(true);
                } else {
                    player.contributeToPot(this, getAnte());
                }
            }
        }
    }

    public void addToPot(int amount) {
        assert(amount > 0) : "You must add a positive amount";
        pot_ += amount;
    }

    /**
     * @return the maximum contribution made by any player so far
     */
    public int getCurrentMaxContribution() {
       int max = Integer.MIN_VALUE;
        Player[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (p.getContribution() > max) {
                max = p.getContribution();
            }
        }
        return max;
    }

    public void setMaxAbsolutRaise(int maxAbsoluteRaise) {
        maxAbsoluteRaise_ = maxAbsoluteRaise;
    }

    public int getMaxAbsoluteRaise()  {
        return maxAbsoluteRaise_;
    }

    /**
     *
     * @return the min number of chips of any player
     */
    public int getAllInAmount() {
        // loop through the players and return the min number of chips of any player
        int min = Integer.MAX_VALUE;
        Player[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (!p.hasFolded() && (p.getCash() < min)) {
                min = p.getCash();
            }
        }
        return min;
    }

    public void setAnte(int amount) {
        ante_ = amount;
    }

    public int getAnte() {
        return ante_;
    }

    /**
     *
     * @return the player whos turn it is now.
     */
    public Player getCurrentPlayer()
    {
        return players_[currentPlayerIndex_];
    }

    public void computerMovesFirst()
    {
        PokerGameViewer gviewer  = (PokerGameViewer)this.getViewer();
        gviewer.doComputerMove(getCurrentPlayer());
    }


    public int getPotValue() {
        return pot_;
    }

    public void setPotValue(int potValue) {
        pot_ = potValue;
    }

    /**
     * Game is over when only one player has enough money left to play
     *
     * @return true if the game is over.
     */
    public boolean done()
    {
        if (getLastMove()==null)
            return false;


        Player[] players = getPlayers();
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = (PokerPlayer)players[i];
            if (!p.isOutOfGame())
                return false;
        }
        return true;
    }


    /**
     * advance to the next player turn in order.
     * @return the index of the next player to play.
     */
    public int advanceToNextPlayer()
    {
        PokerGameViewer pviewer  = (PokerGameViewer)this.getViewer();
        pviewer.refresh();

        // show message when done.
        if (done()) {
            System.out.println( "advanceToNextPlayer done" );
            pviewer.sendGameChangedEvent(null);
            return 0;
        }


        int nextIndex = advanceToNextPlayerIndex();

        if (roundOver()) {
            // every player left in the game has called.
            PokerRound round = pviewer.createMove(getLastMove());
            // records the result on the board.
            makeMove(round);
            pviewer.refresh();

            doRoundOverBookKeeping(pviewer);
        }

        if (!getCurrentPlayer().isHuman()) {
            pviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        pviewer.sendGameChangedEvent(null);

        return nextIndex;
    }


    private void doRoundOverBookKeeping(PokerGameViewer pviewer) {
        PokerPlayer winner = determineWinner();
        int winnings = this.getPotValue();
        winner.claimPot(this);
        pviewer.showRoundOver(winner, winnings);
        // start a new round deal new cards and ante
        dealCardsToPlayers(5);
        anteUp();
        startingPlayerIndex_ = (startingPlayerIndex_++) % this.getNumPlayers();
        currentPlayerIndex_ = startingPlayerIndex_;
    }

    private boolean allButOneFolded() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();

        int numNotFolded = 0;
        for (int i=0; i<players.length; i++) {
            if (!players[i].hasFolded())  {
                numNotFolded++;
            }
        }
        if (numNotFolded == 1) {
            return true;
        }
        return false;
    }

    /**
     * the round is over if there is only one player left who has not folded, or
     * everyone has had a chance to call.
     * @return true of the round is over
     */
    private boolean roundOver() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();

        if (allButOneFolded())  {
            System.out.println("all but one folded");
            return true;
        }

        // special case of no one raising
        boolean nooneRaisedYet = (getAnte()*getNumPlayers() == getPotValue());
        if ((getCurrentPlayer() == getFirstPlayer()) && nooneRaisedYet) {
            System.out.println("no one raised");
            return true;
        }
        if (nooneRaisedYet)
            return false;

        int contrib = this.getCurrentMaxContribution();
        //boolean allMetRaise = true;
        for (int i=0; i<players.length; i++) {
            PokerPlayer p = players[i];
            if (!p.hasFolded()) {
                if (p.getContribution() != contrib) {

                    return false;
                }
            }
        }
        System.out.println("all players have contributed");
        return true;
    }



    /**
     *
     * @return the player with the best poker hand
     */
    private PokerPlayer determineWinner() {
        PokerPlayer[] players = (PokerPlayer[])getPlayers();
        PokerPlayer winner = null;
        PokerHand bestHand = null;
        int first=0;
        //
        while (players[first].hasFolded() && first < players.length) {
            first++;
        }
        if (players[first].hasFolded())
            GameContext.log(0, "All players folded. That was dumb. The winner will be random.");

        winner = players[first];
        bestHand = winner.getHand();

        for (int i=first+1; i<players.length; i++) {
            PokerPlayer p = players[i];
            if (!p.hasFolded() && p.getHand().compareTo(bestHand) > 0) {
                bestHand = p.getHand();
                winner = p;
            }
        }
        return winner;
    }

    /**
     *
     * @param lastMove
     * @return
     */
    private PokerRound createMove(Move lastMove)
    {
        PokerRound gmove = PokerRound.createMove((lastMove==null)?0:lastMove.moveNumber+1);
        return gmove;
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    private int advanceToNextPlayerIndex()
    {
        currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.length;
        while (getPlayer(currentPlayerIndex_).hasFolded())
            currentPlayerIndex_ = (currentPlayerIndex_+1) % players_.length;

        return currentPlayerIndex_;
    }

    private PokerPlayer getPlayer(int index) {
        return (PokerPlayer) getPlayers()[index];
    }

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return players_[startingPlayerIndex_];
    }

    /**
     * @return  the players currently playing the game
     */
    public Player[] getPlayers()
    {
        return players_;
    }

    /**
     * @param players  the players currently playing the game
     */
    public void setPlayers( Player[] players )
    {
        players_ = players;
        // deal cards to the players
        System.out.println("set player dealcards");
        dealCardsToPlayers(5);
    }


    /**
     * @return index of current player that is to give orders
     */
    public int getCurrentPlayerIndex()
    {
        return currentPlayerIndex_;
    }



    /**
     *  Statically evaluate the board position
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        return lastMove.value;
    }

    /*
     * generate all possible next moves.
     * impossible for this game.
     */
    public List generateMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        LinkedList moveList = new LinkedList();
        return moveList;
    }

    /**
     * return any moves that result in a win
     */
    public List generateUrgentMoves( Move lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        return null;
    }

    /**
     * @param m
     * @param weights
     * @param player1sPerspective
     * @return true if the last move created a big change in the score
     */
    public boolean inJeopardy( Move m, ParameterArray weights, boolean player1sPerspective )
    {
        return false;
    }

}
