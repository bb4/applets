package com.becker.game.twoplayer.go.ui;

import com.becker.common.geometry.Location;
import com.becker.game.common.GameContext;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.ui.viewer.GameBoardViewer;
import com.becker.game.common.ui.viewer.ViewerMouseListener;
import com.becker.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.board.move.GoMove;
import com.becker.game.twoplayer.go.board.move.GoMoveGenerator;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 *  Mouse handling for Go game.
 *
 *  @author Barry Becker
 */
public class GoViewerMouseListener extends ViewerMouseListener {

    /** Remember the dragged show piece when the players mouse goes off the board. */
    private BoardPosition savedShowPiece_;

    /**
     * Constructor.
     */
    public GoViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);

        GoController controller = (GoController) viewer.getController();
        if (!controller.getPlayers().allPlayersComputer()) {
            getRenderer().setDraggedShowPiece(
                    new GoBoardPosition(0, 0, null, new GoStone(controller.isPlayer1sTurn())));
            savedShowPiece_ = getRenderer().getDraggedShowPiece();
        }
    }

    /**
     *  mouseClicked requires both the mouse down and mouse up event to occur at the same location.
     */
    @Override
    public void mousePressed( MouseEvent e ) {

        GoController controller = (GoController) viewer_.getController();
        // all derived classes must check this to disable user clicks while the computer is thinking
        if (controller.isProcessing()) {
            return;
        }
        Location loc = getRenderer().createLocation(e);
        GoBoard board = (GoBoard) controller.getBoard();
        boolean player1sTurn = controller.isPlayer1sTurn();

        GameContext.log( 3, "BoardViewer: mousePressed: player1sTurn()=" + player1sTurn);

        GoMove m = new GoMove( loc, 0, new GoStone(player1sTurn));

        // if there is already a piece where the user clicked, or its
        // out of bounds, or its a suicide move, then return without doing anything
        GoBoardPosition stone = (GoBoardPosition) board.getPosition( loc );

         // if stone is null, then the user clicked out of bounds.
        if ( stone != null ) {
            processStonePlacement(loc, m, stone);
        }  
    }

    /**
     * Place the stone on the board.
     */
    private void processStonePlacement(Location loc, GoMove m, GoBoardPosition stone) {

        GoController controller = (GoController) viewer_.getController();
        GoBoard board = (GoBoard) controller.getBoard();
        boolean player1sTurn = controller.isPlayer1sTurn();

        if ( stone.isOccupied() ) {
            JOptionPane.showMessageDialog( null, GameContext.getLabel("CANT_PLAY_ON_STONE") );
            GameContext.log( 0, "BoardViewer: There is already a stone there: " + stone );
            return;
        }
        if ( GoMoveGenerator.isTakeBack(m.getToRow(), m.getToCol(), (GoMove) controller.getLastMove(), board) ) {
            JOptionPane.showMessageDialog( null, GameContext.getLabel("NO_TAKEBACKS"));
            return;
        }
        assert(!stone.isVisited());

        if (m.isSuicidal(board)) {
            JOptionPane.showMessageDialog( null, GameContext.getLabel("SUICIDAL") );
            GameContext.log( 1, "BoardViewer: That move is suicidal (and hence illegal): " + stone );
            return;
        }

        if ( !((AbstractTwoPlayerBoardViewer)viewer_).continuePlay( m ) ) {   // then game over
            getRenderer().setDraggedShowPiece(null);
        }
        else if (controller.getPlayers().allPlayersHuman()) {
            // create a stone to show for the next players move
            getRenderer().setDraggedShowPiece(
                    new GoBoardPosition(loc.getRow(), loc.getCol(), null, new GoStone(!player1sTurn)));
        }
    }

    /**
     * if we are in wallPlacingMode, then we show the wall being dragged around.
     * When the player clicks the wall is irrevocably placed.
     */
    @Override
    public void mouseMoved( MouseEvent e )
    {
        GoController controller = (GoController) viewer_.getController();
        if (controller.isProcessing()) {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        if ( getRenderer().getDraggedShowPiece() != null ) {
            getRenderer().getDraggedShowPiece().setLocation( loc );
        }
        viewer_.repaint();
    }

    @Override
    public void mouseEntered( MouseEvent e ) {
        getRenderer().setDraggedShowPiece(savedShowPiece_);
    }

    @Override
    public void mouseExited( MouseEvent e ) {
        getRenderer().setDraggedShowPiece(null);
    }
}