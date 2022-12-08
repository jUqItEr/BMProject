package com.bm.form;

import javax.swing.*;
import java.awt.*;

public class BMOwnerMainForm extends JFrame {
    private JTabbedPane tabMain;
    private JPanel pnlMain;
    private JTabbedPane tabSub;
    private JList list1;

    public BMOwnerMainForm() {
        initializeComponents();
    }

    private void createUIComponents() {

    }

    private void initializeComponents() {
        pnlMain.setBackground(Color.WHITE);

        this.setContentPane(this.pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pack();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(null);
        this.setResizable(false);
        this.setTitle("배달의민족 주문접수");
    }
}