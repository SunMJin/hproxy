package com.sunrt.proxy.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class MenuFrame extends JFrame implements ListSelectionListener {
    JTextField jtf;
    JList<String> jlist1, jlist2;
    static final String[] CP = { "青椒土豆丝", "西红柿炒鸡蛋", "麻辣小龙虾", "鱼香肉丝", "西湖醋鱼", "红汤老火锅", "可乐", "茉莉花茶" };
    DefaultListModel<String> dlm;//用dlm给jlist2动态添加数据

    public MenuFrame() {
        JPanel jpCenter = new JPanel(new GridLayout(1, 2));
        jlist1 = new JList<String>(CP);
        jlist1.addListSelectionListener(this);
        JScrollPane jsp1 = new JScrollPane(jlist1);//滚动面板
        jsp1.setBorder(BorderFactory.createTitledBorder("全部菜品"));//带标题的边框
        jpCenter.add(jsp1);
        add(jpCenter);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MenuFrame();//启动窗口
    }


    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            dlm.addElement(jlist1.getSelectedValue());//给dlm添加元素,更新jlist2的数据
            jtf.setText(dlm.size()+"道");

        }
    }
}