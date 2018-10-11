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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LineUnavailableException {
        File file=new File("D:\\documents\\NetBeansProjects\\TestAudio\\src\\testaudio\\17. 不安定な神様 (TV Ver.).wav");
        AudioInputStream audioinputstream=null;
        AudioInputStream out=null;
        try {
        audioinputstream=AudioSystem.getAudioInputStream(file);
        out=AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        AudioFormat format=audioinputstream.getFormat();
        int framelength=(int)audioinputstream.getFrameLength();
        int framesize=format.getFrameSize();
        byte[] bytes=new byte[framesize*framelength];
        int channel=format.getChannels();
        int result=0;
        try {
            result=audioinputstream.read(bytes);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(bytes.length+" "+format.getFrameRate()+" "+channel+" "+framesize+" "+framelength+" "+format.isBigEndian());

        

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
        System.out.println(sample[0].length);
        swingcanvas canvas=new swingcanvas();

        Clip speaker = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class,format));
        try {
            speaker.open(out);
            } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        for(int i=0;i<sample[0].length;){
            final int index=i;
            if(i+10000>sample[0].length){break;}
            Thread play=new Thread(() -> {
                speaker.setLoopPoints(index, index+10000);
                speaker.loop(0);
                while (speaker.getFramePosition()<index+10000){}
            });
             play.start();
            System.out.println("index :"+i);
            int[] paintarr=new int[100];
            for(int k=0;k<100;k++){
                //System.out.print(sample[0][i]+" ");
                int sum=0;
                for(int j=0;j<100;j++){
                    sum+=sample[0][i];
                    i++;
                }
                sum/=100;
                paintarr[k]=sum;
                //System.out.println(sum);
            }
            Thread p=new Thread(() -> {canvas.update(paintarr);});
            p.start();
            try {
            p.join();
            play.join();    
            } catch (InterruptedException ex) {
                Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
         System.out.println("done");
         System.exit(0);
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
    
}
