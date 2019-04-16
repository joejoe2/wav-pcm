/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.AWTException;
import java.awt.Color;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author 70136
 */
public class Gui extends JFrame implements DropTargetListener {

    TestAudio test;
    File file;
    private boolean requireddel;
    static final float version = 1.052f;
    String[] modeOpt={"slow","normal","fast"};
    JComboBox comboBox;
    int musicNum=0;
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
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            new Robot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread t = new Thread(() -> {
            if (file != null) {
                startAnalysis();
            }
        });
        t.start();
    }

    public void listAllFile(File folder){
          
          File[] files = folder.listFiles();
          if(files==null)return;
          for (File s : files)
          {
            if(s.isDirectory()){
                listAllFile(s);
            }
            else if(s.getName().endsWith(".mp3")||s.getName().endsWith(".wav")){
                System.out.println(s.getName());
                musicNum++;
            }
            
          }
        
    }
    
    public Gui() throws HeadlessException {
        //
        //File folder=new File("D:/music");
        //listAllFile(folder);
        //System.out.println("there are "+musicNum+" musics in "+folder.getAbsolutePath());
        //
        this.pack();
        this.setTitle("music analyzer / virtualizer");
        this.setSize(800, 700);
        this.setResizable(false);
        getContentPane().setBackground(Color.GRAY);//change color
        getContentPane().setLayout(null);
        //
        JButton check = new JButton("check update");
        check.addActionListener((ActionEvent e) -> {
            String ver = UpdateCheck.getver();
            if (ver == null) {
                JOptionPane.showMessageDialog(this, "network error!");
            } else if (version < Float.parseFloat(ver)) {
                JOptionPane.showMessageDialog(this, "                you have a new version(" + ver + ") of the application\nplease check <https://github.com/joejoe2/wav-pcm> for update");
                int opt = JOptionPane.showConfirmDialog(this, "do you want to download automatically?", "update check", JOptionPane.YES_NO_OPTION);
                try {
                    if (opt == JOptionPane.YES_OPTION) {
                        DownloadTest.autoupdate();
                        JOptionPane.showMessageDialog(this, "update successfully!");
                        restart();
                    } else {
                        Desktop.getDesktop().browse(new URI("https://github.com/joejoe2/wav-pcm"));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "network error!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "you are latest now!");
            }
        });
        check.setLocation(0, 30);
        check.setSize(120, 50);
        this.add(check);
        //
        JButton toGameMenu=new JButton("game mode");
        toGameMenu.addActionListener((e) -> {
            starGameMenu();
        });
        toGameMenu.setLocation(0, 90);
        toGameMenu.setSize(120, 50);
        this.add(toGameMenu);
        //
        
        JLabel label = new JLabel("please drag wav or mp3 file here to start", JLabel.CENTER);
        label.setBounds(150, 300, 500, 100);
        label.setForeground(Color.WHITE);
        this.add(label).setFont(new Font("", 1, 20));
        JLabel vlabel = new JLabel("version:" + version);
        vlabel.setLocation(0, 0);
        vlabel.setSize(120, 25);
        vlabel.setForeground(Color.WHITE);
        this.add(vlabel).setFont(new Font("", 1, 15));
        //
        //
        
        comboBox=new JComboBox(modeOpt);
        comboBox.setSelectedItem("normal");
        comboBox.setSize(100,40);
        comboBox.setLocation(260,35);
        comboBox.setFont(new Font("", 1, 15));
        this.add(comboBox);
        //
        JLabel modetxt=new JLabel("analyze mode:");
        modetxt.setFont(new Font("", 1, 15));
        modetxt.setForeground(Color.WHITE);
        modetxt.setSize(100,30);
        modetxt.setLocation(150,40);
        this.add(modetxt);
        
        //
        //
        
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK, this, true));
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Gui.this.setAlwaysOnTop(true);
        Gui.this.toFront();
        Gui.this.setAlwaysOnTop(false);
        //
    }

    public void startAnalysis() {
        this.setVisible(false);
        if (".mp3".equals(file.getName().substring(file.getName().lastIndexOf(".")))) {
            try {
                file = Convert.mp3ToWav(file.getAbsoluteFile());
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            file.deleteOnExit();
            requireddel = true;
        } else {
            requireddel = false;
        }

        test = new TestAudio(file.getAbsoluteFile(),comboBox.getSelectedIndex());

        try {
            test.main();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "can not surpport " + file.getName().substring(file.getName().lastIndexOf(".")) + " file!" + "\nonly surpport .wav or .mp3 now!");
        }
        test = null;
        if (requireddel) {
            System.gc();
            file.delete();
            requireddel = false;
        }
        file = null;
        Gui.this.setVisible(true);
        Gui.this.setAlwaysOnTop(true);
        Gui.this.toFront();
        Gui.this.setAlwaysOnTop(false);
        if (Runtime.getRuntime().totalMemory() >= 300 * 1000 * 1000) {
            try {
                restart();
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void starGameMenu(){
        this.setVisible(false);
        GameMenu menu=new GameMenu();
        Gui.this.setVisible(true);
        Gui.this.setAlwaysOnTop(true);
        Gui.this.toFront();
        Gui.this.setAlwaysOnTop(false);
    }
    
    public static void main(String[] args) {
        new Gui();
    }

    public static void restart() throws IOException {
        Runtime.getRuntime().exec("java -jar run.jar");
        System.exit(0);
    }

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
}
