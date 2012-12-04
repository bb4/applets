// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.online.ui;

import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.online.OnlineGameTableList;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGameOptions;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.barrybecker4.ui.table.BasicTableModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Iterator;

/**
 * Manage multi-player online game tables.
 * Shows a list of the currently active "tables" to the user in a table.
 * Used only on the client to show list of active game tables.
 *
 * @author Barry Becker
 */
class MultiPlayerTableManager {

    private String currentName_;
    private GameController controller_;
    private MultiPlayerOnlineGameTablesTable onlineGameTablesTable_;

    /** typically the dialog that we live in. Called when table ready to play.   */
    protected ChangeListener gameStartedListener_;


    /**
     * Constructor
     */
    public MultiPlayerTableManager(GameController controller,
            MultiPlayerOnlineGameTablesTable onlineGameTablesTable, ChangeListener startedListener) {
        controller_ = controller;
        onlineGameTablesTable_ = onlineGameTablesTable;
        gameStartedListener_ = startedListener;
    }

    public void setCurrentName(String name) {
        assert name != null;
        currentName_ = name;
    }

    /**
     * The server has sent out a message to all the clients.
     * @param cmd the command to handle.
     */
    public void handleServerUpdate(GameCommand cmd) {

        if (onlineGameTablesTable_ == null) {
            return; // not initialized yet.
        }

        //System.out.println("got an update of the multi-player table list from the server:\n" + cmd);
        switch (cmd.getName())  {
            case UPDATE_TABLES :
                updateTables( (OnlineGameTableList) cmd.getArgument());
                break;
           case START_GAME :
                startGame();
                break;
           case CHAT_MESSAGE : break;
           case DO_ACTION: break;
           default : assert false : "Unexpected command name :"+ cmd.getName();
        }
    }

    /**
     * @param tableList list of tables to update.
     */
    void updateTables(OnlineGameTableList tableList) {
        onlineGameTablesTable_.removeAllRows();
        if (currentName_ == null) return;

        for (OnlineGameTable table : tableList) {
            onlineGameTablesTable_.addRow(table, table.hasPlayer(currentName_));
        }

        // see if the table that the player is at is ready to start playing.
        OnlineGameTable readyTable = tableList.getTableReadyToPlay(currentName_);
        if (readyTable != null) {
            // then the table the player is sitting at is ready to begin play.
            JOptionPane.showMessageDialog(null,
                      "All the players required \n(" + readyTable.getPlayersString()
                      + ")\n have joined this table. Play will now begin. ",
                      "Ready to Start", JOptionPane.INFORMATION_MESSAGE);
            // close the dlg and tell the server to start a thread to play the game

            // send an event to close the new player window. (perhaps we could know its a dialog and close it directly?)
            ChangeEvent event = new ChangeEvent(this);
            gameStartedListener_.stateChanged(event);

            //GameCommand startCmd = new GameCommand(GameCommand.Name.START_GAME, readyTable);
            //controller_.getServerConnection().sendCommand(startCmd);
        }
    }

    /**
     * An online table has filled with players and is ready to start.
     * Initialize the players for the controller with surrogates for all but the single current player on this client.
     */
    void startGame() {
        System.out.println("Start the game for player:" + currentName_
                + " on the client. Table=" +  onlineGameTablesTable_.getSelectedTable());

        // since we are on the client we need to create surrogates for the players which are not the current player
        Iterator<Player> it = onlineGameTablesTable_.getSelectedTable().getPlayers().iterator();
        PlayerList players = new PlayerList();
        while (it.hasNext()) {
            MultiGamePlayer player = (MultiGamePlayer)it.next();
            if (!player.getName().equals(this.currentName_)) {
                // add surrogate
                players.add(new SurrogateMultiPlayer(player, controller_.getServerConnection()));
            }
            else {
                players.add(player);
            }
        }
        System.out.println("starting game with players="+players);
        controller_.setPlayers(players);
    }

    /**
     * The create new table button at the top was clicked.
     */
    public void createNewGameTable(MultiGameOptions options) {

        OnlineGameTable newTable =
            onlineGameTablesTable_.createOnlineTable(currentName_, options);

        // now add it to this list as a new row and tell the server to add it.
        // onlineGameTablesTable_.addRow(newTable);
        controller_.getServerConnection().addGameTable(newTable);
    }

    /**
     * The local user has clicked  a join button on a different table
     * indicating that they want to join that table.
     */
    public void joinDifferentTable(int joinRow) {

        System.out.println("in join different table. row="+ joinRow);
        BasicTableModel m = onlineGameTablesTable_.getPlayerModel();

        for (int i=0; i < m.getRowCount(); i++) {
            // you can join tables other than the one you are at as long as they are not already playing.
            boolean enableJoin = (i != joinRow) && !onlineGameTablesTable_.getGameTable(i).isReadyToPlay();
            m.setValueAt(enableJoin, i, 0);
        }
        controller_.getServerConnection().joinTable(
                  onlineGameTablesTable_.createPlayerForName(currentName_),
                  onlineGameTablesTable_.getGameTable(joinRow));

        onlineGameTablesTable_.getTable().removeEditor();
    }

}
