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
import java.awt.image.BufferedImage;
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

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

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
    JLabel scoreJLabel;
    JLabel comboJLabel;
    int nowsec=0;//record time in sec
    int nowdigit=0;//record time in digit
    int timer=1;//use to count time
    ArrayList<String> timing;//record when to check generate beats
    int holder=0;//hold the index of timing
    int range=40;//hit range
    float nowscore=0;
    int nowcombo=0;
    int [] scBound={5,10,15};
    Color lineColor=new Color(155,155,155,100);
    Color bgColor=new Color(100,100,255);
    Color boundColor=Color.white;
    Color effectColor=new Color(255, 255,56);
    Color beatColor=Color.GREEN;
    ImageIcon pic0,pic1,pic2,pic3;
    BufferedImage bg;
    JLabel lb0,lb1,lb2,lb3;
    JPanel picpanel;
    
    int px2[]={250,0,500,250};//首末点相重,才能画多边形 
    int py2[]={100,600,600,100};
    int px1[]={50,0,500,450,50};//首末点相重,才能画多边形 
    int py1[]={500,600,600,500,500};
    @Override
    protected void paintComponent(Graphics g) {//suggested to override paintComponent instead of paint
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        g.drawImage(bg,0,0,getWidth(),getHeight(),1600/3,0,1600/3+800,896,null);
        
                
        g.setColor(new Color(25,25,25,150));
        g.fillPolygon(px2,py2,4); 
        
        g.setColor(lineColor);
        g.fillPolygon(px1,py1,5);
        
        
        g.setColor(boundColor);
        g.drawLine(250,100,0,600);
        g.drawLine(250,100,125,600);
        g.drawLine(250,100,250,600);
        g.drawLine(250,100,375,600);
        g.drawLine(250,100,500,600);
        
        
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
            b.bomb=true;
        });
        
    }

    
 
    public GameWindow(File f,File arrange) throws Exception{
        //
        beatGenerator=new BeatGenerator(arrange);
        timing=beatGenerator.getTiming();
        test0=new LinkedList<Beat>();
        test1=new LinkedList<Beat>();
        test2=new LinkedList<Beat>();
        test3=new LinkedList<Beat>();
        waitdel=new LinkedList<Beat>();
        out=new LinkedList<Beat>();
        
        bg=ImageIO.read(getClass().getResource("/res/starry_sky2.jpg"));
        
        pic0=new ImageIcon(ImageIO.read(getClass().getResource("/res/pic0.gif")));
        pic1=new ImageIcon(ImageIO.read(getClass().getResource("/res/pic1.gif")));
        pic2=new ImageIcon(ImageIO.read(getClass().getResource("/res/pic2.gif")));
        pic3=new ImageIcon(ImageIO.read(getClass().getResource("/res/pic3.gif")));
        
        
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
        frame.setSize(800, 600);
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
        secJLabel=new JLabel("0.0");
        secJLabel.setSize(200,50);
        secJLabel.setLocation(0,200);
        secJLabel.setFont(new Font("",1,15));
        secJLabel.setForeground(Color.white);
        frame.add(secJLabel);
        //
        scoreJLabel=new JLabel("0");
        scoreJLabel.setSize(200,50);
        scoreJLabel.setLocation(0,100);
        scoreJLabel.setFont(new Font("",1,15));
        scoreJLabel.setForeground(Color.white);
        frame.add(scoreJLabel);
        //
        comboJLabel=new JLabel("0");
        comboJLabel.setSize(200,50);
        comboJLabel.setLocation(0,150);
        comboJLabel.setFont(new Font("",1,15));
        comboJLabel.setForeground(Color.white);
        frame.add(comboJLabel);
        //
        picpanel=new JPanel();
        picpanel.setBackground(Color.GRAY);
        picpanel.setSize(250,250);
        picpanel.setLocation(0,300);
        frame.getContentPane().add(picpanel);
        //
        lb0=new JLabel(pic0);
        lb0.setVisible(false);
        picpanel.add(lb0);
        lb1=new JLabel(pic1);
        lb1.setVisible(false);
        picpanel.add(lb1);
        lb2=new JLabel(pic2);
        lb2.setVisible(false);
        picpanel.add(lb2);
        lb3=new JLabel(pic3);
        lb3.setVisible(false);
        picpanel.add(lb3);
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
        this.setLocation(300,0);
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
          secJLabel.setText("time:"+nowsec+"."+nowdigit);
          scoreJLabel.setText("Score : "+(int)nowscore);
          comboJLabel.setText("Combo : "+nowcombo);
          ////generate beats check and update time
          if(timer==1){
               if(holder<timing.size()&&timing.get(holder).equals(nowsec+"."+nowdigit)){
               //can not use for loop due to linklist will be null whne it is empty
                  Beat b=beatGenerator.next(0, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(250);b.setY(100);
                  test0.add(b);
                  }
                  b=beatGenerator.next(1, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(250);b.setY(100);
                  test1.add(b);
                  }
                  b=beatGenerator.next(2, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(250);b.setY(100);
                  test2.add(b);
                  }
                  b=beatGenerator.next(3, nowsec, nowdigit);
                  if(b!=null){
                  b.setX(250);b.setY(100);
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
             if(!test0.isEmpty()&&test0.peekFirst()!=null&&test0.get(0).getY()>=500&&test0.get(0).getY()-500<=range){
                 //System.out.println("hit");
                 nowcombo=nowcombo+1;
                 if(nowcombo>=scBound[2]){
                     nowscore=nowscore+4*test0.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(true);
                 }
                 else if(nowcombo>=scBound[1]){
                     nowscore=nowscore+3*test0.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(true); lb3.setVisible(false);
                 }
                 else if(nowcombo>=scBound[0]){
                     nowscore=nowscore+2*test0.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(true); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 else{
                     nowscore=nowscore+test0.getFirst().score;
                     lb0.setVisible(true); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 waitdel.add(test0.pollFirst());
                 allow[0]=false;
                 key[0]=false;
             }
          }
          if(!test0.isEmpty()&&test0.peekFirst()!=null&&test0.get(0).getY()>=500&&test0.get(0).getY()-500>range){
                 //System.out.println("miss");
                 out.add(test0.pollFirst());
                 //lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 //nowcombo=0;
          }
          if(allow[1]&&key[1]){
             if(!test1.isEmpty()&&test1.peekFirst()!=null&&test1.get(0).getY()>=500&&test1.get(0).getY()-500<=range){
                 //System.out.println("hit");
                 nowcombo=nowcombo+1;
                 if(nowcombo>=scBound[2]){
                     nowscore=nowscore+4*test1.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(true);
                 }
                 else if(nowcombo>=scBound[1]){
                     nowscore=nowscore+3*test1.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(true); lb3.setVisible(false);
                 }
                 else if(nowcombo>=scBound[0]){
                     nowscore=nowscore+2*test1.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(true); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 else{
                     nowscore=nowscore+test1.getFirst().score;
                     lb0.setVisible(true); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 waitdel.add(test1.pollFirst());
                 allow[1]=false;
                 key[1]=false;
             }
          }
          if(!test1.isEmpty()&&test1.peekFirst()!=null&&test1.get(0).getY()>=500&&test1.get(0).getY()-500>range){
                 //System.out.println("miss");
                 out.add(test1.pollFirst());
                 //lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 //nowcombo=0;
          }
          if(allow[2]&&key[2]){
             if(!test2.isEmpty()&&test2.peekFirst()!=null&&test2.get(0).getY()>=500&&test2.get(0).getY()-500<=range){
                 //System.out.println("hit");
                 nowcombo=nowcombo+1;
                 if(nowcombo>=scBound[2]){
                     nowscore=nowscore+4*test2.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(true);
                 }
                 else if(nowcombo>=scBound[1]){
                     nowscore=nowscore+3*test2.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(true); lb3.setVisible(false);
                 }
                 else if(nowcombo>=scBound[0]){
                     nowscore=nowscore+2*test2.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(true); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 else{
                     nowscore=nowscore+test2.getFirst().score;
                     lb0.setVisible(true); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 waitdel.add(test2.pollFirst());
                 allow[2]=false;
                 key[2]=false;
             }
          }
          if(!test2.isEmpty()&&test2.peekFirst()!=null&&test2.get(0).getY()>=500&&test2.get(0).getY()-500>range){
                 //System.out.println("miss");
                 out.add(test2.pollFirst());
                 //lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 //nowcombo=0;
          }
          if(allow[3]&&key[3]){
             if(!test3.isEmpty()&&test3.peekFirst()!=null&&test3.get(0).getY()>=500&&test3.get(0).getY()-500<=range){
                 //System.out.println("hit");
                 nowcombo=nowcombo+1;
                 if(nowcombo>=scBound[2]){
                     nowscore=nowscore+4*test3.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(true);
                 }
                 else if(nowcombo>=scBound[1]){
                     nowscore=nowscore+3*test3.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(true); lb3.setVisible(false);
                 }
                 else if(nowcombo>=scBound[0]){
                     nowscore=nowscore+2*test3.getFirst().score;
                     lb0.setVisible(false); lb1.setVisible(true); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 else{
                     nowscore=nowscore+test3.getFirst().score;
                     lb0.setVisible(true); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 }
                 waitdel.add(test3.pollFirst());
                 allow[3]=false;
                 key[3]=false;
             }
          }
          if(!test3.isEmpty()&&test3.peekFirst()!=null&&test3.get(0).getY()>=500&&test3.get(0).getY()-500>range){
                 //System.out.println("miss");
                 out.add(test3.pollFirst());
                 //lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
                 //nowcombo=0;
          }
          //
          //clear out beats
          if(!out.isEmpty()){
            out.clear();
            lb0.setVisible(false); lb1.setVisible(false); lb2.setVisible(false); lb3.setVisible(false);
            nowcombo=0;
          }
           //
           //move beats
           test0.forEach((b) -> {
               b.moveOffset(-5.2f,10f);//-5.2
               b.stretch();
           });
           test1.forEach((b) -> {
               b.moveOffset(-2.3f,10f);//-2.3
               b.stretch();
           });
           test2.forEach((b) -> {
               b.moveOffset(0.4f,10f);//0.4
               b.stretch();
           });
           test3.forEach((b) -> {
               b.moveOffset(3.1f,10f);//3.1
               b.stretch();
           });
           out.forEach((b) -> {
               b.stretch();
           });
          //use to contorl effect stage
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
       waitdel.clear();
       
       speaker.stop();
       speaker.close();
       JOptionPane.showMessageDialog(this,"your score: "+(int)nowscore);
        frame.setVisible(false);
        frame.dispose();
        frame=null;
        file=null;
        System.gc();
        return;
    }
}
