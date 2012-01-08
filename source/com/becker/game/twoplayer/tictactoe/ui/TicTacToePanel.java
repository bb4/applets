/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.tictactoe.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.dialogs.NewGameDialog;
import com.becker.game.common.ui.panel.GameInfoPanel;
import com.becker.game.common.ui.viewer.GameBoardViewer;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.becker.game.twoplayer.pente.ui.PentePanel;

import javax.swing.*;
import java.awt.*;

/**
 *  This class defines the main UI for the TicTacToe game applet or application.
 *
 *  @author Barry Becker
 */
public class TicTacToePanel extends PentePanel {

    /**
     *  Construct the panel.
     */
    public TicTacToePanel() {}


    @Override
    public String getTitle() {
        return  GameContext.getLabel("TICTACTOE_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new TicTacToeBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewable viewer ) {
        return new TicTacToeNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TwoPlayerInfoPanel( controller );  
    }

    /**
     * Display the help dialog to give instructions
     */
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("TICTACTOE_TITLE");
        String overview = GameContext.getLabel("TICTACTOE_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }
}



