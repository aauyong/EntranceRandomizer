/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.entrancerando;

import com.mycompany.mapimagepnl.MapTracker;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.mycompany.displaypnl.DisplayPnl;
import com.mycompany.eventhandling.EventHandler;

/**
 *
 * @author blarg
 */
public class EntranceWindow extends javax.swing.JFrame {

    /**
     * Creates new form EntranceWindow
     */
    public EntranceWindow() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        dispPnl = new DisplayPnl();
        mapTrckr = new MapTracker();
        evntHndlr = new EventHandler(dispPnl, mapTrckr);

        // /**
        getContentPane().setLayout(new java.awt.GridBagLayout());
        getContentPane().add(dispPnl, new java.awt.GridBagConstraints() {{
            anchor = java.awt.GridBagConstraints.NORTH;
            fill = java.awt.GridBagConstraints.BOTH;
            gridy = 1;
            weightx = 1;
            weighty = 1;
        }});
        getContentPane().add(mapTrckr, new java.awt.GridBagConstraints() {{
            weighty = 1;
            weightx = 1;
        }});
        //  */

        mapTrckr.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        dispPnl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));

        initDispPnlListeners();
        initMapPnlListeners();


        mapTrckr.setOtherMinSize(dispPnl.getPreferredSize());
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initDispPnlListeners() {
        // Update the Event Handler whenever the Event Table is changed
        dispPnl.getTblModel().addTableModelListener( new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent tblEvt) {
                if (tblEvt.getType() == TableModelEvent.DELETE) {
                    var r = tblEvt.getFirstRow();
                    evntHndlr.removeEvent(dispPnl.getAlteredRowNum(r));
                }
            }
        });
        
        dispPnl.getUselessButton().addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mapTrckr.isHidingUseless(
                        dispPnl.getUselessButton().isSelected()
                    );
                }
            }
        );
    }

    private void initMapPnlListeners() {
        // Add mouse listener to each Entrance Icon
        for (var e : mapTrckr.getEntrances().values()) {
            e.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent mouseEvt) {
                    evntHndlr.tryAddEvent(mapTrckr.entranceIconClicked(e, mouseEvt));
                }
            });
        }

        // Add resizer listener for mapPnl to update
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mapTrckr.setOtherMinSize(dispPnl.getPreferredSize());
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DisplayPnl dispPnl;
    private MapTracker mapTrckr;
    private EventHandler evntHndlr;
    // End of variables declaration//GEN-END:variables
}