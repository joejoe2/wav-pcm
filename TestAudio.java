/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JSlider;

/**
 *
 * @author 70136
 */
public class TestAudio {

    public swingcanvas canvas;
    File file;
    boolean isend;
    boolean isadjusting=false;
    int targettime=-1;
    final int averagenum;
    /**
     * @param args the command line arguments
     */
    public TestAudio(File f,int mode) {
        file = f;
        if(mode==0){
          averagenum=16;
        }else if(mode==1){
          averagenum=12;
        }else if(mode==2){
          averagenum=4;
        }else if(mode==3){
          averagenum=1;
        }else{
          averagenum=16;
        }
    }

    public void main() throws Exception {
        //File file=new File("D:\\documents\\NetBeansProjects\\TestAudio\\src\\testaudio\\33_Sparkling Daydream (Movie size).wav");
        AudioInputStream audioinputstream = null;
        try {
            audioinputstream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        AudioFormat format = audioinputstream.getFormat();
        int framelength = (int) audioinputstream.getFrameLength();
        int framesize = format.getFrameSize();
        byte[] bytes = new byte[framesize * framelength];

        int channel = format.getChannels();

        try {
            audioinputstream.read(bytes);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(bytes.length+" "+format.getFrameRate()+" "+channel+" "+framesize+" "+framelength+" "+format.isBigEndian());

        int[][] sample = new int[channel][bytes.length / 2 / channel];
        for (int i = 0; i < bytes.length;) {

            for (int k = 0; k < channel; k++) {
                sample[k][i / 2 / channel] = get16bitnum(bytes[i + 1], bytes[i]);
                if (i < bytes.length) {
                    i += channel;
                } else {
                    break;
                }
            }
        }

        try {
            audioinputstream.close();
            audioinputstream = null;
            audioinputstream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            bytes = null;
        }

        Clip speaker = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, format));
        try {
            speaker.open(audioinputstream);
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
        
        final int step = 128 * 2 * averagenum;
        
        canvas = new swingcanvas( channel, file.getName().replaceAll(".wav", ""), speaker.getMicrosecondLength());
        file = null;

        
        JSlider slider=new JSlider(0,100);
        slider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mousePressed(MouseEvent e) {
                 //To change body of generated methods, choose Tools | Templates.
                 setIsadjusting(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setTargettime(slider.getValue());
                setIsadjusting(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        slider.setBackground(Color.GRAY);
        slider.setForeground(Color.WHITE);
        canvas.frame.add(slider);
        slider.setSize(250,15);
        slider.setLocation(500,75);
        //
        int[][] paintarr = new int[channel][128 * 2];
        Thread p = null;
        Thread play = null;
        outer:
        for (int i = 0; i < framelength;) {
            while (canvas.isstop()) {
                if (canvas.issterminated()) {
                    break outer;
                }
                speaker.stop();
            }
            if (canvas.issterminated()) {
                break;
            }
            
            if(targettime!=-1){
                i=targettime*framelength/100;
                slider.setValue(targettime);
                targettime=-1;
                //speaker.stop();   cause freeze at the end???
                speaker.setFramePosition(i);
            }else{
                if (!isadjusting) {
                    slider.setValue((int)(i*100f/framelength));
                }
            }
            final int index = i;
            
            
            if (i + step > sample[0].length) {
                break;
            }

            canvas.settime(i * speaker.getMicrosecondLength() / framelength);
            

            
            play = new Thread(() -> {
                speaker.setLoopPoints(index, index + step);
                speaker.loop(0);
                while (speaker.getFramePosition()< index + step&&targettime==-1) {
                   
                }
            });
            try {
                for (int ch = 0; ch < channel; ch++) {
                    i=index;
                    for (int k = 0; k < 128 * 2; k++) {

                        int sum = 0;
                        for (int j = 0; j < averagenum; j++) {
                            sum += sample[ch][i];
                            i++;
                        }
                        sum /= averagenum;
                        paintarr[ch][k] = sum;

                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
            p = new Thread(() -> {
                canvas.update(paintarr);
            });
            p.start();
            play.start();
            try {
                p.join();
                play.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.gc();
            
        }
        restart();
        speaker.stop();
        //speaker.close();
        return;
    }

    public static int get16bitnum(byte high, byte low) {
        short num = 0;
        short h = high;
        short l = low;
        num |= l;
        h <<= 8;
        num |= h;
        return num;
    }

    public void restart() {
        canvas.end();
        canvas = null;
        file = null;
    }

    public void setIsadjusting(boolean isadjusting) {
        this.isadjusting = isadjusting;
    }

    public void setTargettime(int targettime) {
        this.targettime = targettime;
    }
    
    
    
}
