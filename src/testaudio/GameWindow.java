/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    LinkedList<Beat> test0,test1,test2,test3;//contain beats above of deadline
    LinkedList<Beat> waitdel;//contain beats got hit
    LinkedList<Beat> out;//contain beats out of deadline(miss)
    boolean[] key=new boolean[4];//use for input
    boolean[] allow=new boolean[]{true,true,true,true};//avoid long press get hit beats
    BeatGenerator beatGenerator;//read from file and generate beats
    JLabel secJLabel;//time label
    JLabel scoreLabel;
    JLabel comboLabel;
    int nowsec=0;//record time in sec
    int nowdigit=0;//record time in digit
    int timer=1;//use to count time
    ArrayList<String> timing;//record when to check generate beats
    int holder=0;//hold the index of timing
    int range=30;//hit range
    int score;
    int combo;
    Color lineColor=new Color(255,255,255);
    Color bgColor=new Color(0,0,160);
    Color boundColor=Color.gray;
    Color effectColor=new Color(255, 255,56);
    Color beatColor=Color.GREEN;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        
        g.setColor(lineColor);
        g.fillRect(0,400,500,range);
        
        g.setColor(boundColor);
        g.drawLine(125,0,125,800);
        g.drawLine(250,0,250,800);
        g.drawLine(375,0,375,800);
        
        //
        g.setColor(beatColor);
        //draw beats
        test0.forEach((b) -> {
            b.draw(g);
        });
        test1.forEach((b) -> {
            b.draw(g);
        });
        test2.forEach((b) -> {
            b.draw(g);
        });
        test3.forEach((b) -> {
            b.draw(g);
        });
        out.forEach((b) -> {
            b.draw(g);
        });
        g.setColor(effectColor);
        waitdel.forEach((b) -> {
            b.draw(g);
        });
        
    }

    
 
    public GameWindow(File f) throws Exception{
        //
        beatGenerator=new BeatGenerator(null);
        timing=beatGenerator.getTiming();
        test0=new LinkedList<Beat>();
        test1=new LinkedList<Beat>();
        test2=new LinkedList<Beat>();
        test3=new LinkedList<Beat>();
        waitdel=new LinkedList<Beat>();
        out=new LinkedList<Beat>();
        this.setFocusable(true);
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                   if(e.getKeyCode()==KeyEvent.VK_A){
                       key[0]=true;
                   }else if(e.getKeyCode()==KeyEvent.VK_D){
                       key[1]=true;
                   }else if(e.getKeyCode()==KeyEvent.VK_G){
                       key[2]=true;
                   }
                   else if(e.getKeyCode()==KeyEvent.VK_J){
                       key[3]=true;
                   }
                   
            }

            @Override
            public void keyReleased(KeyEvent e) {
                   if(e.getKeyCode()==KeyEvent.VK_A){
                       key[0]=false;
                       allow[0]=true;
                   }else if(e.getKeyCode()==KeyEvent.VK_D){
                       key[1]=false;
                       allow[1]=true;
                   }else if(e.getKeyCode()==KeyEvent.VK_G){
                       key[2]=false;
                       allow[2]=true;
                   }else if(e.getKeyCode()==KeyEvent.VK_J){
                       key[3]=false;
                       allow[3]=true;
                   }
            }
            
        });
        //
        file=f;
        frame=new JFrame();
        frame.setTitle("music game");
        frame.setSize(1000, 800);
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.GRAY);//change color
        frame.getContentPane().setLayout(null);
        //
        JButton back=new JButton("back");
        back.setSize(120, 50);
        back.setLocation(0,30);
        back.addActionListener((e) -> {
            isend=true;
            System.out.println("end");
        });
        frame.add(back);
        //
        //
        secJLabel=new JLabel("0");
        secJLabel.setSize(200,100);
        secJLabel.setLocation(0,100);
        secJLabel.setFont(new Font("",1,15));
        secJLabel.setForeground(Color.white);
        frame.add(secJLabel);
        //
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
        
        this.setBackground(bgColor);
        this.setSize(500,frame.getHeight());
        this.setLocation(150,0);
        frame.getContentPane().add(this);
        
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void main() throws Exception{
       int framelimit=(int)format.getFrameRate()/20; //set update rate 20fps
        //System.out.println(framelimit);
        
       for(int i=0;i<framelength;i+=framelimit){
          
          if(isend){break;}
          final int index=i;
          if(index+framelimit>=framelength)break;
          speaker.setLoopPoints(index, index+framelimit);
          speaker.loop(0);
          secJLabel.setText(nowsec+"."+nowdigit+" real:"+i * speaker.getMicrosecondLength() / framelength/1000000.0);
          ////generate beats check and update time
          if(timer==1){
               if(holder<timing.size()&&timing.get(holder).equals(nowsec+"."+nowdigit)){
               //can not use for loop due to linklist will be null whne it is empty
                  Beat b=beatGenerator.next(0, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(20);b.setY(-20);
                  test0.add(b);
                  }
                  b=beatGenerator.next(1, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(160);b.setY(-20);
                  test1.add(b);
                  }
                  b=beatGenerator.next(2, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(300);b.setY(-20);
                  test2.add(b);
                  }
                  b=beatGenerator.next(3, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(440);b.setY(-20);
                  test3.add(b);
                  }
               //
               holder++;
               }
               if(nowdigit<9){
                 nowdigit=nowdigit+1;
               }else{
                 nowsec++;
                 nowdigit=0;
               }
               timer=0;
          }else{
               ++timer;
          }
          //check beats got hit or miss
          ////can not use for loop due to linklist will be null whne it is empty
          if(allow[0]&&key[0]){
             if(!test0.isEmpty()&&test0.peekFirst()!=null&&test0.get(0).getY()>=400&&test0.get(0).getY()-400<=range){
                 //System.out.println("hit");
                 waitdel.add(test0.pollFirst());
                 allow[0]=false;
                 key[0]=false;
             }
          }
          if(!test0.isEmpty()&&test0.peekFirst()!=null&&test0.get(0).getY()>=400&&test0.get(0).getY()-400>range){
                 //System.out.println("miss");
                 out.add(test0.pollFirst());
          }
          if(allow[1]&&key[1]){
             if(!test1.isEmpty()&&test1.peekFirst()!=null&&test1.get(0).getY()>=400&&test1.get(0).getY()-400<=range){
                 //System.out.println("hit");
                 waitdel.add(test1.pollFirst());
                 allow[1]=false;
                 key[1]=false;
             }
          }
          if(!test1.isEmpty()&&test1.peekFirst()!=null&&test1.get(0).getY()>=400&&test1.get(0).getY()-400>range){
                 //System.out.println("miss");
                 out.add(test1.pollFirst());
          }
          if(allow[2]&&key[2]){
             if(!test2.isEmpty()&&test2.peekFirst()!=null&&test2.get(0).getY()>=400&&test2.get(0).getY()-400<=range){
                 //System.out.println("hit");
                 waitdel.add(test2.pollFirst());
                 allow[2]=false;
                 key[2]=false;
             }
          }
          if(!test2.isEmpty()&&test2.peekFirst()!=null&&test2.get(0).getY()>=400&&test2.get(0).getY()-400>range){
                 //System.out.println("miss");
                 out.add(test2.pollFirst());
          }
          if(allow[3]&&key[3]){
             if(!test3.isEmpty()&&test3.peekFirst()!=null&&test3.get(0).getY()>=400&&test3.get(0).getY()-400<=range){
                 //System.out.println("hit");
                 waitdel.add(test3.pollFirst());
                 allow[3]=false;
                 key[3]=false;
             }
          }
          if(!test3.isEmpty()&&test3.peekFirst()!=null&&test3.get(0).getY()>=400&&test3.get(0).getY()-400>range){
                 //System.out.println("miss");
                 out.add(test3.pollFirst());
          }
           //
           //move beats
           test0.forEach((b) -> {
               b.moveOffset(0,10);
           });
           test1.forEach((b) -> {
               b.moveOffset(0,10);
           });
           test2.forEach((b) -> {
               b.moveOffset(0,10);
           });
           test3.forEach((b) -> {
               b.moveOffset(0,10);
           });
           out.forEach((b) -> {
               b.moveOffset(0,10);
           });
          //
          while(!waitdel.isEmpty()&&waitdel.peekFirst()!=null&&waitdel.peekFirst().stretch>=5){
               waitdel.pollFirst();
          }
          waitdel.forEach((b) -> {
              b.stretch();
           });
          //
          //repaint and wait if in need
          repaint();
          while (speaker.getFramePosition() < index+framelimit) {
                  Thread.sleep(5);
          }
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
