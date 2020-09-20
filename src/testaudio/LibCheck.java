package testaudio;


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.shape.Path;

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
    
    public static String[] check_lib() {
        String[] libs={"jl1.0.1.jar",
        "JTransforms-3.1-with-dependencies.jar",
        "mp3spi1.9.5.jar",
        "tritonus_share.jar"
        };
        
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
}
