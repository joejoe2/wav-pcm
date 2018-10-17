/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.HeadlessException;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author 70136
 */
public class gui extends JFrame implements DropTargetListener{
    TestAudio test;
    File file;
    boolean holding;
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if(holding){return;}
        Object o=null;
        file=null;
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            o=dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            List<File> l=(List<File>)(o);
            file=l.get(0);
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(file.getAbsoluteFile());
        Thread t=new Thread(() -> {
           if(file!=null){
           start();
        }
        });
        t.start();
        this.setVisible(false);
        try {
            new Robot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
    }

    public gui() throws HeadlessException {
        this.pack();
        this.setSize(800, 700);
        this.setResizable(false);
        
        getContentPane().setLayout(null);
        
        JLabel label=new JLabel("please drag wav or mp3 file here to start",JLabel.CENTER);
        label.setBounds(150, 300, 500, 100);
        this.add(label).setFont(new Font("", 1, 20));
        
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK,this,true));
        
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.this.setAlwaysOnTop(true);
        gui.this.toFront();
        gui.this.setAlwaysOnTop(false);
        
    }
    public void start(){
        holding=false;
        System.gc();
        System.gc();
        System.gc();
       if(".mp3".equals(file.getName().substring(file.getName().lastIndexOf(".")))){
            try {
                file=convert.mp3ToWav(file.getAbsoluteFile());
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            file.deleteOnExit();
       }
       
       test=new TestAudio(file.getAbsoluteFile());
       
        try {
            test.main();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        test=null;
        file=null;
        System.gc();
        System.gc();
        System.gc();
        gui.this.setVisible(true);
        gui.this.setAlwaysOnTop(true);
        gui.this.toFront();
        gui.this.setAlwaysOnTop(false);
        if(Runtime.getRuntime().totalMemory()>=300*1000*1000){
            try {
                Runtime.getRuntime().exec("java -jar run.jar");
                System.exit(0);
            } catch (IOException ex) {
                Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static void main(String[] args) {
        new gui();
    }
}
