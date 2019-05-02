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
public class ListItem extends JLabel{
     File file;
    public ListItem(String text,File file,Gui holder) {
        super(text);
        this.file=file;
        this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                holder.file=file;
                Thread t = new Thread(() -> {
            if (file != null) {
                holder.startAnalysis();
            }
            });
              t.start();
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
