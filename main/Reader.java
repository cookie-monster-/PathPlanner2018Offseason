package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import main.Plot.Data;

public class Reader {
	//6 = x, 7 = y
	List<Double> xLis,yLis,posLis,velLis,accLis,radLis;
	public Reader(String filename) {
		posLis = new ArrayList<Double>();
		velLis = new ArrayList<Double>();
		accLis = new ArrayList<Double>();
		radLis = new ArrayList<Double>();
		xLis = new ArrayList<Double>();
		yLis = new ArrayList<Double>();
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(Constants.FILEPATH+filename+".csv"));
			String line = r.readLine();
			line= r.readLine();
			String[] list = line.split(",");
			
			while(line != null) {
				list=line.split(",");
				posLis.add(Double.parseDouble(list[0]));
				velLis.add(Double.parseDouble(list[1]));
				accLis.add(Double.parseDouble(list[2]));
				radLis.add(Double.parseDouble(list[4]));
				xLis.add(Double.parseDouble(list[6]));
				yLis.add(Double.parseDouble(list[7]));
				//System.out.println(x+y);
				line = r.readLine();
			}
			
			System.out.println("DONE READING!");

		}catch(Exception e) {}
	}
	
	public double[] getPos() {
		double[] xArr = new double[posLis.size()];
		for (int i =0;i<posLis.size();i++) {
			xArr[i] = posLis.get(i);
		}
		return xArr;
	}
	public double[] getVel() {
		double[] yArr = new double[velLis.size()];
		for (int i =0;i<velLis.size();i++) {
			yArr[i] = velLis.get(i);
		}
		return yArr;
	}
	public double[] getAcc() {
		double[] xArr = new double[accLis.size()];
		for (int i =0;i<accLis.size();i++) {
			xArr[i] = accLis.get(i);
		}
		return xArr;
	}
	public double[] getRad() {
		double[] yArr = new double[radLis.size()];
		for (int i =0;i<radLis.size();i++) {
			yArr[i] = radLis.get(i);
		}
		return yArr;
	}
	public double[] getX() {
		double[] xArr = new double[xLis.size()];
		for (int i =0;i<xLis.size();i++) {
			xArr[i] = xLis.get(i);
		}
		return xArr;
	}
	public double[] getY() {
		double[] yArr = new double[yLis.size()];
		for (int i =0;i<yLis.size();i++) {
			yArr[i] = yLis.get(i);
		}
		return yArr;
	}
	public double[] getLine() {
		double[] lineArr = new double[posLis.size()*3];
		for (int i =0;i<posLis.size();i++) {
			lineArr[i] = i;
		}for (int i =0;i<posLis.size();i++) {
			lineArr[i+posLis.size()] = i;
		}for (int i =0;i<posLis.size();i++) {
			lineArr[i+posLis.size()*2] = i;
		}
		return lineArr;
	}
	public double[] getPosVelAcc() {
		double[] pvaArr = new double[posLis.size()*3];
		for (int i =0;i<posLis.size();i++) {
			pvaArr[i] = posLis.get(i);
		}
		for (int i =0;i<posLis.size();i++) {
			pvaArr[i+posLis.size()] = velLis.get(i);
		}
		for (int i =0;i<posLis.size();i++) {
			pvaArr[i+posLis.size()*2] = accLis.get(i);
		}
		return pvaArr;
	}
}
