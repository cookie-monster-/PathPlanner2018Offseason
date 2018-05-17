package notmain;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import notmain.Plot.Data;
import notmain.Plot.DataSeriesOptions;
import notmain.Plot.PlotOptions;

public class Example extends JFrame {

    public Example(Data data) {
    	setTitle("Simple example");
       	setSize(1920,1080);
       	setLocationRelativeTo(null);
       	setDefaultCloseOperation(EXIT_ON_CLOSE);
       	PlotOptions opts = Plot.plotOpts();
       	opts.height(950);
       	opts.width(1920);
       	opts.tickSize(10);

       	DataSeriesOptions dopts = new DataSeriesOptions();
       	dopts.lineWidth(6);
    	Plot plot = Plot.plot(opts).series(null, data, dopts);
    	BufferedImage img = plot.getImg();

   		JLabel label = new JLabel();
   		label.setSize(1920, 1080);
   		label.setIcon(new ImageIcon(img));
   		label.setVisible(true);

    	add(label);
	    setVisible(true);
    }

    /*public static void main(String[] args) {
    	Data data = Plot.data().xy(1, 2).xy(3, 4);
    	Example ex = new Example(data);
    }*/
}