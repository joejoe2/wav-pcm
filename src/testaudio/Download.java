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
public class Download {

    /**
     * download the latest version app
     */
    static void download_update() throws Exception {
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
    
    /**
     * download the lib
     * @param lib_name - file name of the lib
     * @throws java.lang.Exception
     */
    static void download_lib(String lib_name) throws Exception {
        URL link = new URL("https://github.com/joejoe2/wav-pcm/raw/master/lib/"+lib_name);
        HttpURLConnection con = (HttpURLConnection) link.openConnection();
        BufferedInputStream buf = new BufferedInputStream(con.getInputStream());
        FileOutputStream fout = new FileOutputStream("lib/"+lib_name);
        int l = 0;
        byte[] bytes = new byte[4096];
        while ((l = buf.read(bytes)) != -1) {
            fout.write(bytes, 0, l);
        }
        buf.close();
        fout.close();
    }
}
