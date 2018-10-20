/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 *
 * @author 70136
 */
public class swingcanvas extends JComponent{
    JFrame frame;
    int[][] paintarr=new int[1][1];
    JLabel tlabel;
    JLabel nlabel;
    int second;
    int min;
    boolean isterminated;
    int opt=0;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates. 
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        for(int ch=0;ch<paintarr.length;ch++){
          int d=ch==1?200:450;
        for(int i=0;i<paintarr[ch].length-1;i++){
        int y1=paintarr[ch][i]/250,y2=paintarr[ch][i+1]/250;

        y1=(y1>=0)?d-y1:d-y1;
        y2=(y2>=0)?d-y2:d-y2;
        
        if(opt==0){
        g.drawLine(i*3, y1,(i+1)*3, y2);}
        else{
        g.drawLine(i*3, y1,(i+1)*3, y1);
        g.drawLine((i+1)*3, y1,(i+1)*3, y2);}
        }
        }
        int d=1000;
        double[][] freq=new double[paintarr.length][paintarr[0].length];
        for(int ch=0;ch<paintarr.length;ch++){
            freq[ch]=Arrays.stream(paintarr[ch]).mapToDouble(dd->{return dd;}).toArray();
            freq[ch]=caculatefft.getMagnitudes(freq[ch]);
            freq[ch]=Arrays.stream(freq[ch]).map(dd->{return dd/d>=140?(dd-d*140)/4+d*40:dd/d>=80?dd-d*40:dd/d>=40?dd-d*15:dd/d>=25?dd-d*5:dd;}).toArray();
                  g.drawLine(832+50,250*(ch+1), 832+50+freq[ch].length*15/2,250*(ch+1) );
              for(int i=0;i<freq[ch].length-1;i++){
                  int y1=250*(ch+1)-(int)freq[ch][i]/d;
                  int y2=250*(ch+1)-(int)freq[ch][i+1]/d;
                  g.drawLine((i)*15/2+832+50, y1,(i)*15/2+832+50,250*(ch+1));
                  g.drawLine((i)*15/2+832+50, y1,(i+1)*15/2+832+50,y1);
                  g.drawLine((i+1)*15/2+832+50, y1,(i+1)*15/2+832+50,y2);
                  g.drawLine((i+1)*15/2+832+50, y1,(i+1)*15/2+832+50,250*(ch+1));
              }
                  g.drawLine((freq[ch].length-1)*15/2+832+50, 250*(ch+1)-(int)freq[ch][freq[ch].length-1]/d, (freq[ch].length)*15/2+832+50,250*(ch+1)-(int)freq[ch][freq[ch].length-1]/d );
                  g.drawLine((freq[ch].length)*15/2+832+50, 250*(ch+1)-(int)freq[ch][freq[ch].length-1]/d, (freq[ch].length)*15/2+832+50, 250*(ch+1));
        }
        
    }
    
    
    
    public  void update(int[][] arr){
       paintarr=arr;
       repaint();
    }

    public swingcanvas(int len,int channel,String name,long microsec) {
        paintarr=new int[channel][128*2];
        frame=new JFrame();
        microsec/=1000000;
        min=(int)microsec/60;
        second=(int)microsec-60*min;
        tlabel=new JLabel("00:00 / "+String.format("%02d:%02d",min,second));
        nlabel=new JLabel("now playing: "+name);
        frame.setLayout(null); 
        frame.getContentPane().add(this);
        this.setLocation(0, 125);
        this.setSize(800+50+16*15*2+50,600);
        this.setBorder(new BevelBorder(1));
        frame.getContentPane().add(nlabel).setFont(new Font("", 1, 20));
        nlabel.setLocation(0,0);
        nlabel.setSize(800,25);
        frame.getContentPane().add(tlabel).setFont(new Font("", 1, 20));
        tlabel.setLocation(0, 25);
        tlabel.setSize(800,25);
        JButton terminbtn=new JButton("terminate");
        JButton optbtn=new JButton("change to discrete wave");
        optbtn.addActionListener((ActionEvent e) -> {
                  if(opt==0){
                  optbtn.setText("change to curve wave");
                  opt=1;
                  }else{
                  optbtn.setText("change to discrete wave");
                  opt=0;
                  }
        });
        terminbtn.addActionListener((ActionEvent e) -> {isterminated=true;});
        frame.add(terminbtn);
        frame.add(optbtn);
        terminbtn.setLocation(0, 50);
        terminbtn.setSize(100,50);
        optbtn.setLocation(120, 50);
        optbtn.setSize(200,50);
        JLabel plabel=new JLabel("pcm wave");
        frame.add(plabel).setFont(new Font("", 1, 20));
        plabel.setSize(200,25);
        plabel.setLocation(this.getWidth()/5,100);
        JLabel flabel=new JLabel("frequency");
        frame.add(flabel).setFont(new Font("", 1, 20));
        flabel.setSize(200,25);
        flabel.setLocation(this.getWidth()/4*3,100);
        frame.setSize(832+50+16*15*2+100, 800);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setAlwaysOnTop(false);
    }
    
    public void settime(long microsec){
           microsec/=1000000;
           int nmin=(int)microsec/60;
           int nsec=(int)microsec-nmin*60;
           tlabel.setText(String.format("%02d:%02d / %02d:%02d",nmin,nsec,min,second));
    }
    
    public void end(){
       frame.setVisible(false);
       frame.dispose();
       frame=null;
       paintarr=null;
    }
    
    public boolean isstop(){
         return isterminated;
    }

}
