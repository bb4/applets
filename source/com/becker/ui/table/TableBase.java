package com.becker.ui.table;

import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
 *
 * This represents a generic table, with a set of columns and tooltips for thos column headers.
 *
 * @author Barry Becker Date: Jan 13, 2007
 */
public abstract class TableBase
{
    protected JTable table_;

    // meta data information about each column and its header.
    protected TableColumnMeta[] columnMeta_;

    public TableBase() {

    };

    /**
     * Constructor
     */
    public TableBase(List rows, String[] columnNames) {

        initColumnMeta(columnNames);
        initializeTable(rows);
    }

    /**
     * constructor
     * @param rows to initializet the rows in the table with.
     */
    public TableBase(List rows, TableColumnMeta[] columnMeta)
    {
        columnMeta_ = columnMeta;
        initializeTable(rows);
    }

    protected void initColumnMeta(String[] columnNames) {
        TableColumnMeta[] columnMeta = new TableColumnMeta[columnNames.length];
        for (int i=0; i<columnNames.length; i++) {
            columnMeta[i] = new TableColumnMeta(columnNames[i], null);
        }
        columnMeta_ = columnMeta;
    }

    protected abstract void addRow(Object row);

    /**
     * @param rows initial data
     */
    protected void initializeTable(List rows)
    {
        String[] columnNames = new String[columnMeta_.length];
        for (int i=0; i<columnMeta_.length; i++) {
            columnNames[i] = columnMeta_[i].getName();
        }
        TableModel m = createTableModel(columnNames);
        table_ = new JTable(m);

        updateColumnMeta(columnMeta_);

        for (TableColumnMeta meta : columnMeta_) {
            meta.initializeColumn(table_);
        }

        table_.doLayout();

        if (rows != null) {
            for (Object p : rows) {
                addRow(p);
            }
        }
    }

    /**
     * override to assign specific tooltips, widths, rederers andneditors on a per column basis.
     * @param columnMeta
     */
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {
        // does nothgin by default
    }

    protected abstract TableModel createTableModel(String[] columnNames);

    public JTable getTable()
    {
        return table_;
    }

    public void addListSelectionListener(ListSelectionListener l)
    {
        table_.getSelectionModel().addListSelectionListener(l);
    }

    public TableModel getModel()
    {
        return table_.getModel();
    }

    protected void setRowHeight(int height) {
        table_.setRowHeight(height);
    }

    protected int getNumColumns() {
        return columnMeta_.length;
    }


    public int getNumRows() {
        return table_.getRowCount();
    }


}
