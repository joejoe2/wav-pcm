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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;


/**
 *
 * @author 70136
 */
public class GameMenu extends JFrame implements DropTargetListener{
    File file;
    File arrange;
    private boolean requireddel;
    GameWindow gameWindow;
    BufferedImage bg;   
    
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
        //arrange=null;
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
                try{
                start();
                }catch(Exception ex){
                   ex.printStackTrace();
                }
            }
        });
        t.start();
    }
    
    public void start(){
        this.setVisible(false);
        if (file.getName().endsWith(".mp3")) {
            try {
                file = Convert.mp3ToWav(file.getAbsoluteFile());
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
        try {
            gameWindow=new GameWindow(file.getAbsoluteFile(),arrange);
            gameWindow.main();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"file may be changed or not supported\n(only surpport .wav or .mp3 now!)");
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
        arrange=null;
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.toFront();
        this.setAlwaysOnTop(false);
    }
    
    public GameMenu(){
        
        try {
            bg=ImageIO.read(getClass().getResource("/res/starry_sky2.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(GameMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        JButton chooseArrange=new JButton("choose_arrange");
        chooseArrange.setSize(160,50);
        chooseArrange.setLocation(150,30);
        
        JFileChooser chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("to choose arrange track");
        chooser.setAcceptAllFileFilterUsed(false);
        
        chooseArrange.addActionListener((e)->{
            
           if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                arrange=chooser.getSelectedFile();
                if(!arrange.exists()){
                    arrange=null;
                    JOptionPane.showMessageDialog(this,"file does not exists!");
                }
            }
            
        });
        this.add(back);
        this.add(chooseArrange);
        JLabel label = new JLabel("please drag wav or mp3 file here to start", JLabel.CENTER);
        label.setBounds(150, 200, 500, 100);
        label.setForeground(Color.WHITE);
        this.add(label).setFont(new Font("", 1, 20));
        
        JLabel label2 = new JLabel("操作說明:", JLabel.LEFT);
        label2.setBounds(150, 330, 500, 100);
        label2.setForeground(Color.WHITE);
        this.add(label2).setFont(new Font("", 1, 20));
        
        JLabel label3 = new JLabel("choose_arrange:選譜面的檔案", JLabel.LEFT);
        label3.setBounds(150, 360, 500, 100);
        label3.setForeground(Color.WHITE);
        this.add(label3).setFont(new Font("", 1, 20));
        
        JLabel label4 = new JLabel("back:回到music analyzer/visualizer", JLabel.LEFT);
        label4.setBounds(150, 390, 500, 100);
        label4.setForeground(Color.WHITE);
        this.add(label4).setFont(new Font("", 1, 20));
        
        JLabel label5 = new JLabel("拖曳音樂檔", JLabel.LEFT);
        label5.setBounds(150, 420, 800, 100);
        label5.setForeground(Color.WHITE);
        this.add(label5).setFont(new Font("", 1, 20));
        
        JLabel label6 = new JLabel("進入music game，按A D G J鍵，", JLabel.LEFT);
        label6.setBounds(150, 450, 500, 100);
        label6.setForeground(Color.WHITE);
        this.add(label6).setFont(new Font("", 1, 20));
        
        JLabel label7 = new JLabel("分別消除由左至右，第一 二 三 四軌道的方塊。", JLabel.LEFT);
        label7.setBounds(150, 480, 500, 100);
        label7.setForeground(Color.WHITE);
        this.add(label7).setFont(new Font("", 1, 20));
        
        JLabel label8 = new JLabel("消除越多方塊以取得越高分吧~。", JLabel.LEFT);
        label8.setBounds(150, 510, 500, 100);
        label8.setForeground(Color.WHITE);
        this.add(label8).setFont(new Font("", 1, 20));
        
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK, this, true));
    }

    
}
