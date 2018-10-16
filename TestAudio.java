/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author 70136
 */
public class TestAudio {
    public swingcanvas canvas;
    File file;
    boolean isend;
    /**
     * @param args the command line arguments
     */
    
    public TestAudio(File f) {
        file=f;
    }

    
    public  void main() throws LineUnavailableException {
        //File file=new File("D:\\documents\\NetBeansProjects\\TestAudio\\src\\testaudio\\33_Sparkling Daydream (Movie size).wav");
        AudioInputStream audioinputstream=null;
        
        try {
            audioinputstream=AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        /*
        
        */
        AudioFormat format=audioinputstream.getFormat();
        int framelength=(int)audioinputstream.getFrameLength();
        int framesize=format.getFrameSize();
        byte[] bytes=new byte[framesize*framelength];
        int channel=format.getChannels();
       
        try {
            audioinputstream.read(bytes);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(bytes.length+" "+format.getFrameRate()+" "+channel+" "+framesize+" "+framelength+" "+format.isBigEndian());
        

        
        int[][] sample=new int[channel][bytes.length/2/channel];
        for(int i=0;i<bytes.length;){
            
            for(int k=0;k<channel;k++){
                sample[k][i/2/channel]=get16bitnum(bytes[i+1], bytes[i]);
                if(i<bytes.length){
                    i+=channel;
                }else{
                    break;
                }
            }
        }
        //System.out.println(sample[0].length);
        final int step=80*60;
        canvas=new swingcanvas(80,channel);
        
        try {
            
            audioinputstream.close();
            audioinputstream=null;
            audioinputstream=AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }finally{file=null;bytes=null;}
        
        Clip speaker = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class,format));
        try {
            speaker.open(audioinputstream);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                audioinputstream.close();
            } catch (IOException ex) {
                Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
            audioinputstream=null;
        }
        //System.out.println("max:"+Runtime.getRuntime().maxMemory()+"free:"+Runtime.getRuntime().freeMemory()+"total:"+Runtime.getRuntime().totalMemory());
        System.gc();
        int[][] paintarr=new int[channel][80];
        Thread p=null;
        Thread play=null;
        for(int i=0;i<framelength;){
            final int index=i;
            if(i+step>sample[0].length){break;}
            play=new Thread(() -> {
                speaker.setLoopPoints(index, index+step);
                speaker.loop(0);
                while (speaker.getFramePosition()<index+step){}
            });
            play.start();
            try{
            for(int ch=0;ch<channel;ch++){
                for(int k=0;k<80;k++){
                    
                    int sum=0;
                    for(int j=0;j<60;j++){
                        sum+=sample[ch][i];
                        i++;
                    }
                    sum/=60;
                    paintarr[ch][k]=sum;
                    
                }
            }
              }catch(ArrayIndexOutOfBoundsException e){
                 restart();
                 sample=null;
                 return;
              }
            p=new Thread(() -> {canvas.update(paintarr);});
            p.start();
            try {
                p.join();
                play.join();    
            } catch (InterruptedException ex) {
                Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
            p=null;
            play=null;
            System.gc();
        }
        
        //System.out.println("done");
        //System.exit(0);
        restart();
        sample=null;
        canvas=null;
        file=null;
        p=null;
        play=null;
        //paintarr=null;
        System.gc();
        return;
    }

    public static  int get16bitnum(byte high,byte low){
        short num=0;
        short h=high;
        short l=low;
        num|=l;
        h<<=8;
        num|=h;
        return num;
    }
    
    public void restart(){
        canvas.end();
        canvas=null;
        file=null;
        System.gc();
    }
    
}