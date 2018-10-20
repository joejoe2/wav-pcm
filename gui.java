/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.AWTException;
import java.awt.Desktop;
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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
public class gui extends JFrame implements DropTargetListener{
    TestAudio test;
    File file;
    private boolean requireddel;
    static final float version=0.99f;
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {        
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
        Object o=null;
        file=null;
        dtde.acceptDrop(DnDConstants.ACTION_LINK);
        try {
            o=dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            List<File> l=(List<File>)(o);
            file=l.get(0);
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
        Thread t=new Thread(() -> {
           if(file!=null){
           start();
        }
        });
        t.start();
    }

    public gui() throws HeadlessException {
        this.pack();
        this.setSize(800, 700);
        this.setResizable(false);
        
        getContentPane().setLayout(null);
        JButton check=new JButton("check update");
        check.addActionListener((ActionEvent e) -> {
            String ver=updatecheck.getver();
            if(ver==null){JOptionPane.showMessageDialog(this, "network error!");}
            else if (version < Float.parseFloat(ver)) {
                JOptionPane.showMessageDialog(this, "                you have a new version of the application\nplease check <https://github.com/joejoe2/wav-pcm> for update");
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/joejoe2/wav-pcm"));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{JOptionPane.showMessageDialog(this, "you are latest now!");}
        });
        check.setLocation(150, 0);
        check.setSize(500, 50);
        this.add(check);
        JLabel label=new JLabel("please drag wav or mp3 file here to start",JLabel.CENTER);
        label.setBounds(150, 300, 500, 100);
        this.add(label).setFont(new Font("", 1, 20));
        JLabel vlabel=new JLabel("version:"+version,JLabel.CENTER);
        vlabel.setLocation(0, 0);
        vlabel.setSize(100,25);
        this.add(vlabel).setFont(new Font("", 1, 15));
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK,this,true));
        
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.this.setAlwaysOnTop(true);
        gui.this.toFront();
        gui.this.setAlwaysOnTop(false);
        
    }
    public void start(){
        this.setVisible(false);
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
            requireddel=true;
       }else{
           requireddel=false;
       }
       
       test=new TestAudio(file.getAbsoluteFile());
       
        try {
            test.main();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "can not surpport "+file.getName().substring(file.getName().lastIndexOf("."))+" file!"+"\nonly surpport .wav or .mp3 now!");
        }
        test=null;
        if(requireddel){
           file.delete();
           requireddel=false;
        }
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
