package com.sunrt.proxy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class TextJPanel extends JPanel {
    public TextJPanel() {
        super();
        setBackground(Color.PINK);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        new ImageIcon("").getImage();

        g2.setColor(Color.RED);
        Font font= null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT,this.getClass().getClassLoader().getResourceAsStream("font.ttf")).deriveFont(20.0F);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2.setFont(font);
        String str="中华人民共和国";
        FontRenderContext fontRenderContext=g2.getFontRenderContext();;
        Rectangle2D rectangle2D=font.getStringBounds(str,fontRenderContext);
        System.out.println(getWidth());
        double x=(getWidth()-rectangle2D.getWidth())/2;
        double y=(getHeight()-rectangle2D.getHeight())/2;
        double ascent=-rectangle2D.getY();
        double baseY=y+ascent;

        g2.drawString(str, (int)x,(int)baseY);

    }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200,50);
        }
}
