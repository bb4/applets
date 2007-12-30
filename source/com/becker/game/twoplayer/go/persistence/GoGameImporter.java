package com.becker.game.twoplayer.go.persistence;

import com.becker.game.common.*;
import ca.dj.jigo.sgf.tokens.*;
import ca.dj.jigo.sgf.*;
import com.becker.game.common.persistence.GameImporter;
import com.becker.game.twoplayer.go.*;

import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 * Imports the stat of a Go game from a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public class GoGameImporter extends GameImporter {

    public GoGameImporter(GoController controller) {
        super(controller);
    }

    public void restoreFromFile( String fileName ) {

        try {
            FileInputStream iStream = new FileInputStream( fileName );
            GameContext.log( 2, "opening " + fileName );
            SGFLoader gameLoader = new SGFGoLoader();
            SGFGame game = gameLoader.load( iStream );
            restoreGame( game );
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog( null,
                                           "file " + fileName + " was not found." + fnfe.getMessage() );
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog( null,
                                           "IOException occurrred while reading " +
                                           fileName + " :" + ioe.getMessage() );
        } catch (SGFException sgfe) {
            JOptionPane.showMessageDialog( null,
                                           "file " + fileName + " had an SGF error while loading: " +
                                           sgfe.getMessage() );
            sgfe.printStackTrace();
        }
    }

    /**
     * Initialize the board based on the SGF game.
     */
    protected void parseSGFGameInfo( SGFGame game) {

        GoController gc = (GoController) controller_;

        Enumeration e = game.getInfoTokens();
        int size = 13; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof SizeToken) {
                SizeToken sizeToken = (SizeToken)token;
                size = sizeToken.getSize();
            }
            else if (token instanceof KomiToken) {
                KomiToken komiToken = (KomiToken) token;
                ((GoOptions)gc.getOptions()).setKomi(komiToken.getKomi());
            }
            // so we don't guess wrong on where the handicap positions are
            // we will rely on their being an AB (add black) command to specifically tell where the handicap stones are
            /*else if (token instanceof HandicapToken) {
                HandicapToken handicapToken = (HandicapToken) token;               
                GameContext.log(2,"***handicap ="+handicapToken.getHandicap());
                this.setHandicap(handicapToken.getHandicap());
            }*/
            else if (token instanceof WhiteNameToken) {
                WhiteNameToken nameToken = (WhiteNameToken) token;
                gc.getPlayer2().setName(nameToken.getName());
            }
            else if (token instanceof BlackNameToken) {
                BlackNameToken nameToken = (BlackNameToken) token;
                gc.getPlayer1().setName(nameToken.getName());
            }
            else if (token instanceof KomiToken) {
                KomiToken komiToken = (KomiToken) token;
                ((GoOptions)gc.getOptions()).setKomi(komiToken.getKomi());
            }
            else if (token instanceof RuleSetToken) {
                //RuleSetToken ruleToken = (RuleSetToken) token;
                //this.setRuleSet(ruleToken.getKomi());
            }
            else {
                GameContext.log(1, "Ignoring  token =" + token.getClass().getName() + " while parsing.");
            }
        }
        gc.getBoard().setSize(size, size);
    }


    protected boolean processToken(SGFToken token, List moveList) {

        boolean found = false;
        if (token instanceof MoveToken ) {
            moveList.add( createMoveFromToken( (MoveToken) token ) );
            found = true;
        }
        else if (token instanceof AddBlackToken ) {
            addMoves((PlacementListToken)token, moveList);
            found = true;
        }
        else if (token instanceof AddWhiteToken ) {
            addMoves((PlacementListToken)token, moveList);
            found = true;
        }
        /*
        else if (token instanceof CharsetToken ) {
            CharsetToken charsetToken = (CharsetToken) token;
        }
        else if (token instanceof OverTimeToken ) {
            OverTimeToken charsetToken = (OverTimeToken) token;
            System.out.println("charset="+charsetToken.getCharset());
        }
         */
        else if (token instanceof TextToken ) {
            TextToken textToken = (TextToken) token;
            //System.out.println("text="+textToken.getText());
        } else {
            GameContext.log(0, "Ignoring token "+token.getClass().getName() + " while processing.");
        }
        return found;
    }

    /**
     * add a sequence of moves all at once.
     * Such as placing handicaps when reading from an sgf file.
     * @param token
     */
    private static void addMoves(PlacementListToken token, List moveList) {
        Iterator<Point> points = token.getPoints();
        System.out.println("num points ="+token.getPoints2().size());
        boolean player1 = token instanceof AddBlackToken;
        
        while (points.hasNext()) {
            Point point = points.next();
            System.out.println("adding move at row=" + point.y+" col="+ point.x);
            moveList.add( new GoMove( point.y, point.x, 0, new GoStone(player1)));
        }
    }


    protected Move createMoveFromToken( SGFToken token)
    {        
          MoveToken mvToken = (MoveToken) token;
          if (mvToken.isPass()) {
              return GoMove.createPassMove(0, !mvToken.isWhite());
          }
          return new GoMove( mvToken.getY(), mvToken.getX(), 0, new GoStone(!mvToken.isWhite()));
    }

}
