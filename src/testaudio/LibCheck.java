package testaudio;


import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 70136
 */
public class LibCheck {
    //all libs
    private static final String[] libs={"jl1.0.1.jar",
        "JTransforms-3.1-with-dependencies.jar",
        "mp3spi1.9.5.jar",
        "tritonus_share.jar"
    };
    
    /**
     *check and download(recover) missing libs
     * @param context - base gui component for JOptionPane
     */
    static void check_lib(Component context) {
        String[] missing = check();
        if (missing == null || missing.length == 0) {
            //JOptionPane.showMessageDialog(this, "no missing libs");
        } else {
            String missmsg = "missing libs:";
            for (String lib : missing) {
                missmsg += "\n" + lib;
            }
            missmsg += "\ntry to recover !";
            JOptionPane.showMessageDialog(context, missmsg);
            try {
                for (String lib : missing) {
                    Download.download_lib(lib);
                }
                JOptionPane.showMessageDialog(context, "recover successfully!");
                restart();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(context, "network error!\n plz go to download and place files at lib/");
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/joejoe2/wav-pcm/tree/master/lib"));
                } catch (Exception ex1) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }
    
    /**
     *check  missing libs in 'lib/' folder
     * @return - missing libs
     */
    static String[] check() {
        
        ArrayList<String> missing=new ArrayList<>();
        
        for (String lib : libs) {
           if(!new File("lib/"+lib).exists()){
               missing.add(lib);
           }
        }
        
        String[] result=new String[0];
        result=missing.toArray(result);
        return result;
    }
    
    /**
     *restart app to complete libs recover 
     * @throws IOException
     */
    static void restart() throws IOException {
        Runtime.getRuntime().exec("java -jar run.jar");
        System.exit(0);
    }
}
