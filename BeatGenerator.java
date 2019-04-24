/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 陳品修
 */
public class BeatGenerator {

    File arrange;
    ArrayList<String> note;
    ArrayList<String> notelist;
    ArrayList<String> timing;
    int holder=0;
    public  BeatGenerator() {
        arrange=new File("arrange.txt");
        FileReader fileReader;
        note=new ArrayList<String>();
        notelist=new ArrayList<String>();
        timing=new ArrayList<String>();
        try {
            fileReader = new FileReader(arrange);
            BufferedReader br=new BufferedReader(fileReader);
            String n;
            n=br.readLine();
            while(true){
                n=br.readLine();
                if(n.equals("track end")){
                    break;
                }
                else{
                    note.add(n);
                }
            }
            br.close();
            fileReader.close();
            while(!note.isEmpty()){
                String[] ThisLine=note.get(0).split(":");
                for(int i=1;i<ThisLine.length;i++){
                    notelist.add(ThisLine[0]+","+ThisLine[i]);
                }
                note.remove(0);
                timing.add(ThisLine[0]);
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(BeatGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public ArrayList<String> getTiming(){
       return timing;
    }
    
    public Beat next(int trackid,int nowsec,int nowdigit){
        if(holder<notelist.size()){
            String l=notelist.get(holder);
            String[] line=l.split(",");
            if((line[0].equals(nowsec+"."+nowdigit))&&(Integer.parseInt(line[1])==trackid)){
               float score=Float.parseFloat(line[3]);
               Beat obj=new Beat(0,score);
               holder++;
               return obj;
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }
    
}
