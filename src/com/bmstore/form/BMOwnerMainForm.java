package com.bmstore.form;

import com.bmstore.database.DBConnection;
import oracle.jdbc.OracleTypes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BMOwnerMainForm extends JFrame {
    private final Connection conn = DBConnection.getConnection();

    private JTabbedPane tabMain;
    private JPanel pnlMain;
    private JTabbedPane tabSub;
    private JTable menuTable;

    private final JButton btnOrder = new JButton("주문하기");
    private final JButton btnCancel = new JButton("취소");

    private JTable waitingTable;
    private DefaultTableModel defaultWaitingTable;
    private static Object[][] waitingList;

    private String _storeId;

    public BMOwnerMainForm() {
        initializeComponents();
    }

    public BMOwnerMainForm(String storeId) {
        this._storeId = storeId;
        initializeComponents();
    }

    private void createUIComponents() {

    }

    private void initializeComponents() {
        this.setContentPane(this.pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pack();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(null);
        this.setResizable(false);
        this.setTitle("배달의민족 주문접수");

        this.setTabMenu();
        this.setTabWaiting();
        this.setTabComplete();

        pnlMain.setBackground(Color.WHITE);
    }

    /* menu_table == 메뉴 */
    /*
     * @description Set the menu to JTable
     * @author      Jaehyun Park (raffine_adm@naver.com)
     * */
    public Object[][] getMenuList() {
        Object[][] tempContent;

        try {
            CallableStatement cstmt = conn.prepareCall("{call P_GET_MENU_LIST(?, ?) }");
            ArrayList<ArrayList<Object>> list = new ArrayList<>();

            cstmt.setString(1, this.getStoreId());
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.executeQuery();

            ResultSet rs = (ResultSet)cstmt.getObject(2);

            while (rs.next()) {
                var objects = new ArrayList<>();

                try {
                    Icon icon1;

                    objects.add(rs.getString(1));
                    icon1 = new ImageIcon(new URL(rs.getString(2)));
                    objects.add(icon1);
                    objects.add(rs.getString(3));
                    objects.add(rs.getString(4));
                    objects.add(rs.getString(5));
                    objects.add("1");

                    list.add(objects);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            tempContent = new Object[list.size()][6];

            for (int i = 0; i < list.size(); i++) {
                tempContent[i] = list.get(i).toArray(new Object[6]);
            }
            cstmt.close();
            rs.close();

            return tempContent;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new Object[0][0];
    }

    public void setTabMenu() {
        final String[] header = { "메뉴번호", "메뉴이미지", "메뉴명", "메뉴가격", "재고", "주문수량" };

        Dimension dim = new Dimension(430, 400);  //단순 2차원값 입력을 위한 클래스
        JPanel menuPanel = new JPanel();

        menuPanel.setLayout(null);

        Object[][] menuList = getMenuList();

        //  Returning the Class of each column will allow different
        //  renderers to be used based on Class
        DefaultTableModel defaultTableModel = new DefaultTableModel(menuList, header) {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

        menuTable = new JTable(defaultTableModel);
        menuTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = menuTable.getSelectedRow();
                    int column = menuTable.getSelectedColumn();
                    String result = menuTable.getValueAt(row, column).toString();

                    System.out.println(result);  // required sout!!
                    menuList[row][column] = result;
                }
            }
        });
        menuTable.setSize(700, 500);
        //menu_table.setDefaultEditor(Object.class, null); // 셀을 readonly로 바꾼다.
        menuTable.setRowHeight(40);
        menuTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //table.setLocation(0,0);

        //이런식으로 생성시에 테이블을 넘겨주어야 정상적으로 볼 수 있다.
        JScrollPane scrollPaneMenu = new JScrollPane(menuTable);
        //jscp1.add(table); 과 같이 실행하면, 정상적으로 출력되지 않음.
        scrollPaneMenu.setLocation(0, 0);
        scrollPaneMenu.setSize(700, 500);

        menuPanel.add(scrollPaneMenu);

        btnOrder.setLocation(10, 500);
        btnOrder.setSize(100, 30);
        btnOrder.addActionListener(e -> {
            int[] selectedRow = menuTable.getSelectedRows();

            if (selectedRow.length == 0) {
                JOptionPane.showMessageDialog(null,
                        "메뉴를 선택 후 이용해주세요.", "Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField tf = new JTextField(10);
            String name = JOptionPane.showInputDialog("주소을 입력하세요.");

            if (name != null) {
                tf.setText(name);
            } else {
                JOptionPane.showMessageDialog(null,
                        "주소를 입력해주세요!", "Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            //ArrayList<ArrayList<Object>> order_list = new ArrayList<ArrayList<Object>>();
            Map<Object, Object> obj = new HashMap<>();

            for (int j : selectedRow) {
                Object[] row = menuList[j];

                if (row[5] == "0") {
                    continue;
                }
                obj.put(row[0], row[5]);
            }
            order(obj.toString(), tf.getText());
        });
        menuPanel.add(btnOrder);

        this.tabSub.addTab("메뉴", menuPanel);
    }

    public void order(String json, String Address) {
        try {
            PreparedStatement pstmt =
                    conn.prepareStatement("insert into DELIVERY values(DELIVERY_SEQ.NEXTVAL,?,?,?,?,?,?)");
            String storeId = this.getStoreId();

            pstmt.setString(1, Address);
            pstmt.setInt(2, 0);
            pstmt.setInt(3, 1);
            pstmt.setString(4, storeId);
            pstmt.setString(5, "");
            pstmt.setString(6, json);

            if (pstmt.executeUpdate() > 0) {
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
    public Object[][] getWaitingList() {
        Object[][] tempContent;

        try {
            CallableStatement cstmt = conn.prepareCall("{call P_GET_SP_WAITING_LIST(?, ?) }");

            cstmt.setString(1, this.getStoreId());
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.executeQuery();

            ResultSet rs = (ResultSet) cstmt.getObject(2);
            ArrayList<ArrayList<Object>> list = new ArrayList<>();

            while (rs.next()) {
                ArrayList<Object> objects = new ArrayList<>();

                objects.add(rs.getString(1));
                objects.add(rs.getString(2));

                if (rs.getString(3).equals("0")) {
                    objects.add("대기중");
                } else if (rs.getString(3).equals("1")) {
                    objects.add("배달중");
                } else if (rs.getString(3).equals("2")) {
                    objects.add("완료");
                } else {
                    objects.add("미정");
                }
                objects.add(rs.getString(4));
                list.add(objects);
            }
            tempContent = new Object[list.size()][4];

            for (var i = 0; i < list.size(); i++) {
                tempContent[i] = list.get(i).toArray(new Object[4]);
            }
            cstmt.close();
            rs.close();

            return tempContent;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return new Object[0][0];
    }

    public void setTabWaiting() {
        final String[] header = {"배달일련번호", "배달지", "배달상태", "메뉴"};

        JPanel waitingPanel = new JPanel();

        waitingPanel.setLayout(null);

        waitingList = getWaitingList();

        defaultWaitingTable = new DefaultTableModel(waitingList, header) {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

        waitingTable = new JTable(defaultWaitingTable);
        waitingTable.setSize(700, 500);
        waitingTable.setDefaultEditor(Object.class, null); // 셀을 reanonly로 바꾼다.
        waitingTable.setRowHeight(40);
        waitingTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //table.setLocation(0,0);

        //이런식으로 생성시에 테이블을 넘겨주어야 정상적으로 볼 수 있다.
        JScrollPane scrollPaneWaiting = new JScrollPane(waitingTable);

        //jscp1.add(table); 과 같이 실행하면, 정상적으로 출력되지 않음.
        scrollPaneWaiting.setLocation(0, 0);
        scrollPaneWaiting.setSize(700, 500);
        waitingPanel.add(scrollPaneWaiting);

        btnCancel.setLocation(10, 500);
        btnCancel.setSize(100, 30);
        btnCancel.addActionListener(e -> {
            int[] selectedRow = waitingTable.getSelectedRows();

            if (selectedRow.length == 0) {
                JOptionPane.showMessageDialog(null,
                        "메뉴를 선택 후 취소해주세요.", "Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int result = JOptionPane.showConfirmDialog(null, "정말 취소하시겠어요?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.CLOSED_OPTION) {
                // 사용자가 "예", "아니요"의 선택 없이 다이얼로그 창을 닫은 경우
                JOptionPane.showMessageDialog(null,
                        "취소하였습니다!", "Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (result == JOptionPane.YES_NO_OPTION) {
                try {
                    PreparedStatement pstmt =
                            conn.prepareStatement("DELETE FROM DELIVERY WHERE DELIVERY_NUMBER=?");
                    int row = waitingTable.getSelectedRow();
                    int column = 0; // DELIVERY_NUMBER
                    int d_number = Integer.parseInt(waitingList[row][column].toString());

                    pstmt.setInt(1, d_number);
                    pstmt.executeUpdate();
                    pstmt.close();

                    waitingList = getWaitingList();
                    defaultWaitingTable.removeRow(row);
                    defaultWaitingTable.fireTableDataChanged();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(null,
                        "취소하였습니다!", "Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
        waitingPanel.add(btnCancel);

        this.tabSub.addTab("처리중", waitingPanel);
    }

    /* complete_table == 완료 */
    public Object[][] getCompleteList() {
        Object[][] tempContent;

        try {
            CallableStatement cstmt = conn.prepareCall("{call P_GET_SP_COMPLETE_LIST(?,?) }");

            cstmt.setString(1, this.getStoreId());
            cstmt.registerOutParameter(2, OracleTypes.CURSOR);
            cstmt.executeQuery();

            ResultSet rs = (ResultSet) cstmt.getObject(2);
            ArrayList<ArrayList<Object>> list = new ArrayList<>();

            while (rs.next()) {
                ArrayList<Object> objects = new ArrayList<>();

                objects.add(rs.getString(1));
                objects.add(rs.getString(2));

                if (rs.getString(3).equals("0")) {
                    objects.add("대기중");
                } else if (rs.getString(3).equals("1")) {
                    objects.add("배달중");
                } else if (rs.getString(3).equals("2")) {
                    objects.add("완료");
                } else {
                    objects.add("미정");
                }
                objects.add(rs.getString(4));
                list.add(objects);
            }
            tempContent = new Object[list.size()][4];

            for (int i = 0; i < list.size(); i++) {
                tempContent[i] = list.get(i).toArray(new Object[4]);
            }
            cstmt.close();
            rs.close();

            return tempContent;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new Object[0][0];
    }

    public void setTabComplete() {
        final String[] header = { "배달일련번호", "배달지", "배달상태", "메뉴" };
        JPanel completePanel = new JPanel();

        completePanel.setLayout(null);

        Object[][] completeList = getCompleteList();

        //  Returning the Class of each column will allow different
        //  renderers to be used based on Class
        DefaultTableModel defaultTableModel = new DefaultTableModel(completeList, header) {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

        JTable completeTable = new JTable(defaultTableModel);

        completeTable.setSize(700, 500);
        completeTable.setDefaultEditor(Object.class, null); // 셀을 reanonly로 바꾼다.
        completeTable.setRowHeight(40);
        completeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //table.setLocation(0,0);
        JScrollPane scrollPaneComplete = new JScrollPane(completeTable); //이런식으로 생성시에 테이블을 넘겨주어야 정상적으로 볼 수 있다.
        //jscp1.add(table); 과 같이 실행하면, 정상적으로 출력되지 않음.
        scrollPaneComplete.setLocation(0, 0);
        scrollPaneComplete.setSize(700, 500);
        completePanel.add(scrollPaneComplete);

        this.tabSub.addTab("완료", completePanel);
    }

    public String getStoreId() {
        return this._storeId;
    }

    public void setStoreId(String storeId) {
        this._storeId = storeId;
    }
}
