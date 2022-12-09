package com.bmstore.base;

import javax.swing.*;
import java.awt.*;

/*
 * @author  jUqItEr (pyt773924@gmail.com)
 * @version 1.0.0
 * */
public class OrderRenderer<E> extends JPanel implements ListCellRenderer<E> {

    private JPanel jpCell = new JPanel();

    /*
    * @description The constructor.
    *
    * @author      jUqItEr (pyt773924@gmail.com)
    * */
    public OrderRenderer() {
//        super(new BorderLayout());
    }

    /*
     * @description The VerticalScrollBar is automatically set as required when an order cells overflow.
     *
     * @author      jUqItEr (pyt773924@gmail.com)
     */

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.width = 0;

        return dim;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
        jpCell.add(new JLabel("Render"));
        return jpCell;
    }

    private static void resetButtonStatus(AbstractButton button) {
        ButtonModel model = button.getModel();

        model.setArmed(false);
        model.setPressed(false);
        model.setRollover(false);
        model.setSelected(false);
    }
}
