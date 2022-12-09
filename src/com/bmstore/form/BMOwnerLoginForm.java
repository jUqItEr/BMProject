package com.bmstore.form;

import com.bmstore.base.RoundedJTextField;
import com.bmstore.base.RoundedJPasswordField;
import com.bmstore.database.DBConnection;
import com.bmstore.security.HashAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BMOwnerLoginForm extends JFrame {
    private RoundedJTextField txtLoginId;
    private JPanel pnlImageLogo;
    private JPanel pnlMain;
    private RoundedJPasswordField txtLoginPassword;
    private JLabel lblId;
    private JLabel lblPassword;
    private JButton btnSignIn;
    private JCheckBox chkBoxAutoLogin;
    private JButton btnFindInfo;
    private JButton btnSignUp;

    public BMOwnerLoginForm() {
        initializeComponents();
    }

    public static void main(String[] args) {
        new BMOwnerLoginForm();
    }

    private void createUIComponents() {
        pnlImageLogo = new JPanel() {
            final Image background = new ImageIcon("src/com/bmstore/res/drawable/bg_background.jpg")
                                         .getImage();

            @Override
            protected void paintComponent(Graphics g) {
                Dimension dim = getSize();

                super.paintComponent(g);
                g.drawImage(background, 0, 0, dim.width, dim.height, this);
            }
        };

        txtLoginId = new RoundedJTextField(5);
        txtLoginPassword = new RoundedJPasswordField(5);
    }

    private void initializeComponents() {
        this.setContentPane(this.pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pack();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("배달의민족 사장님");

        /*
        * @description Get Nimbus Look And Feel.
        * @author      Kiseok Kang (pyt773924@gmail.com)
        * */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }

        btnSignIn.addActionListener(e -> {
            String storeId = txtLoginId.getText();
            String storePwd = String.valueOf(txtLoginPassword.getPassword());

            if (storeId.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "아이디를 입력해주세요."
                );
                return;
            }
            if (storePwd.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "비밀번호를 입력해주세요."
                );
                return;
            }

            try {
                String query = "SELECT STORE_PASSWORD1, STORE_PASSWORD2 FROM BMSTORE WHERE STORE_ID=?";
                Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs;
                pstmt.setString(1, storeId);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String md5 = rs.getString(1);
                    String sha1 = rs.getString(2);
                    String pwdMd5 = HashAlgorithm.makeHash(storePwd, "md5");
                    String pwdSha1 = HashAlgorithm.makeHash(storePwd, "sha1");

                    if (pwdMd5.equals(md5) && pwdSha1.equals(sha1)) {
                        new BMOwnerMainForm(storeId);

                        this.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "비밀번호가 틀렸습니다."
                        );
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "아이디가 존재하지 않습니다."
                    );
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "아이디나 비밀번호을 찾을 수 없습니다."
                );
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        });

        pnlMain.setBackground(Color.WHITE);
    }
}
