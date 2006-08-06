package com.becker.puzzle.redpuzzle;

import java.util.*;

/**
 * The pieces that sre in the red puzzle.
 * In no particular order.
 * model in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public class PieceList {

    /** the real game has 9 pieces, but I might experiment with 4 or 16 for testing. */
    private static final int DEFUALT_NUM_PIECES = 9;
    private int maxNumPieces_ = DEFUALT_NUM_PIECES;

    // use the same seed for repeatable results.
    // 0 solves in 25,797 tries.
    // 1 solves in  7,275
    // 2 solves in 12,349
    // 3 solves in  8,187
    // 4 solves in 14,150
    // 5 solves in  1,157
    private static final Random R = new Random(5);

    private List<Piece> pieces_;

    /** this defines the puzzle pieces for the standard 9x9 puzzle. */
    private static final Piece[] INITIAL_PIECES_9 = {
        new Piece( Nub.OUTY_SPADE,  Nub.OUTY_DIAMOND,  Nub.INNY_HEART, Nub.INNY_DIAMOND, 1),  // 0
        new Piece( Nub.OUTY_CLUB,  Nub.OUTY_HEART,  Nub.INNY_DIAMOND, Nub.INNY_CLUB, 2),     // 1
        new Piece( Nub.OUTY_HEART,  Nub.OUTY_SPADE,  Nub.INNY_SPADE, Nub.INNY_CLUB, 3),     // 2
        new Piece( Nub.OUTY_CLUB,  Nub.OUTY_HEART,  Nub.INNY_SPADE, Nub.INNY_HEART, 4),    // 3
        new Piece( Nub.INNY_SPADE,  Nub.INNY_HEART,    Nub.OUTY_SPADE,   Nub.OUTY_DIAMOND, 5),
        new Piece( Nub.OUTY_HEART,  Nub.OUTY_DIAMOND,  Nub.INNY_DIAMOND, Nub.INNY_HEART, 6),
        new Piece( Nub.OUTY_HEART,  Nub.OUTY_DIAMOND,  Nub.INNY_CLUB, Nub.INNY_CLUB, 7),
        new Piece( Nub.OUTY_DIAMOND,  Nub.OUTY_CLUB,  Nub.INNY_CLUB, Nub.INNY_DIAMOND, 8),
        new Piece( Nub.OUTY_SPADE,  Nub.OUTY_SPADE,  Nub.INNY_HEART, Nub.INNY_CLUB, 9),
     };

    /** this defines the puzzle pieces for the standard 9x9 puzzle. */
    private static final Piece[] INITIAL_PIECES_4 = {
         INITIAL_PIECES_9[0], INITIAL_PIECES_9[1], INITIAL_PIECES_9[2], INITIAL_PIECES_9[3]
     };


    /**
     * a list of 9 puzzle pieces.
     */
    public PieceList() {

        this(DEFUALT_NUM_PIECES);
    }

    /**
     * a list of puzzle pieces.
     */
    public PieceList(int numPieces) {

        maxNumPieces_ = numPieces;
        assert(numPieces==4 || numPieces == 9);

        pieces_ = new LinkedList<Piece>();
    }

    public static PieceList getInitialPuzlePieces() {
        return getInitialPuzlePieces(DEFUALT_NUM_PIECES);
    }

    /**
     * Factory method for creating the initial puzzle pieces.
     * @return the initial 9 pieces (in random order) to use when solving.
     */
    public static PieceList getInitialPuzlePieces(int numPieces) {

        PieceList pieces = new PieceList();
        Piece[] initialPieces = null;
        switch (numPieces) {
            case 4 : initialPieces = INITIAL_PIECES_4; break;
            case 9 : initialPieces = INITIAL_PIECES_9; break;
            default: assert false;
        }
        for (Piece p : initialPieces)  {
           pieces.add(p);
        }

        // shuffle the pieces so we get difference solutions -
        // or at least different approaches to the solution if there is only one.
        pieces.shuffle();

        return pieces;
    }

    /**
     *
     * @param i the index of the piece to get.
     * @return the i'th piece.
     */
    public Piece get(int i)  {
        assert i < maxNumPieces_ : "there are only " + maxNumPieces_ + " pieces.";

        return pieces_.get(i);
    }

    /**
     * @return the last piece added.
     */
    public Piece getLast()  {

        return pieces_.get(pieces_.size() - 1);
    }

    /**
     * @param p piece to add to the end of the list.
     */
    public void add(Piece p) {
        pieces_.add(p);
        assert pieces_.size() <= maxNumPieces_ :
                "there can only be at most " + maxNumPieces_ + " pieces.";
    }

    /**
      * @param p piece to add to the end of the list.
      */
     public void add(int i, Piece p) {
        pieces_.add(i, p);
        assert pieces_.size() <= maxNumPieces_ :
                "there can only be at most " + maxNumPieces_ + " pieces.";
    }

    /**
     * @param p the piece to remove.
     * @return true if the list contained this element.
     */
    public boolean remove(Piece p) {
        return pieces_.remove(p);
    }

    public Piece removeLast() {
        Piece p = pieces_.get(pieces_.size() - 1);
        pieces_.remove( p );
        return p;
    }

    public void shuffle() {
        Collections.shuffle(pieces_, R);
    }

    /**
     * @return the number of pieces in the list.
     */
    public int size() {
        return pieces_.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PieceList: ("+size()+" pieces)\n");
        for (Piece p : pieces_) {
            buf.append(p.toString() + '\n');
        }
        return buf.toString();
    }

}
