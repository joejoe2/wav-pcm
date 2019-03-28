/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Robot;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author 70136
 */
public class GameMenu extends JFrame implements DropTargetListener{
    File file;
    private boolean requireddel;
    GameWindow gameWindow;
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void dragExit(DropTargetEvent dte) {
        //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void drop(DropTargetDropEvent dtde) {
        Object o = null;
        file = null;
        dtde.acceptDrop(DnDConstants.ACTION_LINK);
        try {
            o = dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            List<File> l = (List<File>) (o);
            file = l.get(0);
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            new Robot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread t = new Thread(() -> {
            if (file != null) {
                start();
            }
        });
        t.start();
    }
    
    public void start(){
        this.setVisible(false);
        if (".mp3".equals(file.getName().substring(file.getName().lastIndexOf(".")))) {
            try {
                file = convert.mp3ToWav(file.getAbsoluteFile());
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            file.deleteOnExit();
            requireddel = true;
        } else {
            requireddel = false;
        }
        try {
            gameWindow=new GameWindow(file.getAbsoluteFile());
            gameWindow.main();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "can not surpport " + file.getName().substring(file.getName().lastIndexOf(".")) + " file!" + "\nonly surpport .wav or .mp3 now!");
        }
        
        gameWindow = null;
        if (requireddel) {
            try {
                System.gc();//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Files.delete(file.toPath());
            } catch (IOException ex) {
                Logger.getLogger(GameMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            requireddel = false;
        }
        file = null;
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.toFront();
        this.setAlwaysOnTop(false);
    }
    
    public GameMenu(){
        this.pack();
        this.setTitle("music game menu");
        this.setSize(800, 700);
        this.setResizable(false);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.toFront();
        getContentPane().setBackground(Color.GRAY);//change color
        getContentPane().setLayout(null);
        
        JButton back=new JButton("back");
        back.setSize(120, 50);
        back.setLocation(0,30);
        back.addActionListener((e) -> {
            GameMenu.this.dispose();
        });
        this.add(back);
        JLabel label = new JLabel("please drag wav or mp3 file here to start", JLabel.CENTER);
        label.setBounds(150, 300, 500, 100);
        label.setForeground(Color.WHITE);
        this.add(label).setFont(new Font("", 1, 20));
        
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK, this, true));
    }

    
}
