package com.sunrt.proxy.ui;

import javax.swing.*;

public class ServerList extends JList {
    public ServerList() {
        DefaultListModel<ServerModel>defaultListModel=new DefaultListModel<>();
        setModel(defaultListModel);
        addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()){
                getSelectedValue();
            }
        });
    }
}
