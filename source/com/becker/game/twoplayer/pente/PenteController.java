package com.becker.game.twoplayer.pente;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.common.Move;
import com.becker.optimization.ParameterArray;

import java.util.*;

/**
 * Defines everything the computer needs to know to play Pente
 *
 * @author Barry Becker
 */
public class PenteController extends TwoPlayerController
{

    // This is how many in a row are needed to win
    // if M is five, then the game is pente
    private static final int M = PentePatterns.M;

    // these weights determine how the computer values each pattern
    // if only one computer is playing, then only one of the weights arrays is used.

    // these weights determine how the computer values features of the board
    // if only one computer is playing, then only one of the weights arrays is used.
    // use these weights if no others are provided
    private static final double[] DEFAULT_WEIGHTS = {0.0,   0.0,  0.0,  0.0,  2.0,  5.0,  30.0, 31.0,   140.0,  1400.0,  1400.0,  1400.0};
    // don't allow the weights to exceed these maximum values
    private static final double[] MAX_WEIGHTS = {5.0, 5.0, 5.0, 10.0, 20.0, 20.0, 100.0, 100.0, 1000.0, 10000.0, 10000.0, 20000.0};
    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "1a weight", "1b weight", "1c weight", "2a weight",
        "2b weight", "3a weight", "3b weight", "4a  weight",
        "4b weight", "5 weight", "6 weight", "7 weight"};
    private static final String[] WEIGHT_DESCRIPTIONS = {
        "1a in a row weight", "1b in a row weight", "options with 1c in a row weight", "options 2a in a row weight",
        "options of 2b in a row weight", "open ended 3a in a row weight", "open ended 3b in a row weight", "4a in a row weight",
        "open ended 4b in a row (with options) weight", "arrangements of 5 in a row weight", "arrangements of 6 in a row weight",
        "arrangements of 7 in a row weight"
    };

    public static final char REGULAR_PIECE = GamePiece.REGULAR_PIECE;

    // for any given ply never consider more that BEST_PERCENTAGE of the top moves
    private static final int BEST_PERCENTAGE = 60;

    private static final int DEFAULT_NUM_ROWS = 20;
    protected static final int DEFAULT_NUM_COLS = 20;

    private static final char P1_SYMB = 'X';
    private static final char P2_SYMB = 'O';

    /**
     *  Construct the Pente game controller
     */
    public PenteController()
    {
        initializeData();
        board_ = new PenteBoard( DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS );
    }

    /**
     *  Construct the Pente game controller given an initial board size
     */
    public PenteController(int nrows, int ncols )
    {
        initializeData();
        board_ = new PenteBoard( nrows, ncols );
    }

    /**
     * @return the number of consecutive pieces in a row needed to constitute a win.
     */
    public static int getNInARow()
    {
        return M;
    }

    protected int getDefaultBestPercentage()
    {
        return BEST_PERCENTAGE;
    }

    /**
     *  this gets the pente specific patterns and weights
     */
    protected void initializeData()
    {
        weights_ = new GameWeights( DEFAULT_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
        PentePatterns.initialize();
    }

    /**
     * the first move of the game (made by the computer)
     */
    public void computerMovesFirst()
    {
        int delta = M - 1;
        int c = (int) (Math.random() * (board_.getNumCols() - 2 * delta) + delta + 1);
        int r = (int) (Math.random() * (board_.getNumRows() - 2 * delta) + delta + 1);
        TwoPlayerMove m = TwoPlayerMove.createMove( r, c, 0, 1, new GamePiece(true) );
        board_.makeMove( m );
        moveList_.add( m );
        player1sTurn_ = false;
    }

    /**
     *
     * @param line  the line to evaluate
     * @param symb  the current players symbol
     * @param opponent   symbol for the opponents's piece
     * @param pos
     * @param minpos
     * @param maxpos
     * @param weights
     * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
     */
    private double evalLine( StringBuffer line, char symb, char opponent,
                          int pos, int minpos, int maxpos, ParameterArray weights )
    {
        int len = maxpos - minpos;
        int ct = pos;
        if ( len < 3 )
            return 0; // not an interesting pattern.

        if ( (line.charAt( pos ) == opponent)
                && !(pos == minpos) && !(pos == maxpos - 1) ) {
            // first check for a special case where there was a blocking move in the
            // middle. In this case we break the string into an upper and lower
            // half and evaluate each separately.
            //StringBuffer low = new StringBuffer(line.substring(0,pos+1));
            //StringBuffer high = new StringBuffer(line.substring(pos,len));
            return (evalLine( line, symb, opponent, pos, minpos, pos + 1, weights )
                    + evalLine( line, symb, opponent, pos, pos, maxpos, weights ));

        }
        // In general we march from position in the middle towards the ends of the
        // string. Marching stops when we encounter one of the following
        // conditions:
        //  - 2 blanks in a row (@@ we may want to allow this)
        //  - an opponents blocking piece
        //  - the end of a line.
        if ( (line.charAt( pos ) == opponent) && (pos == minpos) )
            ct++;
        else
            while ( ct > minpos && (line.charAt( ct - 1 ) != opponent)
                    && !(line.charAt( ct ) == PentePatterns.UNOCCUPIED && line.charAt( ct - 1 ) == PentePatterns.UNOCCUPIED) )
                ct--;
        int start = ct;
        ct = pos;
        if ( (line.charAt( pos ) == opponent) && (pos == maxpos - 1) )
            ct--;
        else
            while ( ct < (maxpos - 1) && (line.charAt( ct + 1 ) != opponent)
                    && !(line.charAt( ct ) == PentePatterns.UNOCCUPIED && line.charAt( ct + 1 ) == PentePatterns.UNOCCUPIED) )
                ct++;
        int stop = ct;
        int inthash = PentePatterns.convertPatternToInt( line, start, stop + 1 );
        int index = PentePatterns.weightIndexTable_[inthash];

        if ( symb == P1_SYMB )
            return weights.get(index).value;
        else
            return -weights.get(index).value;
    }

    /**
     *  @return the difference in worth after making a move campared with before.
     *  We need to look at it from the point of view of both sides (p1 = +, p2 = -)
     */
    private double computeValueDifference( StringBuffer line, int position, ParameterArray weights )
    {
        char opponent = P2_SYMB;
        char symb = line.charAt( position ); // the last move made
        if ( symb == P2_SYMB )
            opponent = P1_SYMB;

        int len = line.length();
        if ( len < 3 )
            return 0; // not an interesting pattern.

        double newScore = evalLine( line, symb, opponent, position, 0, len, weights );
        newScore += evalLine( line, opponent, symb, position, 0, len, weights );

        line.setCharAt( position, PentePatterns.UNOCCUPIED );

        double oldScore = evalLine( line, symb, opponent, position, 0, len, weights );
        oldScore += evalLine( line, opponent, symb, position, 0, len, weights );

        return newScore - oldScore;
    }

    // debugging aid
    protected static void worthDebug( char c, StringBuffer line, int pos, int diff )
    {
        GameContext.log( 2,
                "Direction: " + c + " " + line + "Pos: " + pos + "  difference:" + diff );
    }

    private static void lineAppend( BoardPosition pos, StringBuffer line )
    {
        assert (pos!=null): "pos "+pos+" was null!";
        if ( pos.getPiece() == null )
            line.append( PentePatterns.UNOCCUPIED );
        else if ( pos.getPiece().isOwnedByPlayer1() )
            line.append( P1_SYMB );
        else
            line.append( P2_SYMB );
    }

    /**
     *  Statically evaluate the board position
     *  @return the lastMoves value modified by the value add of the new move.
     *   a large positive value means that the move is good from the specified players viewpoint
     */
    protected double worth( Move lastMove, ParameterArray weights )
    {
        int startc, startr, stopc, stopr, position;
        int i;
        double diff;
        TwoPlayerMove lMove = (TwoPlayerMove)lastMove;
        int row = lMove.getToRow();
        int col = lMove.getToCol();
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        StringBuffer line = new StringBuffer( "" );

        // look at every string that passes through this new move
        // to see how the value is affected.
        // there are 4 directions: - | \ /

        startc = col - M;   //  -
        if ( startc < 1 ) startc = 1;
        stopc = col + M;
        if ( stopc > numCols ) stopc = numCols;
        for ( i = startc; i <= stopc; i++ )
            lineAppend( board_.getPosition( row, i ), line );

        position = col - startc;
        diff = computeValueDifference( line, position, weights );
        //worthDebug('-', line, position, diff);

        startr = row - M;      //  |
        if ( startr < 1 ) startr = 1;
        stopr = row + M;
        if ( stopr > numRows ) stopr = numRows;
        line.setLength( 0 );
        for ( i = startr; i <= stopr; i++ )
            lineAppend( board_.getPosition( i, col ), line );

        position = row - startr;
        diff += computeValueDifference( line, position, weights );
        //worthDebug('|', line, position, diff);

        startc = col - M;      //  \
        startr = row - M;
        if ( startc < 1 ) {
            startr = startr + 1 - startc;
            startc = 1;
        }
        if ( startr < 1 ) {
            startc = startc + 1 - startr;
            startr = 1;
        }
        stopc = col + M;
        stopr = row + M;
        if ( stopc > numCols ) {
            stopr = stopr + numCols - stopc;
            stopc = numCols;
        }
        if ( stopr > numRows ) {
            stopc = stopc + numRows - stopr;
            stopr = numRows;
        }
        line.setLength( 0 );
        for ( i = startr; i <= stopr; i++ )
            lineAppend( board_.getPosition( i, startc + i - startr ), line );

        position = row - startr;
        diff += computeValueDifference( line, position, weights );
        //worthDebug('\\', line, position, diff);

        startc = col - M;     //  /
        startr = row + M;
        if ( startc < 1 ) {
            startr = startr + startc - 1;
            startc = 1;
        }
        if ( startr > numRows ) {
            startc = startc - numRows + startr;
            startr = numRows;
        }
        stopc = col + M;
        stopr = row - M;
        if ( stopc > numCols ) {
            stopr = stopr - numCols + stopc;
            stopc = numCols;
        }
        if ( stopr < 1 ) {
            stopc = stopc + stopr - 1;
            stopr = 1;
        }
        line.setLength( 0 );
        for ( i = startc; i <= stopc; i++ )
            lineAppend( board_.getPosition( startr - i + startc, i ), line );

        position = col - startc;
        diff += computeValueDifference( line, position, weights );
        //lastMove.difference = diff;
        //worthDebug('/', line, position, diff);

        return lastMove.value + diff;
    }

    /*
     * generate all possible next moves
     */
    public List generateMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        LinkedList moveList = new LinkedList();
        int i,j;
        int Ncols = board_.getNumCols();
        int Nrows = board_.getNumRows();

        PenteBoard pb = (PenteBoard) board_;
        pb.determineCandidateMoves();

        boolean player1 = !(lastMove.player1);
        int moveNum = lastMove.moveNumber + 1;

        for ( i = 1; i <= Ncols; i++ )      //cols
            for ( j = 1; j <= Nrows; j++ )    //rows
                if ( pb.isCandidateMove( j, i ) ) {
                    //System.out.println("adding "+lastMove.value);
                    TwoPlayerMove m = TwoPlayerMove.createMove( j, i, lastMove.value, moveNum,
                                              new GamePiece(player1));
                    pb.makeMove( m );
                    m.value = worth( m, weights, player1sPerspective );
                    // now revert the board
                    pb.undoMove( m );
                    moveList.add( m );
                }

        return getBestMoves( player1, moveList, player1sPerspective );
    }

    /**
     * return any moves that result in a win
     */
    public List generateUrgentMoves( TwoPlayerMove lastMove, ParameterArray weights, boolean player1sPerspective )
    {
        List moves = generateMoves( lastMove, weights, player1sPerspective );

        // now keep only those that result in a win.
        Iterator it = moves.iterator();
        while ( it.hasNext() ) {
            TwoPlayerMove move = (TwoPlayerMove) it.next();
            if ( Math.abs( move.inheritedValue ) < WINNING_VALUE )
                it.remove();
            else
                move.urgent = true;
        }
        // ( moves.size() > 0 )
        //    GameContext.log( 0, "pente controller: the urgent moves are :" + moves );
        return moves;
    }

    /**
     * @param m
     * @param weights
     * @param player1sPerspective
     * @return true if the last move created a big change in the score
     */
    public boolean inJeopardy( Move m, ParameterArray weights, boolean player1sPerspective )
    {
        // consider the delta big if >= w. Where w is the value of a near win.
        double w = weights.get(8).value;

        double newValue = worth( m, weights, player1sPerspective );
        double diff = newValue - m.value;

        return (diff > w);
    }
}
