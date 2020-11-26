/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 70136
 */
public class Recoord {

    /**
     *store the selected searching folder to 'setting.ini'
     * @param folder - selected searching folder
     */
    static void writeRecord(File folder) {
        if (new File("setting.ini").exists() && folder != null && folder.exists()) {
            File ini = new File("setting.ini");
            try {
                ini.createNewFile();
                PrintWriter printWriter = new PrintWriter(ini, "UTF-8");
                printWriter.println("search folder:" + folder);
                printWriter.flush();
                printWriter.close();
                System.gc();
            } catch (Exception e) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     *read the last selected searching folder in 'setting.ini'
     * @return - selected searching folder last time
     */
    static File readRecord() {
        if (new File("setting.ini").exists()) {
            File ini = new File("setting.ini");
            try {
                FileInputStream in = new FileInputStream(ini);
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                String str = bufferedReader.readLine();
                in.close();
                reader.close();
                bufferedReader.close();
                str = str.substring(14);
                System.gc();
                if ("null".equals(str)) {
                    return null;
                } else if (new File(str).exists()) {
                    return new File(str);
                }
            } catch (Exception ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

        } else {
            File ini = new File("setting.ini");
            try {
                ini.createNewFile();
                PrintWriter printWriter = new PrintWriter(ini);
                printWriter.println("search folder:null");
                printWriter.flush();
                printWriter.close();
                System.gc();
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        return null;
    }
}
