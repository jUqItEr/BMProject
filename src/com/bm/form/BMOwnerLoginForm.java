package com.bm.form;

import com.bm.base.RoundedJTextField;
import com.bm.base.RoundedJPasswordField;
import com.bm.database.DBConnection;
import com.bm.security.HashAlgorithm;

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
    private JButton btnLogin;
    private JCheckBox checkBox1;
    private JButton button1;
    private JButton button2;

    public BMOwnerLoginForm() {
        btnLogin.addActionListener(e -> {
            try {
                String storeId = txtLoginId.getText();
                String query = "SELECT STORE_PASSWORD1, STORE_PASSWORD2 FROM BMSTORE WHERE STORE_ID=?";
                Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs;

                pstmt.setString(1, storeId);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String md5 = rs.getString(1);
                    String sha1 = rs.getString(2);
                    String pwd = String.valueOf(txtLoginPassword.getPassword());
                    String pwdMd5 = HashAlgorithm.makeHash(pwd, "md5");
                    String pwdSha1 = HashAlgorithm.makeHash(pwd, "sha1");

                    if (pwdMd5.equals(md5) && pwdSha1.equals(sha1)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "로그인 성공!"
                        );
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
    }

    public static void main(String[] args) {
        BMOwnerLoginForm form = new BMOwnerLoginForm();
        form.initializeComponents();
    }

    public void createUIComponents() {
        pnlImageLogo = new JPanel() {
            final Image background = new ImageIcon("src/com/bm/res/drawable/bg_background.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                Dimension dim = getSize();
                double ratio = (double)background.getHeight(null) / background.getWidth(null);
                int newWidth = (int)(dim.width * ratio);
                int newHeight = (int)(dim.height * ratio);

                super.paintComponent(g);
                g.drawImage(background, newWidth / 2, newHeight / 2, newWidth, newHeight, this);
            }
        };

        txtLoginId = new RoundedJTextField(5);
        txtLoginPassword = new RoundedJPasswordField(5);
    }

    public void initializeComponents() {
        pnlMain.setBackground(Color.WHITE);

        this.setContentPane(this.pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pack();

        this.setSize(600, 800);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
        this.setLayout(null);
        this.setTitle("배달의민족 사장님");
    }
}