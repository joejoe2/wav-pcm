/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author 70136
 */
public class swingcanvas extends JComponent{
    JFrame frame;
    int[] paintarr=new int[100];
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        for(int i=0;i<99;i++){
        int y1=paintarr[i]/100,y2=paintarr[i+1]/100;

        y1=(y1>=0)?250-y1:250+y1;
        y2=(y2>=0)?250-y2:250+y2;
        
        g.drawLine(i*10, y1,(i+1)*10, y2);
        }
    }
    
    
    
    public  void update(int[] arr){
       paintarr=arr;
       repaint();
       
    }

    public swingcanvas() {
        frame=new JFrame();
        frame.getContentPane().add(this);
        frame.setSize(720, 560);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
