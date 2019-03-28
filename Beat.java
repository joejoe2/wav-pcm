/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Graphics;

/**
 *
 * @author 70136
 */
public class Beat {
    float x,y;
    int length;
    int width;
    
    public Beat(float x,float y,int type) {
        this.x=x;
        this.y=y;
        if(type==0){
          width=10;
          length=40;
        }
    }
    
    public void draw(Graphics g){
       g.drawRect((int)x, (int)y, width, length);
    }
    
    public void moveOffset(float dx,float dy){
       x+=dx;
       y+=dy;
    }
}
