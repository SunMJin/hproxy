package com.sunrt.proxy.ui;

import java.awt.*;

public class test4 {
    public static void main(String[] args) {
        String[] strings=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(String s:strings){
            System.out.println(s);
        }
    }
}