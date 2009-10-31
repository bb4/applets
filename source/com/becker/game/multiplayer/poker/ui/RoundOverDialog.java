package com.becker.game.multiplayer.poker.ui;

import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.OptionsDialog;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.PlayerLabel;
import com.becker.game.multiplayer.poker.player.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.NumberFormat;


/**
 * Show a summary of the final results.
 * The winner is the player with al the chips.
 *
 * @author Barry Becker
 */
public class RoundOverDialog extends OptionsDialog
{
    private GradientButton closeButton_;

    private PokerPlayer winner_;
    private int winnings_;
    private PlayerLabel playerLabel_;
    private JLabel winLabel_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public RoundOverDialog( JFrame parent, PokerPlayer winner, int winnings )
    {
        super( parent );
        winner_ = winner;
        winnings_ = winnings;
        showContent();
    }

    protected JComponent createDialogContent() {
        setResizable( true );
        JPanel mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                               BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JPanel buttonsPanel = createButtonsPanel();
        JPanel instructions = createInstructionsPanel();

        mainPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        playerLabel_ = new PlayerLabel();
        playerLabel_.setPlayer(winner_);

        winLabel_ = new JLabel();
        initWonMessage();

        //panel.setPreferredSize(new Dimension(400, 100));
        panel.add(playerLabel_, BorderLayout.NORTH);
        panel.add(winLabel_, BorderLayout.CENTER);
        //panel.add(amountToCall, BorderLayout.SOUTH);
        return panel;
    }

    private void initWonMessage() {
         NumberFormat cf = BettingDialog.getCurrencyFormat();
        String cash = cf.format(winnings_);
        winLabel_.setText("won " + cash + " from the pot!");
        //JLabel amountToCall = new JLabel("To call, you need to add " + cf.format(callAmount_));
    }

    @Override
    public String getTitle() {
       return "Round Over";
    }

    protected JPanel createButtonsPanel(){
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        closeButton_ = new GradientButton();
        initBottomButton( closeButton_, GameContext.getLabel("CLOSE"), GameContext.getLabel("CLOSE_TIP") );

        buttonsPanel.add( closeButton_ );
        return buttonsPanel;
    }


    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == closeButton_) {
            this.setVisible(false);
        }
    }

}

