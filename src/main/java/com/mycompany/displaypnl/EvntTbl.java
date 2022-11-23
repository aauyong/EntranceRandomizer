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

public class EvntTbl extends JTable {
    private List<String[]> events;
    public EvntTbl() {
        this.model = new EvntTblModel();
        events = new ArrayList<>();

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


    public void addEvent(String evnt, String entr, String ext) {
        LocalTime time = LocalTime.now();
        time = time.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        String[] s = {time.toString(), evnt, entr, ext};
        this.model.insertRow(0, s);
        events.add(0, s);
    }

    public void deleteEvent(int row) {
        model.removeRow(row);
    }

    protected List<String[]> getEvents() {
        return events;
    }

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
}
