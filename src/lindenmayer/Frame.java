package lindenmayer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Frame extends JFrame {

    public List<Line2D.Double> lines;
    public Rectangle2D rectangle2D;

    public Frame(List<Line2D.Double> lines,
                 Rectangle2D rectangle2D) {
        super("Lines Drawing Demo");

        this.lines = lines;
        this.rectangle2D = rectangle2D;
        setSize(1000,1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }


    void drawLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for(Line2D.Double line : lines){
                g2d.drawLine((int) Math.round(line.getX1()), (int) Math.round(line.getY1()),
                        (int) Math.round(line.getX2()), (int) Math.round(line.getY2()));
        }

    }

    public void paint(Graphics g) {
        super.paint(g);
        drawLines(g);
    }

}
