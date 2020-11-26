/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JSlider;

/**
 *
 * @author 70136
 */
public class TestAudio {
    //gui analyze_window
    private AnalyzeWindow analyze_window;
    //control progress bar
    private JSlider slider;
    //target music file
    private final File file;
    //flag
    private boolean isadjusting;
    //moving target frame position in %
    private int target_percent = -1;
    //num of frames per loop step for analyze
    private final int step = 4096;
    //music streams
    private AudioInputStream play_stream;//for playing clip
    private AudioInputStream analyze_stream;//for live stream analyze
    //music clip
    private Clip speaker;
    //music info
    private AudioFormat format;
    private long framelength;
    private int framesize;
    private int samplerate;
    private int fhz_resolution;//bin width for fft
    private int max_freq;//available max freq for fft
    private int channel;
    private long total_len_ms;

    /**
     * create and set up music analyze
     *
     * @param file - analyze target music file
     * @throws java.lang.Exception - occur when unsupported file format or audio stream error
     */
    public TestAudio(File file) throws Exception {
        //target music file
        this.file = file;
        setup_analyze();
        setup_control();
    }

    /**
     * prepare to analyze
     *
     * @throws java.lang.Exception - occur when unsupported file format or audio stream error
     */
    private void setup_analyze() throws Exception {
        //open audio stream
        try {
            play_stream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        //get format info
        format = play_stream.getFormat();
        framelength = play_stream.getFrameLength();
        framesize = format.getFrameSize();
        samplerate = (int) format.getSampleRate();
        fhz_resolution = samplerate / step;
        max_freq = samplerate / 2;
        channel = format.getChannels();
        //get playing clip from play_stream
        speaker = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, format));
        try {
            speaker.open(play_stream);
        } catch (IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                play_stream.close();
            } catch (IOException ex) {
                Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //get total music time in ms
        total_len_ms = speaker.getMicrosecondLength();
        //get analyze_stream
        try {
            analyze_stream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * prepare gui window and control items
     *
     */
    private void setup_control() {
        //gui window
        analyze_window = new AnalyzeWindow(channel, file.getName().replaceAll(".wav", ""), speaker.getMicrosecondLength());
        //progress control bar
        slider = new JSlider(0, 100);//restrict to 0-100 (%)
        //listener for time positin dragging contorl
        slider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //set isadjusting flag when pressed
                setIsadjusting(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //get target time position(%) and reset isadjusting flag when released
                setTargettime(slider.getValue());
                setIsadjusting(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        slider.setBackground(Color.GRAY);
        slider.setForeground(Color.WHITE);
        analyze_window.frame.add(slider);
        slider.setSize(250, 15);
        slider.setLocation(500, 75);
    }

    /**
     *start to analyze
     * @throws Exception
     */
    public void start() throws Exception {
        //prepare temp var for analyze
        //buffer for live stream analyze read
        int BUFFER_SIZE = step * 2 * channel;
        byte[] bytesBuffer = new byte[BUFFER_SIZE];
        //fft and smoothing data array
        int[][] fftarr = new int[channel][step];
        double[][] pre = new double[channel][step];
        //how many bytes read from stream in buffer
        int bytesRead = -1;

        //analyze with fixed num of frames
        outer:
        for (long i = 0;; i += step) {
            //detect pause(may plus terminate)
            while (analyze_window.isstop()) {
                if (analyze_window.issterminated()) {
                    break outer;
                }
                speaker.stop();
                Thread.sleep(50);
            }

            //detect terminate
            if (analyze_window.issterminated()) {
                break;
            }
            //detect progress bar changed by user
            //move to target time position if target_percent is changed by progress bar
            if (target_percent != -1) {
                //target frame position
                long new_pos = target_percent * (framelength / 100);
                //if target position is after current's then simply skip to that target
                //else then reopen analyze_stream and skip to that target
                if (i < new_pos) {
                    analyze_stream.skip((new_pos - i) * framesize);
                } else {
                    analyze_stream = null;
                    try {
                        analyze_stream = AudioSystem.getAudioInputStream(file);
                    } catch (UnsupportedAudioFileException | IOException ex) {
                        Logger.getLogger(TestAudio.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    analyze_stream.skip(new_pos * framesize);
                }
                //update loop index
                i = new_pos;
                //update progress bar
                slider.setValue(target_percent);
                //reset target position
                target_percent = -1;
                //set clip to new position
                speaker.setFramePosition((int) i);
            } else {//if progress bar dose not changed by user, it should update as music going
                if (!isadjusting) {
                    slider.setValue((int) (i * 100.0 / framelength));
                }
            }
            
            //detect loop bound
            if(!(i + step < framelength)){
                break;
            }
            
            //the clip may be played
            //1.after paused above
            //2.after set new target position above
            //3.noramly(loop(0) has no effect in this codition)
            try {
                speaker.loop(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //read raw pcm data from analyze_stream
            if ((bytesRead = analyze_stream.read(bytesBuffer)) != -1) {
                //split into channels
                int[][] sample = new int[channel][bytesBuffer.length / 2 / channel];
                for (int j = 0; j < bytesBuffer.length;) {
                    for (int k = 0; k < channel; k++) {
                        sample[k][j / 2 / channel] = Utils.get16bitnum(bytesBuffer[j + 1], bytesBuffer[j]);
                        if (j < bytesBuffer.length) {
                            j += channel;
                        } else {
                            break;
                        }
                    }
                }
                //compute fft
                fftarr = sample;
                double[][] freq = new double[fftarr.length][fftarr[0].length];
                for (int ch = 0; ch < fftarr.length; ch++) {
                    freq[ch] = Arrays.stream(fftarr[ch]).mapToDouble(dd -> {
                        return dd;
                    }).toArray();
                    //fft
                    freq[ch] = Utils.getMagnitudes(freq[ch]);
                    //to db
                    freq[ch] = Utils.mag_to_db(freq[ch]);
                    //convert db to ...
                    double max = 1;
                    for (int j = 0; j < freq[ch].length; j++) {
                        freq[ch][j] = Math.pow(10, Utils.apply_weighting(j * fhz_resolution, freq[ch][j]) / 10);
                        max = Math.max(max, freq[ch][j]);
                    }
                    //convert based on normalized
                    for (int j = 0; j < freq[ch].length; j++) {
                        freq[ch][j] = (int) (freq[ch][j] / max * 100) / 2 * 3;
                        pre[ch][j] = freq[ch][j];
                    }
                    //smooth
                    Utils.smooth(freq[ch], pre[ch], 0.6);
                }

                //update showing data(pcm and freqency and current time progress)
                analyze_window.settime((long) (total_len_ms * 1.0 * i / framelength));
                analyze_window.update(sample, freq, fhz_resolution);
                analyze_window.settime(i * speaker.getMicrosecondLength() / framelength);
            } else {
                //eof analyze_stream
                break;
            }
            
            //wait clip and analyze progress sync
            while (speaker.getFramePosition() < i + step) {
                Thread.sleep(50);
            }

        }
        
        //analyze end
        destroy_window();
        analyze_stream.close();
        speaker.stop();
        speaker.close();
        System.gc();
    }

    /**
     *destroy gui
     */
    private void destroy_window() {
        analyze_window.end();
        analyze_window=null;
    }

    /**
     *set adjusting flag for progress bar
     * @param isadjusting
     */
    private void setIsadjusting(boolean isadjusting) {
        this.isadjusting = isadjusting;
    }

    /**
     *set target time position for progress bar
     * @param targettime - 0-100 in %
     */
    private void setTargettime(int targettime) {
        this.target_percent = targettime;
    }
}
