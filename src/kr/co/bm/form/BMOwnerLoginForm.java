package kr.co.bm.form;

import kr.co.bm.base.RoundedJPasswordField;
import kr.co.bm.base.RoundedJTextField;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BMOwnerLoginForm extends JFrame {
    private RoundedJTextField txtLoginId;
    private JPanel pnlImageLogo;
    private JPanel pnlMain;
    private RoundedJPasswordField txtLoginPassword;
    private JLabel lblId;
    private JLabel lblPassword;

    public static void main(String[] args) {
        BMOwnerLoginForm form = new BMOwnerLoginForm();
        form.initializeComponents();
    }

    public void createUIComponents() {
        txtLoginId = new RoundedJTextField(5);
        txtLoginPassword = new RoundedJPasswordField(5);
    }

    public void initializeComponents() {
        pnlMain.setBackground(Color.WHITE);

        this.setContentPane(this.pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pack();

        this.setSize(300, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);


    }
}