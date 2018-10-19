/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 70136
 */
public class updatecheck {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //System.out.println(getver("https://github.com/joejoe2/wav-pcm"));
    }
    
    public static String getver(){
        String url="https://github.com/joejoe2/wav-pcm";
        int result=-1;
        String line=null;
        try {
            URL link=new URL(url);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()))) {
                String str;
                while((str=br.readLine())!=null){
                        result = str.indexOf("version-");
                        if (result != -1) {
                            line = str;
                            break;
                        }
                    }
                    if (result == -1) {
                        return null;
                    }
                    return line.substring(result + 8, line.indexOf(".txt"));
            }
            catch (IOException ex) {
            Logger.getLogger(updatecheck.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(Exception e){
               return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(updatecheck.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
}
