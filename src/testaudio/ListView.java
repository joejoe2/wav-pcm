/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author 70136
 */
public class ListView extends JPanel{

    public ListView(ArrayList<File> list,Gui holder) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for(int i=0;i<list.size();i++){
            ListItem item=new ListItem(list.get(i).getName(),list.get(i), holder);
            this.add(item);
            item.setSize(200,50);
            
        }
    }
    
    public void update(ArrayList<File> list,Gui holder){
           this.removeAll();
           for(int i=0;i<list.size();i++){
            ListItem item=new ListItem(list.get(i).getName(),list.get(i), holder);
            this.add(item);
            item.setSize(200,50);
            
        }
    }
    
    
}
