package com.mycompany.Connections;

import javax.swing.JComponent;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Point;

import com.mycompany.mapimagepnl.EntranceIcon;

public class Connection extends JComponent {
    private Line2D line;
    private EntranceIcon entr;
    private EntranceIcon ext;
    private Point entrDrawPt;
    private Point extDrawPt;

    private Color color;
    private BasicStroke stroke;

    public Connection(EntranceIcon a, EntranceIcon b) {
        this.line = new Line2D.Double();
        this.entr = a;
        this.ext = b;
        updateDrawPts();

        this.color = Color.RED;
        this.stroke = new BasicStroke(3);
        this.setVisible(true);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        this.line.setLine(this.entrDrawPt, this.extDrawPt);
        System.out.println("Painting");

        System.out.println(this.entrDrawPt);
        System.out.println(this.extDrawPt);
        g2.setPaint(this.color);
        g2.setStroke(stroke);
        g2.draw(this.line);
    }

    protected final void updateDrawPts() {
        System.out.println("Update %s - %s".formatted( this.entr.getEntrName(), this.ext.getEntrName()));
        Point translate;
        java.awt.Dimension d;
        this.entrDrawPt = this.entr.getLocation();

        translate = this.entr.getParent().getLocation();
        d = this.entr.getPreferredSize();
        this.entrDrawPt.translate(
            translate.x + d.width/2,
            translate.y + d.height/2
        );

        this.extDrawPt = this.ext.getLocation();
        System.out.println(extDrawPt);
        translate = this.ext.getParent().getLocation();
        d = this.ext.getPreferredSize();
        System.out.println(translate);

        this.extDrawPt.translate(
            translate.x + d.width/2,
            translate.y + d.height/2
        );
        System.out.println(extDrawPt);
        System.out.println();
    }

}
