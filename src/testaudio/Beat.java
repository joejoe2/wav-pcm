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

    

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }
    private float x;
    private float y;
    int length;
    int width;
    float score;
    int stretch=0;
    int line;
    boolean bomb=false;
    
    public Beat(int type,float score,int line) {
        this.score=score;
        if(type==0){
          width=1;
          length=1;
        }
        this.line=line;
        
    }
    
    public void draw(Graphics g){
       if(!bomb){
        g.fillOval((int)getX(), (int)getY(), width, length);
       }else{
            
            for(int i=0;i<10;i++){
            g.drawOval((int)x,(int)y, width-i, length-i);
            }
        }
    }
    
    public void moveOffset(float dx,float dy){
        setX(getX() + dx);
        setY(getY() + dy);
    }
    
    public void stretch(){
         if(bomb)
        {
                width+=10;
                length+=10;
                ++stretch;
                moveOffset(-5f,-5f);
        }else{
         width+=1;
         length+=1;
         }
        
    }
    
}
