package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.Player;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Barry Becker
 */
public class PlayerLabel extends JPanel {

    JPanel swatch;
    JLabel playerLabel;

    public PlayerLabel() {
        swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(10, 10));
        playerLabel = new JLabel();
        add(swatch);
        add(playerLabel);
    }

    public void setPlayer(Player player) {
        swatch.setBackground(player.getColor());
        playerLabel.setText(player.getName());
    }
}
