package com.becker.puzzle.adventure.ui.editor;

import com.becker.puzzle.adventure.Choice;
import com.becker.puzzle.adventure.ChoiceList;
import com.becker.ui.table.*;

import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;


/**
 * Shows a list of the child scenes, allows editing the navigation text,
 * and allows navigating to them.
 *
 * @author Barry Becker
 */
class ChildTable extends TableBase  {

    public static final String NEW_CHOICE_DESC_LABEL = " - Put your choice description here -";

    public static final String NAVIGATE_TO_CHILD_BUTTON_ID = "navToChild";

    static final int NAVIGATE_INDEX = 0;
    static final int CHOICE_DESCRIPTION_INDEX = 1;

    private static final String NAVIGATE = "Navigate to";
    private static final String CHOICE_DESCRIPTION = "Choice Description";


    private static final String[] CHILD_COLUMN_NAMES =  {
         NAVIGATE,
         CHOICE_DESCRIPTION
    };

    private TableButtonListener tableButtonListener_;

    /**
     * Constructor
     */
    public ChildTable(ChoiceList choices, TableButtonListener listener)
    {
        initColumnMeta(CHILD_COLUMN_NAMES);
        tableButtonListener_ = listener;
        initializeTable(choices);
        getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public int moveRow(int oldRow, int newRow) {
        if (newRow >= 0 && newRow < getChildTableModel().getRowCount()) {
            getChildTableModel().moveRow(oldRow, oldRow, newRow);
            getTable().setRowSelectionInterval(newRow, newRow);
            System.out.println("new row selected after move="+ newRow +"(oldrow="+ oldRow +")");
            return newRow;
        }
        return oldRow;
    }

    /**
     * Add a row based on a player object.
     */
    @Override
    protected void addRow(Object choice)
    {
        Choice childChoice = (Choice) choice;
        Object d[] = new Object[getNumColumns()];
        d[NAVIGATE_INDEX] = childChoice.getDestination();
        d[CHOICE_DESCRIPTION_INDEX] = childChoice.getDescription();
        getChildTableModel().addRow(d);
    }

    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta navigateCol = columnMeta[NAVIGATE_INDEX];

        TableButton navCellEditor = new TableButton(NAVIGATE_INDEX, NAVIGATE_TO_CHILD_BUTTON_ID);

        navCellEditor.addTableButtonListener(tableButtonListener_);
        navCellEditor.setToolTipText("navigate to this scene");
        navigateCol.setCellRenderer(navCellEditor);
        navigateCol.setCellEditor(navCellEditor);
        navigateCol.setPreferredWidth(200);
        navigateCol.setMaxWidth(400);

        columnMeta[CHOICE_DESCRIPTION_INDEX].setPreferredWidth(500);
    }


    @Override
    protected TableModel createTableModel(String[] columnNames)  {
        return new ChildTableModel(columnNames, 0);
    }

    ChildTableModel getChildTableModel()
    {
        return (ChildTableModel)table_.getModel();
    }
}
