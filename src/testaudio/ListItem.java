package testaudio;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 70136
 */
public class ListItem extends JLabel {

    File file;

    /**
     *create a list item (startAnalysis when clicked)
     * @param text - list item text
     * @param file - represented file
     * @param holder - parant component with function for clicked on list item
     */
    public ListItem(String text, File file, Gui holder) {
        super(text);
        this.file = file;
        this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (file != null) {
                    holder.startAnalysis(file);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                ListItem.this.setBackground(Color.black);
                ListItem.this.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ListItem.this.setBackground(Color.white);
                ListItem.this.repaint();
            }
        });
    }

}
