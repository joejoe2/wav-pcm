/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testaudio;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import testaudio.Utils;

/**
 *
 * @author 70136
 */
public class SwingCanvas extends JPanel {

    JFrame frame;
    int[][] paintarr = new int[1][1];
    double[][] pre;
    JLabel tlabel;
    JLabel nlabel;
    JLabel clabel;
    int second;
    int min;
    boolean isterminated;
    boolean isstop;
    int opt = 0;
    int wid = 1;
    int width = 4;
    int offset = 550;
    private final double freq_resolution;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates. 
        g.setColor(Color.GREEN);//paniter color

        //draw pcm
        for (int ch = 0; ch < paintarr.length; ch++) {
            int d = ch == 1 ? 200 : 450;

            for (int i = 0; i < paintarr[ch].length - 1; i++) {
                int y1 = paintarr[ch][i] / 250, y2 = paintarr[ch][i + 1] / 250;

                y1 = (y1 >= 0) ? d - y1 : d - y1;
                y2 = (y2 >= 0) ? d - y2 : d - y2;

                //reduce draw point by 8
                int draw_xpos = i / 8;
                if (opt == 0) {//discrete wave
                    g.drawLine(draw_xpos * wid, y1, (draw_xpos + 1) * wid, y2);
                } else {//curve way
                    g.drawLine(draw_xpos * wid, y1, (draw_xpos + 1) * wid, y1);
                    g.drawLine((draw_xpos + 1) * wid, y1, (draw_xpos + 1) * wid, y2);
                }
            }
        }
        //draw freq
        int left_bound = 1000;
        int low_bound = 250;
        int max_freq = 2048;
        double[][] freq = new double[paintarr.length][paintarr[0].length];
        for (int ch = 0; ch < paintarr.length; ch++) {
            freq[ch] = Arrays.stream(paintarr[ch]).mapToDouble(dd -> {
                return dd;
            }).toArray();
            //fft
            freq[ch] = Utils.getMagnitudes(freq[ch]);
            //to db
            freq[ch]=Utils.mag_to_db(freq[ch]);
            //convert db to ...
            double max = 1;
            for (int i = 0; i < freq[ch].length; i++) {
                freq[ch][i] = Math.pow(10, Utils.apply_weighting(i * freq_resolution, freq[ch][i]) / 10);
                max = Math.max(max, freq[ch][i]);
            }
            //convert based on normalized
            for (int i = 0; i < freq[ch].length; i++) {
                freq[ch][i] = (int) (freq[ch][i] / max * 100) / 2 * 3;
                pre[ch][i]=freq[ch][i];
            }
            //smooth
            Utils.smooth(freq[ch], pre[ch], 0.6);

            //draw freq label
            for (int i = 0; i < 4; i++) {
                int f = (int) (i * max_freq / 4);
                g.drawString(f + " hz", offset + (int) (max_freq / freq_resolution) / 4 * i * width, low_bound * (ch + 1) + 15);
            }

            //draw botton line
            g.drawLine(offset, low_bound * (ch + 1), offset + (max_freq) * width, low_bound * (ch + 1));
            //draw freq
            for (int i = (int) Math.ceil(20 / freq_resolution); i < max_freq / freq_resolution - 1; i++) {
                int y1 = low_bound * (ch + 1) - (int) freq[ch][i];
                int y2 = low_bound * (ch + 1) - (int) freq[ch][i + 1];
                g.drawLine((i) * width + offset, y1, (i) * width + offset, low_bound * (ch + 1));
                g.drawLine((i) * width + offset, y1, (i + 1) * width + offset, y1);
                g.drawLine((i + 1) * width + offset, y1, (i + 1) * width + offset, y2);
                g.drawLine((i + 1) * width + offset, y1, (i + 1) * width + offset, low_bound * (ch + 1));
            }
            g.drawLine((max_freq - 1) * width + offset, low_bound * (ch + 1) - (int) freq[ch][max_freq - 1], (max_freq) * width + offset, low_bound * (ch + 1) - (int) freq[ch][max_freq - 1]);
            g.drawLine((max_freq) * width + offset, low_bound * (ch + 1) - (int) freq[ch][max_freq - 1], (max_freq) * width + offset, low_bound * (ch + 1));

        }

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //paint call paintcomponent paintborder panitchild actually
    }

    /**
     * update the data to show
     *
     * @param arr - pcm sequence
     */
    public void update(int[][] arr) {
        paintarr = arr;
        repaint();
    }

    public SwingCanvas(int channel, String name, long microsec, double freq_resolution) {
        this.setBackground(Color.BLACK);
        paintarr = new int[channel][4096];
        pre = new double[channel][4096];
        frame = new JFrame();
        this.freq_resolution = freq_resolution;
        microsec /= 1000000;
        min = (int) microsec / 60;
        second = (int) microsec - 60 * min;
        tlabel = new JLabel("00:00 / " + String.format("%02d:%02d", min, second));
        nlabel = new JLabel("now playing: " + name);
        clabel = new JLabel("channels: " + channel);
        frame.setLayout(null);
        frame.getContentPane().add(this);
        frame.getContentPane().setBackground(Color.GRAY);
        this.setLocation(0, 125);
        this.setSize(1400, 600);
        this.setBorder(new BevelBorder(1));
        frame.getContentPane().add(nlabel).setFont(new Font("", 1, 20));
        nlabel.setLocation(0, 0);
        nlabel.setSize(1000, 25);
        nlabel.setForeground(Color.WHITE);
        frame.getContentPane().add(tlabel).setFont(new Font("", 1, 20));
        tlabel.setLocation(0, 25);
        tlabel.setSize(300, 25);
        tlabel.setForeground(Color.WHITE);
        frame.getContentPane().add(clabel).setFont(new Font("", 1, 20));
        clabel.setLocation(350, 25);
        clabel.setSize(200, 25);
        clabel.setForeground(Color.WHITE);
        JButton terminbtn = new JButton("terminate");
        JButton stopbtn = new JButton("stop");
        terminbtn.addActionListener((ActionEvent e) -> {
            isterminated = true;
        });
        stopbtn.addActionListener((ActionEvent e) -> {
            isstop = !isstop;
        });
        frame.add(terminbtn);
        frame.add(stopbtn);
        terminbtn.setLocation(0, 50);
        terminbtn.setSize(100, 50);
        stopbtn.setLocation(350, 50);
        stopbtn.setSize(100, 50);
        JLabel plabel = new JLabel("pcm wave");
        frame.add(plabel).setFont(new Font("", 1, 20));
        plabel.setSize(200, 25);
        plabel.setLocation(this.getWidth() / 5, 100);
        plabel.setForeground(Color.WHITE);
        JLabel flabel = new JLabel("frequency");
        frame.add(flabel).setFont(new Font("", 1, 20));
        flabel.setSize(200, 25);
        flabel.setLocation(this.getWidth() / 4 * 3, 100);
        flabel.setForeground(Color.WHITE);
        frame.setSize(1400, 780);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setAlwaysOnTop(false);
    }

    /**
     * update time label
     *
     * @param microsec - ms
     */
    public void settime(long microsec) {
        microsec /= 1000000;
        int nmin = (int) microsec / 60;
        int nsec = (int) microsec - nmin * 60;
        tlabel.setText(String.format("%02d:%02d / %02d:%02d", nmin, nsec, min, second));
    }

    /**
     * stop playing and go back
     */
    public void end() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
        paintarr = null;
    }

    public boolean issterminated() {
        return isterminated;
    }

    public boolean isstop() {
        return isstop;
    }

    public int callbackpos() {
        return -1;
    }

}
