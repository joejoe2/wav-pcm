/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author 70136
 */
public class DownloadTest {

    /**
     * @param args the command line arguments
     */
    public static void autoupdate() throws Exception {
        // TODO code application logic here
        URL link = new URL("https://github.com/joejoe2/wav-pcm/raw/master/TestAudio.jar");
        HttpURLConnection con = (HttpURLConnection) link.openConnection();
        BufferedInputStream buf = new BufferedInputStream(con.getInputStream());
        FileOutputStream fout = new FileOutputStream("TestAudio.jar");
        int l = 0;
        byte[] bytes = new byte[4096];
        while ((l = buf.read(bytes)) != -1) {
            fout.write(bytes, 0, l);
        }
        buf.close();
        fout.close();
    }

}
