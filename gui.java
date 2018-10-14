/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;

/**
 *
 * @author 70136
 */
public class gui extends JFrame implements DropTargetListener{
    TestAudio test;
    File file;
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
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
        
        //this.dispose();
        
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
        this.setSize(800, 700);
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK,this,true));
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void start(){
       test=new TestAudio(file.getAbsoluteFile());
        try {
            test.main();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args) {
        gui g=new gui();

    }
}
