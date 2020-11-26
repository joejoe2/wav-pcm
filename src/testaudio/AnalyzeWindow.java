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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 *
 * @author 70136
 */
public class AnalyzeWindow extends JPanel {

    //gui component
    JFrame frame;
    private JLabel tlabel;
    private JLabel nlabel;
    private JLabel clabel;
    private JButton terminbtn;
    private JButton stopbtn;
    //total music len in [min:sec]
    private final int second;
    private final int min;
    private final int channel;
    private final String name;

    //drawing data array
    private int[][] pcmtarr = new int[1][1];
    private double[][] freqarr = new double[1][1];

    //flag
    private boolean isterminated;
    private boolean isstop;

    //drawing param
    private final int wid = 1;
    private final int width = 4;
    private final int offset = 550;
    private double freq_resolution;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);//paniter color

        //reduce draw point factor
        int reduce = pcmtarr[0].length * 8 / 4096;
        //draw pcm
        for (int ch = 0; ch < pcmtarr.length; ch++) {
            int d = ch == 1 ? 200 : 450;

            for (int i = 0; i < pcmtarr[ch].length - 1; i++) {
                int y1 = pcmtarr[ch][i] / 250, y2 = pcmtarr[ch][i + 1] / 250;

                y1 = (y1 >= 0) ? d - y1 : d - y1;
                y2 = (y2 >= 0) ? d - y2 : d - y2;

                int draw_xpos = i / reduce;
                g.drawLine(draw_xpos * wid, y1, (draw_xpos + 1) * wid, y2);
            }
        }

        //draw freq
        int left_bound = 1000;
        int low_bound = 250;
        int max_index = 200;//max num of freq points to be drwn
        int max_freq = (int) (max_index * freq_resolution);
        for (int ch = 0; ch < freqarr.length; ch++) {
            //draw freq label
            for (int i = 0; i < 4; i++) {
                int f = (int) (i * max_freq / 4);
                g.drawString(f + " hz", offset + (int) (max_freq / freq_resolution) / 4 * i * width, low_bound * (ch + 1) + 15);
            }
            //draw botton line
            g.drawLine(offset, low_bound * (ch + 1), offset + (max_index) * width, low_bound * (ch + 1));
            //draw freq
            for (int i = (int) Math.ceil(20 / freq_resolution); i < max_index; i++) {
                int y1 = low_bound * (ch + 1) - (int) freqarr[ch][i];
                int y2 = low_bound * (ch + 1) - (int) freqarr[ch][i + 1];
                g.drawLine((i) * width + offset, y1, (i) * width + offset, low_bound * (ch + 1));
                g.drawLine((i) * width + offset, y1, (i + 1) * width + offset, y1);
                g.drawLine((i + 1) * width + offset, y1, (i + 1) * width + offset, y2);
                g.drawLine((i + 1) * width + offset, y1, (i + 1) * width + offset, low_bound * (ch + 1));
            }
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
     * @param pcmarr
     * @param freqarr
     * @param freq_resolution
     */
    void update(int[][] pcmarr, double[][] freqarr, int freq_resolution) {
        this.pcmtarr = pcmarr;
        this.freqarr = freqarr;
        this.freq_resolution = freq_resolution;

        repaint();
    }

    /**
     * setup layout
     */
    private void set_layouts() {
        //drawing area
        this.setBackground(Color.BLACK);
        this.setLocation(0, 125);
        this.setSize(1400, 600);
        this.setBorder(new BevelBorder(1));
        //main window
        frame = new JFrame();
        frame.setLayout(null);
        frame.getContentPane().add(this);
        frame.getContentPane().setBackground(Color.GRAY);
        frame.setSize(1400, 780);
        //label
        tlabel = new JLabel("00:00 / " + String.format("%02d:%02d", min, second));
        nlabel = new JLabel("now playing: " + name);
        clabel = new JLabel("channels: " + channel);
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
        //btn
        terminbtn = new JButton("terminate");
        stopbtn = new JButton("stop");

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
        //other config
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setAlwaysOnTop(false);
    }

    /**
     * bind listeners of gui components
     */
    private void set_listeners() {
        terminbtn.addActionListener((ActionEvent e) -> {
            isterminated = true;
        });
        stopbtn.addActionListener((ActionEvent e) -> {
            isstop = !isstop;
        });
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                isterminated = true;
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public AnalyzeWindow(int channel, String name, long microsec) {
        this.channel = channel;
        this.name = name;

        microsec /= 1000000;
        min = (int) microsec / 60;
        second = (int) microsec - 60 * min;

        set_layouts();
        set_listeners();
    }

    /**
     * update time label
     *
     * @param microsec - ms
     */
    void settime(long microsec) {
        microsec /= 1000000;
        int nmin = (int) microsec / 60;
        int nsec = (int) microsec - nmin * 60;
        tlabel.setText(String.format("%02d:%02d / %02d:%02d", nmin, nsec, min, second));
    }

    /**
     * stop playing and go back
     */
    void end() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
        pcmtarr = null;
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
