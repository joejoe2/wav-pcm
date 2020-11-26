/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author 70136
 */
public class UpdateCheck {

    static final float version = 1.08f;

    /**get the latest vesion num in string
     * @return - null or a flaot string
     */
    public static String getver() {
        String url = "https://github.com/joejoe2/wav-pcm";
        int result = -1;
        String line = null;
        try {
            URL link = new URL(url);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()))) {
                String str;
                while ((str = br.readLine()) != null) {
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
            } catch (IOException ex) {
                Logger.getLogger(UpdateCheck.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(UpdateCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *check and select whether to update 
     * @param context - base gui component for JOptionPane
     */
    static void check_ver(Component context) {
        String ver = UpdateCheck.getver();
        if (ver == null) {
            JOptionPane.showMessageDialog(context, "network error!");
        } else if (version < Float.parseFloat(ver)) {
            JOptionPane.showMessageDialog(context, "                you have a new version(" + ver + ") of the application\nplease check <https://github.com/joejoe2/wav-pcm> for update");
            int opt = JOptionPane.showConfirmDialog(context, "do you want to download automatically?", "update check", JOptionPane.YES_NO_OPTION);
            try {
                if (opt == JOptionPane.YES_OPTION) {
                    Download.download_update();
                    JOptionPane.showMessageDialog(context, "update successfully!");
                    restart();
                } else {
                    Desktop.getDesktop().browse(new URI("https://github.com/joejoe2/wav-pcm"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(context, "network error!");
            }
        } else {
            JOptionPane.showMessageDialog(context, "you are latest now!");
        }
    }

    /**
     *restart app to complete update 
     * @throws IOException
     */
    static void restart() throws IOException {
        Runtime.getRuntime().exec("java -jar run.jar");
        System.exit(0);
    }
}
