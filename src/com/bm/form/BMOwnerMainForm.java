package com.bm.form;

import com.bm.base.OrderRenderer;
import com.bm.database.DBConnection;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.oracore.OracleType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bm.form.BMOwnerLoginForm.STOREID;

public class BMOwnerMainForm extends JFrame {
    private JTabbedPane tabMain;
    private JPanel pnlMain;
    private JTabbedPane tabSub;
    private JList list1;
    public Connection conn = DBConnection.getConnection();
    public JTable menu_table;
    public DefaultTableModel default_menu_table;
    public JTable waiting_table;
    public DefaultTableModel default_waiting_table;
    public JTable complete_table;
    public DefaultTableModel default_complete_table;

    public BMOwnerMainForm() { initializeComponents(); }

    private void createUIComponents() { }
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
        this.set_tab_menu();
        this.set_tab_waiting();
        this.set_tab_complete();
    }

    /* menu_table == 메뉴 */
    public Object[][] get_menu_list() {
        System.out.println("get_menu_list");
        Object[][] temp_content;
        try {
            CallableStatement cstmt = conn.prepareCall("{call P_GET_MENU_LIST(?, ?) }");
            System.out.println(STOREID);
            cstmt.setString(1, STOREID);
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.executeQuery();
            ResultSet rs = (ResultSet) cstmt.getObject(2);
            ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
            while (rs.next()) {
                var temp = new ArrayList<Object>();
                try {
                    temp.add(rs.getString(1));
                    Icon icon1 = new ImageIcon(new URL( rs.getString(2) ));
                    temp.add(icon1);
                    temp.add(rs.getString(3));
                    temp.add(rs.getString(4));
                    temp.add(rs.getString(5));
                    temp.add("1");
                    list.add(temp);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            temp_content = new Object[list.size()][6];
            for (var i = 0; i < list.size(); i++) {
                temp_content[i] = list.get(i).toArray(new Object[6]);
            }

            cstmt.close();
            rs.close();
            return temp_content;
        } catch (SQLException error) {
            System.out.println(error);
            error.printStackTrace();
        }
        Object[][] empty = new Object[0][0];
        return empty;
    }
    public void set_tab_menu() {
        Dimension dim = new Dimension(430, 400);  //단순 2차원값 입력을 위한 클래스
        var menu_panel = new JPanel();
        menu_panel.setLayout(null);


        String header[] = {"메뉴번호", "메뉴이미지", "메뉴명", "메뉴가격", "재고", "주문수량"};
        Object[][] menu_list = get_menu_list();

        default_menu_table = new DefaultTableModel(menu_list, header)
        {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };

        menu_table =  new JTable(default_menu_table);
//        default_menu_table.addTableModelListener(new TableModelListener()
//        {
//            public void tableChanged(TableModelEvent evt)
//            {
//
//                menu_list[row][columns] =
//                System.out.println(row + "," + columns);
//            }
//        });
        menu_table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    int row = menu_table.getSelectedRow();
                    int column = menu_table.getSelectedColumn();
                    String resul = menu_table.getValueAt(row, column).toString();
                    System.out.println(resul);
                    menu_list[row][column] = resul;
                }
            }
        });
        menu_table.setSize(700, 500);
        //menu_table.setDefaultEditor(Object.class, null); // 셀을 reanonly로 바꾼다.
        menu_table.setRowHeight(40);
        menu_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //table.setLocation(0,0);
        JScrollPane scrollPane_menu = new JScrollPane(menu_table); //이런식으로 생성시에 테이블을 넘겨주어야 정상적으로 볼 수 있다.
        //jscp1.add(table); 과 같이 실행하면, 정상적으로 출력되지 않음.
        scrollPane_menu.setLocation(0,0);
        scrollPane_menu.setSize(700,500);
        menu_panel.add(scrollPane_menu);

        var button_order = new JButton("주문하기");
        button_order.setLocation(10,500);
        button_order.setSize(100,30);
        button_order.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRow = menu_table.getSelectedRows();
                if(selectedRow.length == 0) {
                    JOptionPane.showMessageDialog(null,
                            "메뉴를 선택 후 이용해주세요.", "Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField tf = new JTextField(10);
                String name =
                        JOptionPane.showInputDialog("주소을 입력하세요.");
                if(name != null) {
                    tf.setText(name);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "주소를 입력해주세요!", "Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //ArrayList<ArrayList<Object>> order_list = new ArrayList<ArrayList<Object>>();
                Map<Object, Object> obj =  new HashMap<Object, Object>();
                for(var i=0; i<selectedRow.length; i++) {
                    var row = menu_list[selectedRow[i]];
                    if(row[5] == "0") {
                        continue;
                    }
                    obj.put(row[0], row[5]);
                }
                String str = obj.toString();
                order(str, tf.getText());

            }
        });
        menu_panel.add(button_order);

        this.tabSub.addTab("메뉴", menu_panel);
    }
    public void order(String json, String Address) {
        try {
            PreparedStatement pstmt =
                    conn.prepareStatement("insert into DELIVERY values(DELIVERY_SEQ.NEXTVAL,?,?,?,?,?,?)");

            String STORE_ID = STOREID;
            pstmt.setString(1, Address);
            pstmt.setInt(2, 0);
            pstmt.setInt(3, 1);
            pstmt.setString(4, STORE_ID);
            pstmt.setString(5, "");
            pstmt.setString(6, json);
            pstmt.executeUpdate();
            if(true) {
                System.out.println("추가완료");
            } else {
                System.out.println("추가실패");
            }
            pstmt.close();

        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    /* waiting_table == 처리중 */
    public Object[][] get_waiting_list() {
        Object[][] temp_content;
        try {
            CallableStatement cstmt = conn.prepareCall("{call P_GET_SP_WAITING_LIST(?, ?) }");
            cstmt.setString(1, STOREID);
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.executeQuery();
            ResultSet rs = (ResultSet) cstmt.getObject(2);
            ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
            while (rs.next()) {
                var temp = new ArrayList<Object>();
                temp.add(rs.getString(1));

                temp.add(rs.getString(2));
                if(rs.getString(3).equals("0")) {
                    temp.add("대기중");
                } else if(rs.getString(3).equals("1")) {
                    temp.add("배달중");
                } else if(rs.getString(3).equals("2")) {
                    temp.add("완료");
                } else {
                    temp.add("미정");
                }
                temp.add(rs.getString(4));
                list.add(temp);
            }
            temp_content = new Object[list.size()][4];
            for (var i = 0; i < list.size(); i++) {
                temp_content[i] = list.get(i).toArray(new Object[4]);
            }
            cstmt.close();
            rs.close();
            return temp_content;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        Object[][] empty = new Object[0][0];
        return empty;
    }
    public void set_tab_waiting() {
        var waiting_panel = new JPanel();
        waiting_panel.setLayout(null);

        String header[] = {"배달일련번호", "배달지", "배달상태", "메뉴"};
        Object[][] waiting_list = get_waiting_list();
        System.out.println(waiting_list.length);

        default_waiting_table = new DefaultTableModel(waiting_list, header)
        {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };

        waiting_table =  new JTable(default_waiting_table);
        waiting_table.setSize(700, 500);
        waiting_table.setDefaultEditor(Object.class, null); // 셀을 reanonly로 바꾼다.
        waiting_table.setRowHeight(40);
        waiting_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //table.setLocation(0,0);
        JScrollPane scrollPane_waiting = new JScrollPane(waiting_table); //이런식으로 생성시에 테이블을 넘겨주어야 정상적으로 볼 수 있다.
        //jscp1.add(table); 과 같이 실행하면, 정상적으로 출력되지 않음.
        scrollPane_waiting.setLocation(0,0);
        scrollPane_waiting.setSize(700,500);
        waiting_panel.add(scrollPane_waiting);

        var button_cancel = new JButton("취소");
        button_cancel.setLocation(10,500);
        button_cancel.setSize(100,30);
        button_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRow = waiting_table.getSelectedRows();
                if(selectedRow.length == 0) {
                    JOptionPane.showMessageDialog(null,
                            "메뉴를 선택 후 이용해주세요.", "Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField tf = new JTextField(10);
                String name =
                        JOptionPane.showInputDialog("주소을 입력하세요.");
                if(name != null) {
                    tf.setText(name);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "주소를 입력해주세요!", "Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        });
        waiting_panel.add(button_cancel);

        this.tabSub.addTab("처리중", waiting_panel);
    }

    /* complete_table == 완료 */
    public Object[][] get_complete_list() {
        Object[][] temp_content;
        try {
            CallableStatement cstmt = conn.prepareCall("{call P_GET_SP_COMPLETE_LIST(?, ?) }");
            cstmt.setString(1, STOREID);
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.executeQuery();
            ResultSet rs = (ResultSet) cstmt.getObject(2);
            ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
            while (rs.next()) {
                var temp = new ArrayList<Object>();
                temp.add(rs.getString(1));

                temp.add(rs.getString(2));
                if(rs.getString(3).equals("0")) {
                    temp.add("대기중");
                } else if(rs.getString(3).equals("1")) {
                    temp.add("배달중");
                } else if(rs.getString(3).equals("2")) {
                    temp.add("완료");
                } else {
                    temp.add("미정");
                }
                temp.add(rs.getString(4));
                list.add(temp);
            }
            temp_content = new Object[list.size()][4];
            for (var i = 0; i < list.size(); i++) {
                temp_content[i] = list.get(i).toArray(new Object[4]);
            }
            cstmt.close();
            rs.close();
            return temp_content;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        Object[][] empty = new Object[0][0];
        return empty;
    }
    public void set_tab_complete() {
        var complete_panel = new JPanel();
        complete_panel.setLayout(null);

        String header[] = {"배달일련번호", "배달지", "배달상태", "메뉴"};
        Object[][] complete_list = get_complete_list();
        System.out.println(complete_list.length);

        default_complete_table = new DefaultTableModel(complete_list, header)
        {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };

        complete_table =  new JTable(default_complete_table);
        complete_table.setSize(700, 500);
        complete_table.setDefaultEditor(Object.class, null); // 셀을 reanonly로 바꾼다.
        complete_table.setRowHeight(40);
        complete_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //table.setLocation(0,0);
        JScrollPane scrollPane_complete = new JScrollPane(complete_table); //이런식으로 생성시에 테이블을 넘겨주어야 정상적으로 볼 수 있다.
        //jscp1.add(table); 과 같이 실행하면, 정상적으로 출력되지 않음.
        scrollPane_complete.setLocation(0,0);
        scrollPane_complete.setSize(700,500);
        complete_panel.add(scrollPane_complete);

        this.tabSub.addTab("완료", complete_panel);
    }
}