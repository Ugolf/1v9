package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Gui myGui = new Gui();
                myGui.centreWindow(myGui);
                myGui.setVisible(true);
            }
        });

    }
}