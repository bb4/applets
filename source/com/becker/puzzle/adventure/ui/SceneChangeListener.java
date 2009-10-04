package com.becker.puzzle.adventure.ui;


/**
 * Dispatched when you advance to a different scene of the story.
 * @author Barry Becker Date: Jul 16, 2006
 */
public interface SceneChangeListener {

    /**
     * @param selectionIndex the selected choice leading to the next scene in the story.
     */
    void sceneChanged(int selectionIndex);
}
