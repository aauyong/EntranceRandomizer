package com.mycompany.Connections;

import javax.swing.JPanel;

public class ConnectionPnl extends JPanel {
    public ConnectionPnl() {
        this.setLayout(null);
        setVisible(true);
        setOpaque(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateConnections();
            }
        });
    }
    
    private void updateConnections() {
        for (var c : getComponents() ) {
            var connect = (Connection) c;
            connect.updateDrawPts();
        }
        repaint();
    }
            
    public void addConnection(Connection c) {
        add(c);
        c.setBounds(0,0,getParent().getWidth(),getParent().getHeight());
    }

}
