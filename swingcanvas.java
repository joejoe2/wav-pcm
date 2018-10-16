/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates. 
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        for(int ch=0;ch<paintarr.length;ch++){
          int d=ch==1?250:500;
        for(int i=0;i<paintarr[ch].length-1;i++){
        int y1=paintarr[ch][i]/100,y2=paintarr[ch][i+1]/100;

        y1=(y1>=0)?d-y1:d+y1;
        y2=(y2>=0)?d-y2:d+y2;
        
        g.drawLine(i*10, y1,(i+1)*10, y2);
        }
        }
    }
    
    
    
    public  void update(int[][] arr){
       
       paintarr=arr;
       repaint();
       
    }

    public swingcanvas(int len,int channel,String name,long microsec) {
        paintarr=new int[channel][2];
        frame=new JFrame();
        microsec/=1000000;
        min=(int)microsec/60;
        second=(int)microsec-60*min;
        tlabel=new JLabel("00:00 / "+String.format("%02d:%02d",min,second));
        nlabel=new JLabel("now playing: "+name);
        frame.setLayout(null); 
        frame.getContentPane().add(this);
        this.setLocation(0, 100);
        this.setSize(800,600);
        frame.getContentPane().add(nlabel).setFont(new Font("", 1, 20));
        nlabel.setLocation(0,0);
        nlabel.setSize(800,25);
        frame.getContentPane().add(tlabel).setFont(new Font("", 1, 20));
        tlabel.setLocation(0, 25);
        tlabel.setSize(800,25);
        JButton terminbtn=new JButton("terminate");
        terminbtn.addActionListener((ActionEvent e) -> {isterminated=true;});
        frame.add(terminbtn);
        terminbtn.setLocation(0, 50);
        terminbtn.setSize(100,50);
        frame.setSize(800, 700);
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
