package com.mycompany.displaypnl;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.TableCellRenderer;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * A JTable for storing and displaying Events as String[].
 * 
 * The table displays each event as a row, with the timestamp of the event, 
 * type of event, name of affected entrance, and name of affected exit. 
 * Additionally, the final column is a delete button to remove a specific 
 * events
 * 
 * Events are stored in a stack-like manner: Most recent events are stored
 * at the top of the table, whereas the oldest events are at the bottom.
 * 
 * Access is limited, most interfacing is done through the {@code DisplayPnl}.
 * 
 * Supports deleting and getting specific rows by index.
 * 
 * Utilizes a custom table model and custom cell editors and renderers to 
 * build the table buttons. 
 * @author aauyong
 */
public class EvntTbl extends JTable {
    public EvntTbl() {
        this.model = new EvntTblModel();
        this.events = new ArrayList<>();

        setModel(model);
        getColumn("Delete").setCellRenderer(new ButtonRenderer());
        getColumn("Delete").setCellEditor( new ButtonEditor(new JCheckBox()) );

        this.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        var w = getPreferredSize().width;
        getColumnModel().getColumn(0).setPreferredWidth(w/10);
        getColumnModel().getColumn(1).setPreferredWidth(w/10);
        getColumnModel().getColumn(2).setPreferredWidth(w/3);
        getColumnModel().getColumn(3).setPreferredWidth(w/3);
        getColumnModel().getColumn(4).setPreferredWidth(50);
    }

    /**
     * Adds an Event to the table.
     * 
     * An event is described as four Strings; A timestamp, an Event Type, 
     * the entrance name, and the exit name.
     * 
     * The timestamp is calculated and truncated down to seconds. All of these 
     * elements then intstantiate a string array that is inserted into the table at
     * index 0.
     * @param evntType
     * @param entrName
     * @param extName
     */
    public void addEvent(String evntType, String entrName, String extName) {
        LocalTime time = LocalTime.now();
        time = time.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        String[] s = {time.toString(), evntType, entrName, extName};
        this.model.insertRow(0, s);
        events.add(0, s);
    }

    /**
     * Delete event at a specific row
     * @param row
     */
    protected void deleteEvent(int row) {
        model.removeRow(row);
    }


    protected List<String[]> getEvents() {
        return events;
    }

    /**
     * Custom Table Model encapsulated in the {@code EvntTbl}.
     * 
     * Creates a five column table; Timestamp, Event, Entrance, Exit, and Delete.
     * 
     * Only allows the 5th column to be edited, the delete button.
     * 
     * @author aauyong
     */
    private class EvntTblModel extends javax.swing.table.DefaultTableModel {
        public EvntTblModel() {
            super(
                new Object [][] {},
                new String [] {
                    "Timestamp", "Event", "Entrance", "Exit", "Delete"
                }
            );
        }

        /**
         * Only let the Delete Button Column be editable/clickable
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4;
        }
    }

    /**
     * Custom JButton that is rendered as a table cell
     * 
     * 
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                // TODO figure out how to line up the background buttons with the
                // the appropriate background color
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    /**
     * Custom DefaultCellEditor with a JButton
     * 
     * Deletes rows of the table when the button is pressed
     */
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();

            button.setOpaque(true);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteEvent(currentRow);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            return button;
        }
    }

    private EvntTblModel model;

    private List<String[]> events;
}
