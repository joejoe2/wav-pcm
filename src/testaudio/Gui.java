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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

    TestAudio test;
    File file;
    private boolean requireddel;
    static final float version = 1.06f;
    String[] modeOpt={"slow","normal","fast"};
    JComboBox comboBox;
    int musicNum=0;
    ArrayList<File> filelist;
    File folder;
    ListView view;
    JScrollPane jScrollPane;

    
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
                //System.out.println(s.getName());
                filelist.add(s);
                musicNum++;
            }
            
          }
        
    }
    
    public Gui() throws HeadlessException {
        
        this.setTitle("music analyzer / visualizer");
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
        modetxt.setSize(200,30);
        modetxt.setLocation(150,40);
        this.add(modetxt);
        
        //
        JFileChooser chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("select where to search your music");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        
        //
        JButton chbtn=new JButton("choose folder");
        chbtn.setFont(new Font("", 1, 15));
        chbtn.setSize(150,50);
        chbtn.setLocation(650,120);
        this.add(chbtn);
        
        chbtn.addActionListener((e) -> {
            if(chooser.showOpenDialog(Gui.this)==JFileChooser.APPROVE_OPTION){
                folder=chooser.getSelectedFile();
                System.out.println(folder);
                search();
                Gui.this.revalidate();
                Gui.this.repaint();
                Gui.this.writeRecord();
            }
        });
        
        //
        folder=readRecord();
        search();
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

    public void writeRecord(){
         if (new File("setting.ini").exists()&&folder!=null&&folder.exists()) {
                 File ini=new File("setting.ini");
            try {
                ini.createNewFile();
                PrintWriter printWriter=new PrintWriter(ini,"UTF-8");
                printWriter.println("search folder:"+folder);
                printWriter.flush();
                printWriter.close();
                System.gc();
            }catch(Exception e){
                  Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, e);
            }
         }
    } 
    
    public File readRecord(){
        if (new File("setting.ini").exists()) {
              File ini=new File("setting.ini");
            try {
                FileInputStream in=new FileInputStream(ini);
                InputStreamReader reader=new InputStreamReader(in,"UTF-8");
                BufferedReader bufferedReader=new BufferedReader(reader);
                String str=bufferedReader.readLine();
                in.close();
                reader.close();
                bufferedReader.close();
                str=str.substring(14);
                System.gc();
                if("null".equals(str)){
                    return null;
                }else if(new File(str).exists()){
                     return new File(str);
                }
            } catch (Exception ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
              
        }else{
              File ini=new File("setting.ini");
            try {
                ini.createNewFile();
                PrintWriter printWriter=new PrintWriter(ini);
                printWriter.println("search folder:null");
                printWriter.flush();
                printWriter.close();
                System.gc();
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        
        return null;
    }
    
    public void search(){
        Thread s=new Thread(() -> {
        if(folder!=null&&filelist==null&&jScrollPane==null&&view==null){
        filelist=new ArrayList<File>();
        listAllFile(folder);
        view=new ListView(filelist, this);
        
        jScrollPane=new JScrollPane(view);
        jScrollPane.setSize(795, 500);
        jScrollPane.setLocation(0,170);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(jScrollPane.getVerticalScrollBar().getUnitIncrement()*6);
        this.add(jScrollPane);
        }else if(folder!=null){
              filelist=new ArrayList<File>();
              listAllFile(folder);
              view.update(filelist, this);      
        }
        Gui.this.repaint();
        Gui.this.revalidate();
        });
        s.start();
        
    }
    
    public void startAnalysis() {
        this.setVisible(false);
        if (file.getName().endsWith(".mp3")) {
            try {
                file = Convert.mp3ToWav(file.getAbsoluteFile());
                System.gc();
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            //file.deleteOnExit();
            requireddel = true;
        } else {
            requireddel = false;
        }

        test = new TestAudio(file.getAbsoluteFile(),comboBox.getSelectedIndex());

        try {
            System.gc();
            test.main();
            test = null;
        if (requireddel) {
            System.gc();
            file.delete();
            requireddel = false;
        }
        file = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"file may be changed or not supported\n(only surpport .wav or .mp3 now!)");
        }
        
        
        search();
        
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.toFront();
        this.setAlwaysOnTop(false);
        /*if (Runtime.getRuntime().totalMemory() >= 300 * 1000 * 1000) {
            try {
                restart();
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
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
        Gui gui=new Gui();
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
