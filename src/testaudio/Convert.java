/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author 70136
 */
public class Convert {

    public static File mp3ToWav(File mp3Data) throws UnsupportedAudioFileException, IOException {
        // open stream
        AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3Data);
        AudioFormat sourceFormat = mp3Stream.getFormat();
        // create audio format object for the desired stream/audio format
        // this is *not* the same as the file format (wav)
        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(), 16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);
        // create stream that delivers the desired format
        AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
        // write stream into a file with file format wav
        File f = new File(mp3Data.getName().substring(0, mp3Data.getName().lastIndexOf(".")));
        AudioSystem.write(converted, Type.WAVE, f);
        mp3Stream.close();
        converted.close();
        System.gc();
        return f.getAbsoluteFile();
    }

}
