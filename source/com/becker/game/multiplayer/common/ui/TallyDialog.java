package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;
import java.awt.AWTEvent;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;

/**
 * Show a summary of the final results.
 * We will show how stats about each player.
 * The winner is determined by some metric applied to these stats.
 *
 * @author Barry Becker
 */
public abstract class TallyDialog extends OptionsDialog
{
    private GameController controller_;

    private GradientButton okButton_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param controller pass in game controller.
     */
    public TallyDialog( Frame parent, GameController controller )
    {
        super( parent );
        controller_ = controller;

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        try {
            initUI();
        } catch (OutOfMemoryError oom) {
            GameContext.log( 0, "we ran out of memory!" );
            GameContext.log( 0, GUIUtil.getStackTrace( oom ) );
        }
        pack();
    }

    public String getTitle()
    {
        return GameContext.getLabel("TALLY_TITLE");
    }


    /**
     * ui initialization of the tree control.
     */
    protected void initUI()
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        List<? extends Player> players = controller_.getPlayers();
        String winningPlayer = findWinner(players);

        // show a label at the top with who the winner is
        JLabel winnerLabel = new JLabel();
        winnerLabel.setText("<html>" + GameContext.getLabel("GAME_OVER") + "<br>"
                            + GameContext.getLabel("WINNER_IS")+"<br>" + winningPlayer + "</html>");
        winnerLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        mainPanel.add(winnerLabel, BorderLayout.NORTH);

        SummaryTable summaryTable_= createSummaryTable(players);
        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        tablePanel.add(new JScrollPane(summaryTable_.getTable()), BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);


        this.getContentPane().add(mainPanel);
        //this.setPreferredSize(new Dimension(500,300));
    }

    protected abstract SummaryTable createSummaryTable(List<? extends Player> players);

    protected abstract String findWinner(List<? extends Player> players);


    /**
     *  create the OK Cancel buttons that go at the botton
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        //initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        //buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }


    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source == okButton_) {
            this.setVisible(false);
        }
    }


}

