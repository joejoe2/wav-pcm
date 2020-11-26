/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.AWTException;
import java.awt.Color;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author 70136
 */
public class Gui extends JFrame implements DropTargetListener {

    //analyze handler
    private TestAudio test;
    //search file list
    private ArrayList<File> filelist;
    //selected folder for search
    private File folder;
    //gui component
    private ListView view;
    private JScrollPane jScrollPane;
    private JButton check;
    private JButton toGameMenu;
    private JButton chbtn;
    private JFileChooser chooser;
    private JLabel vlabel;

    public Gui() throws HeadlessException {
        //check libs
        LibCheck.check_lib(this);
        //set layouts
        set_layouts();
        //set listeners
        set_listeners();
        //read setting
        folder = Recoord.readRecord();
        //update music list
        update_musicList();
        //other configs
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK, this, true));
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Gui.this.setAlwaysOnTop(true);
        Gui.this.toFront();
        Gui.this.setAlwaysOnTop(false);
    }

    /**
     * setup layout
     */
    private void set_layouts() {
        //main window
        this.setTitle("music analyzer / visualizer");
        this.setSize(800, 700);
        this.setResizable(false);
        getContentPane().setBackground(Color.GRAY);//change color
        getContentPane().setLayout(null);
        //check update
        check = new JButton("check update");
        check.setLocation(0, 30);
        check.setSize(120, 50);
        this.add(check);
        //game mode
        toGameMenu = new JButton("game mode");
        toGameMenu.setLocation(0, 90);
        toGameMenu.setSize(120, 50);
        this.add(toGameMenu);
        //version
        vlabel = new JLabel("version:" + UpdateCheck.version);
        vlabel.setLocation(0, 0);
        vlabel.setSize(120, 25);
        vlabel.setForeground(Color.WHITE);
        this.add(vlabel).setFont(new Font("", 1, 15));
        //Folder Chooser
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("select where to search your music");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        //choose folder
        chbtn = new JButton("choose folder");
        chbtn.setFont(new Font("", 1, 15));
        chbtn.setSize(150, 50);
        chbtn.setLocation(650, 120);
        this.add(chbtn);

    }

    /**
     * bind listeners of gui components
     */
    private void set_listeners() {
        check.addActionListener((ActionEvent e) -> {
            //press to check update
            UpdateCheck.check_ver(this);
        });
        toGameMenu.addActionListener((e) -> {
            //press to open game menu 
            startGameMenu();
        });
        chbtn.addActionListener((e) -> {
            //press to choose searching folder
            if (chooser.showOpenDialog(Gui.this) == JFileChooser.APPROVE_OPTION) {
                folder = chooser.getSelectedFile();
                System.out.println(folder);
                //update music list by selected folder
                update_musicList();
                Gui.this.revalidate();
                Gui.this.repaint();
                //save folder to record
                Recoord.writeRecord(folder);
            }
        });
    }

    /**
     * asyn update the searching music list and view
     */
    private void update_musicList() {
        //asyn to seach and update music list
        Thread t = new Thread(() -> {
            if (folder != null && filelist == null && jScrollPane == null && view == null) {
                //if does not assign any searching folder last time
                //get all music file in folder
                filelist = Utils.listAllFile(folder);
                //create the list view
                view = new ListView(filelist, this);
                //create scroll pane
                jScrollPane = new JScrollPane(view);
                jScrollPane.setSize(795, 500);
                jScrollPane.setLocation(0, 170);
                jScrollPane.getVerticalScrollBar().setUnitIncrement(jScrollPane.getVerticalScrollBar().getUnitIncrement() * 6);
                this.add(jScrollPane);
            } else if (folder != null) {
                //direct update lsit view
                filelist = Utils.listAllFile(folder);
                view.update(filelist, this);
            }
            jScrollPane.revalidate();
        });
        //start in another thread
        t.start();
    }

    /**
     * start the analyze task in another thread
     *
     * @param file - analyze target music file
     */
    void startAnalysis(File file) {
        Thread t = new Thread(() -> {
            startProgress(file);
        });
        t.start();
    }

    /**
     * analyze the music
     *
     * @param file - analyze target music file
     */
    private void startProgress(File file) {
        //hide start window
        this.setVisible(false);
        //flag for whether to del temp file
        boolean requireddel = false;
        //check and convert to supported file format(wav) of java sound api
        if (file.getName().endsWith(".mp3")) {
            //mp3 shold be convert into temp wav file
            try {
                file = Convert.mp3ToWav(file.getAbsoluteFile());
                System.gc();
            } catch (Exception ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            //the temp wav file need to be delete after play
            requireddel = true;
        } else if (file.getName().endsWith(".wav")) {
            //the origin wav file dose not need to be delete after play
            requireddel = false;
        } else {
            //un-supported file format
            JOptionPane.showMessageDialog(this, "file is not supported\n(only surpport .wav or .mp3)");
            return;
        }

        try {
            //start analyze progress
            test = new TestAudio(file.getAbsoluteFile());
            //start play
            test.start();
            test = null;
            //finish play
            //after play need to del temp wav file
            if (requireddel) {
                file.delete();
                requireddel = false;
            }
            file = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            System.gc();
        }

        //update music list before go back to start window
        update_musicList();

        //restore start window
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.toFront();
        this.setAlwaysOnTop(false);
    }

    /**
     * open the game menu window
     */
    private void startGameMenu() {
        this.setVisible(false);
        GameMenu menu = new GameMenu();
        Gui.this.setVisible(true);
        Gui.this.setAlwaysOnTop(true);
        Gui.this.toFront();
        Gui.this.setAlwaysOnTop(false);
    }

    /**
     * program entry
     *
     * @param args - sys args
     */
    public static void main(String[] args) {
        Gui gui = new Gui();
    }

    /**
     * Override drop on window for drag and play
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_LINK);
        File file = null;
        try {
            //get the drag on files when dropped down
            List<File> l = (List<File>) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
            //only get the first file
            file = l.get(0);
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        //mask for only accepted when mouse released
        try {
            new Robot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        //check there is no exception for drag and drop file
        if (file == null) {
            return;
        }
        //start analye progress 
        startAnalysis(file);
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
