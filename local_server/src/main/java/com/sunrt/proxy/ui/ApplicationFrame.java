package com.sunrt.proxy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class ApplicationFrame extends JFrame {
    public ApplicationFrame() {
        setTitle("proxy");
        setResizable(false);
        Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(dimension.getWidth()*0.3),(int)(dimension.getHeight()*0.3));
        setLocationByPlatform(true);
        setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("icon.png")).getImage());

        JComponent jComponent=new JComponent(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                /*Rectangle2D.Float rect=new Rectangle2D.Float();
                rect.setFrameFromDiagonal(0,0,100,100);
                g2.setBackground(Color.RED);
                g2.fill(rect);
                g2.draw(rect);*/

                FontRenderContext fontRenderContext=g2.getFontRenderContext();
                Font font=new Font("幼圆", Font.PLAIN, 18);
                g2.setFont(font);

                font.getLineMetrics("中文", fontRenderContext);


                Rectangle2D rectangle2D=font.getStringBounds("中文", fontRenderContext);
                rectangle2D.getWidth();
                rectangle2D.getHeight();
                double t=-rectangle2D.getY();

                g2.drawString("中文", 75,100 );
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300,200);
            }
        };
        jComponent.setOpaque(true);
        //jComponent.setBackground(Color.RED);
        //jComponent.setForeground(Color.white);



        add(jComponent);
        //pack();
        toFront();
        //setUndecorated(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ApplicationFrame applicationFrame=new ApplicationFrame();
            applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            applicationFrame.setVisible(true);
        });
    }
}
