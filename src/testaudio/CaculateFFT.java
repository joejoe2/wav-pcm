/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

//import com.sun.media.sound.FFT;

/**
 *
 * @author 70136
 */
public class CaculateFFT {

    /**
     * Get the frequency intensities
     *
     * @param amplitudes amplitudes of the signal
     * @return intensities of each frequency unit: mag[frequency_unit]=intensity
     */
    public static double[] getMagnitudes(double[] amplitudes) {

        int sampleSize = amplitudes.length;//amplitudes.length must me 2^n!!!!!!!!!

        // call the fft and transform the complex numbers
        FFT fft = new FFT(sampleSize / 2, -1);
        fft.transform(amplitudes);
        // end call the fft and transform the complex numbers

        // even indexes (0,2,4,6,...) are real parts
        // odd indexes (1,3,5,7,...) are img parts
        int indexSize = sampleSize / 2;

        // FFT produces a transformed pair of arrays where the first half of the
        // values represent positive frequency components and the second half
        // represents negative frequency components.
        // we omit the negative ones
        int positiveSize = indexSize / 2;

        double[] mag = new double[positiveSize];
        for (int i = 0; i < indexSize; i += 2) {
            mag[i / 2] = Math.sqrt(amplitudes[i] * amplitudes[i] + amplitudes[i + 1] * amplitudes[i + 1]);
        }

        return mag;
    }
}
