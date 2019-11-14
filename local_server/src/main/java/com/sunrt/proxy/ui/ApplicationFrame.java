package com.sunrt.proxy.ui;

import javax.swing.*;
import java.awt.*;

public class ApplicationFrame extends JFrame {
    public ApplicationFrame() {
        setTitle("proxy");
        //setResizable(false);
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(dimension.getWidth()*0.3),(int)(dimension.getHeight()*0.3));
        setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("icon.png")).getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagConstraints gridBagConstraints=new GridBagConstraints();
        gridBagConstraints.gridx= 0;
        gridBagConstraints.gridy= 0;
        gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx=200;
        gridBagConstraints.weighty=200;
        add(new TextJPanel(),gridBagConstraints);
        GridBagConstraints gridBagConstraints2=new GridBagConstraints();
        gridBagConstraints2.gridx=0;
        gridBagConstraints2.gridy=3;
        gridBagConstraints2.anchor=GridBagConstraints.WEST;
        gridBagConstraints2.weightx=200;
        gridBagConstraints2.weighty=200;
        add(new TextJPanel(),gridBagConstraints2);
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ApplicationFrame applicationFrame=new ApplicationFrame();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(applicationFrame);

            applicationFrame.toFront();
            JButton exitButton=new JButton("关闭");
           // applicationFrame.add(exitButton);

            applicationFrame.setVisible(true);
        });
    }
}
