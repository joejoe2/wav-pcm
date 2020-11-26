/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import org.jtransforms.fft.DoubleFFT_1D;

/**
 *
 * @author 70136
 */
public class Utils {
    /**
     * seach all .mp3 or .wav in target folder
     * @param folder - seaching target
     * @return ArrayList - found files
     */
    public static ArrayList<File> listAllFile(File folder) {
        ArrayList<File> filelist=new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return filelist;
        }
        //sort files and dirs
        Utils.sort_by_creationTime(files, true);
        for (File s : files) {
            if (s.isDirectory()) {
                filelist.addAll(listAllFile(s));
            } else if (s.getName().endsWith(".mp3") || s.getName().endsWith(".wav")) {
                filelist.add(s);
            }
        }
        
        return filelist;
    }
    
    /**
     *combine two 8 bit byte into 16 bit short int
     * @param high - high byte
     * @param low - low byte
     * @return - 16 bit short int in [high:low]
     */
    public static short get16bitnum(byte high, byte low) {
        short num = 0;
        short h = high;
        short l = low;
        num |= l;
        h <<= 8;
        num |= h;
        return num;
    }
    
    /**
     * remove signal's dc effect
     *
     * @param array - input sequence
     */
    public static void remove_dc(int[] array) {
        int mean = 0;

        for (int d : array) {
            mean += d;
        }
        mean /= array.length;
        for (int i = 0; i < array.length; i++) {
            array[i] -= mean;
        }

    }

    /**
     * adjust abs db with human perception
     *
     * @param f - frequency
     * @param db - decibel
     * @return weighted db
     */
    public static double apply_weighting(double f, double db) {
        //https://stason.org/TULARC/physics/acoustics-faq/8-1-Formula-for-A-weighting.html
        //https://en.wikipedia.org/wiki/A-weighting
        //use b-weighting
        double raf = Math.pow(12194, 2) * Math.pow(f, 3) / (f * f + 20.6 * 20.6) / (f * f + 12194 * 12194) / Math.sqrt((f * f + 158.5 * 158.5));
        raf = 20 * Math.log10(raf) + 0.17;
        return db + raf;
    }

    /**
     * smooth the array
     *
     * @param array - input sequence
     * @param prev - previous instant input sequence
     * @param factor - smoothing factor
     */
    public static void smooth(double[] array, double[] prev, double factor) {
        for (int i = 1; i < array.length; i++) {
            array[i] =factor * array[i - 1] + (1 - factor) * array[i];
        }
    }
    
    /**
     *  convert magnitude to db
     * @param array - input mag sequence
     */
    public static double [] mag_to_db(double [] array) {
        return Arrays.stream(array).map(dd -> {
                double scaled = 10 * Math.log10(dd);
                return scaled;
            }).toArray();
    }
    
    /**
     * Get the frequency intensities by fft
     *
     * @param amplitudes amplitudes of the signal
     * @return intensities of each frequency unit: mag[frequency_unit]=intensity
     */
    public static double[] getMagnitudes(double[] amplitudes) {

        int sampleSize = amplitudes.length;//amplitudes.length must me 2^n!!!!!!!!!
        
        DoubleFFT_1D FFT=new DoubleFFT_1D(sampleSize);
        // execute fft
        double[] mag= new double[sampleSize/2];
	FFT.realForward(amplitudes);
	for(int i = 0; i < sampleSize/2; i+=2) {
            mag[i/2]=Math.sqrt(Math.pow(amplitudes[i], 2)+Math.pow(amplitudes[i+1], 2));
	}
        
        return mag;
    }
    
    /**
     * sort files by creationTime
     * @param files - file array
     * @param isDescending - use descending or ascending order
     */
    public static void sort_by_creationTime(File[] files, boolean isDescending) {
        int flag=isDescending?1:-1;
        Arrays.sort(files,new Comparator<File>(){
            //sort into descending order
            @Override
            public int compare(File o1, File o2) {
                try {
                    FileTime t1=(FileTime) Files.getAttribute(o1.toPath(), "creationTime");
                    FileTime t2=(FileTime) Files.getAttribute(o2.toPath(), "creationTime");
                    return flag*t2.compareTo(t1);
                } catch (IOException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
                return 0;
            }
        });
    }
    
    /**
     *  sort files by lastModifiedTime
     * @param files - file array
     * @param isDescending - use descending or ascending order
     */
    public static void sort_by_lastModifiedTime(File[] files, boolean isDescending) {
        if(isDescending){
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        }else{
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        }
    }
    
}
