/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author 70136
 */
public class GameWindow extends JPanel{
    File file;
    JFrame frame;
    Clip speaker;
    boolean isend;
    AudioFormat format;
    long framelength;
    Beat test;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        g.setColor(Color.GREEN);
        test.draw(g);
        
    }

    
 
    public GameWindow(File f) throws Exception{
        file=f;
        frame=new JFrame();
        frame.setTitle("music game");
        frame.setSize(1000, 800);
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.GRAY);//change color
        frame.getContentPane().setLayout(null);
        
        JButton back=new JButton("back");
        back.setSize(120, 50);
        back.setLocation(0,30);
        back.addActionListener((e) -> {
            isend=true;
            System.out.println("end");
        });
        frame.add(back);

        AudioInputStream audioinputstream = AudioSystem.getAudioInputStream(file);
        speaker = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioinputstream.getFormat()));
        try {
            speaker.open(audioinputstream);
            format=audioinputstream.getFormat();
            framelength=audioinputstream.getFrameLength();
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                audioinputstream.close();
            } catch (IOException ex) {
                Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
            audioinputstream = null;
        }
        
        this.setBackground(Color.BLACK);
        this.setSize(500,frame.getHeight());
        this.setLocation(150,0);
        frame.getContentPane().add(this);
        
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void main() throws Exception{
       int framelimit=(int)format.getFrameRate()/40; //set update rate 40fps
        System.out.println(framelimit);
        test=new Beat(20, -20, 0);
       for(int i=0;i<framelength;i+=framelimit){
          if(isend){break;}
          final int index=i;
          Thread play = new Thread(() -> {
                speaker.setLoopPoints(index, index+framelimit);
                speaker.loop(0);
                while (speaker.getFramePosition() < index) {
                   
                }
            });
          play.run();
          test.moveOffset(0, 8);
          repaint();
          play.join();
          System.gc();
       }
       speaker.stop();
       speaker.close();
        frame.setVisible(false);
        frame.dispose();
        frame=null;
        file=null;
        System.gc();
        return;
    }
}
