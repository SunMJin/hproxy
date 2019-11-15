package com.sunrt.proxy.ui;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

public class ApplicationFrame extends JFrame {
    public ApplicationFrame() {
        setTitle("服务器管理");
        setResizable(false);
        setLayout(new GridBagLayout());
        Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(dimension.getWidth()*0.4),(int)(dimension.getHeight()*0.3));
        setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("icon.png")).getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JScrollPane serverListScrollPanel=new JScrollPane();
        ServerList serverList=new ServerList();
        serverListScrollPanel.setViewportView(serverList);
        serverListScrollPanel.setBorder(BorderFactory.createTitledBorder("服务器列表"));
        add(serverListScrollPanel,new GBC(0, 0).setWeight(20,100).setFill(GridBagConstraints.BOTH));

        JPanel serverDetailPanel=new JPanel();
        serverDetailPanel.setBorder(BorderFactory.createTitledBorder("服务器"));
        serverDetailPanel.setLayout(new GridBagLayout());

        JPanel ipLeftPanel=new JPanel();
        JCheckBox ipCheckBox=new JCheckBox();
        JLabel ipLabel=new JLabel("*服务器IP");
        ipLeftPanel.add(ipCheckBox);
        ipLeftPanel.add(ipLabel);
        serverDetailPanel.add(ipLeftPanel,getLeftGBC(0,0));
        TextField ipTextField=new TextField(20);
        serverDetailPanel.add(ipTextField,getRightGBC(1,0));

        JLabel portLabel=new JLabel("*端口");
        serverDetailPanel.add(portLabel,getLeftGBC(0,1));
        TextField portTextField=new TextField(20);
        serverDetailPanel.add(portTextField,getRightGBC(1,1));

        JPanel pwLeftPanel=new JPanel();
        JCheckBox pwCheckBox=new JCheckBox();
        JLabel pwLabel=new JLabel("*密码");
        pwLeftPanel.add(pwCheckBox);
        pwLeftPanel.add(pwLabel);
        serverDetailPanel.add(pwLeftPanel,getLeftGBC(0,2));
        JPasswordField pwTextField=new JPasswordField(20);
        serverDetailPanel.add(pwTextField,getRightGBC(1,2).setWeight(GridBagConstraints.REMAINDER,100));

        add(serverDetailPanel,new GBC(1,0).setFill(GridBagConstraints.BOTH).setWeight(80,100));
    }

    public static GBC getLeftGBC(int x,int y){
        return new GBC(x,y).setAnchor(GridBagConstraints.NORTHEAST).setWeight(5,0);
    }

    public static GBC getRightGBC(int x,int y){
        return new GBC(x,y).setAnchor(GridBagConstraints.NORTHWEST).setWeight(100,0);
    }

    public static void main(String[] args)  {
        EventQueue.invokeLater(() -> {
            try {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            ApplicationFrame applicationFrame=new ApplicationFrame();
            applicationFrame.toFront();
            applicationFrame.setVisible(true);
        });
    }
}
