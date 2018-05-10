package main;

import java.awt.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {
	private static ArrayList<Double[]> leftAccs = new ArrayList<Double[]>();
	//private double[] leftAccs = new double[1];
	public void addLeftAcc(Double[] acc){
		leftAccs.add(acc);
	}
	
	private static ArrayList<Double[]> rightAccs = new ArrayList<Double[]>();
	public void addRightAcc(Double[] acc){
		rightAccs.add(acc);
	}
	
	private static double startAng = 0;
	public void setStartAng(double radians) {
		startAng = radians;
	}
	public double getStartAng() {
		return startAng;
	}
	
	public void printArrays(){
		System.out.println(leftAccs);
		for(int x =0;x<leftAccs.size();x++){
			Double[] q = leftAccs.get(x);
			System.out.println(q[0]+","+q[1]+","+q[2]);
		}
		for(int x =0;x<rightAccs.size();x++){
			Double[] q = rightAccs.get(x);
			System.out.println(q[0]+","+q[1]+","+q[2]);
		}
	}

	public void writeFile(String filename){
		double lineNum = leftAccs.size();
		String filepath=Constants.FILEPATH+filename+".csv";
		FileWriter m_writer;
		try {
			m_writer = new FileWriter(filepath, false);
			//m_writer.write(filename + "\n" + ((int)lineNum+1) + "\n");
			m_writer.write("pos,vel,acc,jerk,gyro,time,x,y\n");
		} catch ( IOException e ) {
			System.out.println(e);
			m_writer = null;
		}
		double deltaAcc=0;
		double accLast=0;
		double deltaVel=0;
		double velLast=0;
		double deltaPos=0;
		double posLast=0;
		double timeStep = Constants.TIMESTEP;
		double wheelbase = Constants.WHEELBASE;
		double deltaX=0;
		double deltaY=0;
		double xLast=0;
		double yLast=0;
		double deltaRad=0;
		double radLast=0;
		double xNow=0,yNow=0,posNow=0,velNow=0,accNow=0,radNow=0;

	    yLast=-wheelbase/24;//-1.125;
	    xLast=0.0;
		for(int line = -1;line < lineNum;line++){
			if(line >= 0){
				Double[] accVel = leftAccs.get(line);
				deltaPos=accVel[0];
				deltaVel = accVel[1];
				deltaAcc = accVel[2];
				deltaRad = accVel[3];
				deltaX = accVel[4];
				deltaY = accVel[5];

				posNow=posLast+deltaPos;
				velNow=velLast+deltaVel;
				accNow=accLast+deltaAcc;
				radNow=radLast+deltaRad;
				xNow=xLast+deltaX;
				yNow=yLast+deltaY;

				posLast = posNow;
				velLast = velNow;
				accLast = accNow;
				radLast = radNow;
				xLast = xNow;
				yLast = yNow;
					
				if(m_writer != null){try{
						m_writer.write(posNow + ","+velNow+","+accNow+",0,"+radNow+","+timeStep+","+xNow+","+yNow+"\n");// jerk, x, y = 0
					}catch(Exception e){}}
			}else{
				 if(m_writer != null){try{
						m_writer.write("0,"+0+",0,0,0,"+timeStep+","+xNow+","+yNow+"\n");//first line 0 everything
					}catch(Exception e){}}
				
			}
			
		}
		
		//RIGHT SIDE ----------------------------------------------------------
	    posLast = 0;
		velLast = 0;
		accLast = 0;
		radLast = 0;
		xLast = 0;
		yLast = wheelbase/24;
		for(int line = -1;line < lineNum;line++){
			if(line >= 0){
				Double[] accVel = rightAccs.get(line);

				deltaPos=accVel[0];
				deltaVel = accVel[1];
				deltaAcc = accVel[2];
				deltaRad = accVel[3];
				deltaX = accVel[4];
				deltaY = accVel[5];

				posNow=posLast+deltaPos;
				velNow=velLast+deltaVel;
				accNow=accLast+deltaAcc;
				radNow=radLast+deltaRad;
				xNow=xLast+deltaX;
				yNow=yLast+deltaY;

				posLast = posNow;
				velLast = velNow;
				accLast = accNow;
				radLast = radNow;
				xLast = xNow;
				yLast = yNow;
				
			    if(m_writer != null){try{
						m_writer.write(posNow + ","+velNow+","+accNow+",0,"+radNow+","+timeStep+","+xNow+","+yNow+"\n");// jerk, x, y = 0
					}catch(Exception e){}}
			}else{
				 if(m_writer != null){try{
						m_writer.write("0,"+0+",0,0,0,"+timeStep+","+xNow+","+yNow+"\n");//first line 0 everything
					}catch(Exception e){}}
			}
		}

		try{m_writer.close();System.out.println("Wrote to "+filepath);}catch(Exception e){}
	}
}
