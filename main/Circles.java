package main;

import java.io.FileWriter;
import java.io.IOException;

public class Circles {
	public void drawCirclePath(String filename, double totalDegrees) {
	    String filepath = "C:/Users/frc4587/Desktop/pathGui/"+filename+".txt";
		//double totalDegrees = 900;
		double acceleration = Constants.ACC_MAX;
		double wheelbase = Constants.WHEELBASE;
		double totalDistance;
		double velLast=0.0;
		double velNow;
		double posNow;
		double posLast=0.0;
		double acc;
		double totalTime;
		double radians;
		double lineNum = 0;
		double timeStep = Constants.TIMESTEP;
		double x;
		double y;
		
		totalDistance = wheelbase * Math.PI /360 * totalDegrees / 12; // divide 12 = ft
		totalTime = Math.sqrt(4*totalDistance/acceleration);
		double timeOff = totalTime % timeStep;
		totalTime -= timeOff;
		if(timeOff >= timeStep/2){
			totalTime+=timeStep;
		}
		lineNum = totalTime / timeStep + 1;// +1 = line of 0's
		System.out.println("dist: "+totalDistance+" time: "+totalTime+" timeOff: "+timeOff+" lineNum: "+lineNum);
		FileWriter m_writer;
	    try {
			m_writer = new FileWriter(filepath, false);
			m_writer.write(filename + "\n" + (int)lineNum + "\n");
		} catch ( IOException e ) {
			System.out.println(e);
			m_writer = null;
		}
	    
	    //first side
	    x=-wheelbase/24;//-1.125;
	    y=0.0;
		for(int line = 0;line < lineNum;line++){
			if(line != 0){
				if(lineNum%2==0){
					if (line<lineNum/2){
						acc=acceleration;
					}else if(line>lineNum/2){
						acc=-acceleration;
					}else{
						acc=-acceleration*1.472;
					}
				}else{
					if(line==lineNum/2+0.5){
						acc=-acceleration*1.472;
					}else if (line<lineNum/2){
						acc=acceleration;
					}else{//line>lineNum
						acc=-acceleration;
					}
				}
				/*if (line<(lineNum-1)/2){
					acc=acceleration;
				}else if(line>(lineNum-1)/2){
					acc=-acceleration;
				}else{
					acc=3;
				}*/
				velNow = velLast + acc * timeStep;
				posNow = posLast + (velLast + velNow)/2 * timeStep;
				radians = posNow*24/wheelbase;//360/(wheelbase*Math.PI)/4.77464829275769;//magic num??????
				//x+=Math.sin(radians)*(posNow-posLast);
				//y+=Math.cos(radians)*(posNow-posLast);
				
				//http://rossum.sourceforge.net/papers/CalculationsForRobotics/CirclePath.htm
				double startAngle = -Math.PI/2;//Math.PI/2;
				x=-wheelbase/24 - wheelbase/24*Math.sin(startAngle)+wheelbase/24*Math.sin((radians/(line*0.02-0.0))*(line*0.02-0.0)+startAngle);
				y=0.0 - wheelbase/24*Math.cos(startAngle)+wheelbase/24*Math.cos((radians/(line*0.02-0.0))*(line*0.02-0.0)+startAngle);
	
				velLast = velNow;
				posLast = posNow;
				
			    if(m_writer != null){try{
						m_writer.write(posNow + " "+velNow+" "+(int)acc+" 0 "+radians+" "+timeStep+" "+x+" "+y+"\n");// jerk, x, y = 0
					}catch(Exception e){}}
	
				if(line+1==lineNum){//last one
					System.out.println("left side");
					System.out.println("posNow: "+posNow+" endDegrees: "+radians*180/Math.PI);
					System.out.println("posError: "+(totalDistance-Math.abs(posNow))+" degreesError: "+(totalDegrees-radians*180/Math.PI));
				}
			}else{
				 if(m_writer != null){try{
						m_writer.write("0 0 0 0 0 "+timeStep+" "+x+" "+y+"\n");//first line 0 everything
					}catch(Exception e){}}
			}
		}
		//second side
	    x=wheelbase/24;//1.125;
	    y=0.0;
		posLast =0.0;
		velLast=0.0;
		for(int line = 0;line < lineNum;line++){
			if(line != 0){
				if(lineNum%2==0){
					if (line<lineNum/2){
						acc=-acceleration;
					}else if(line>lineNum/2){
						acc=acceleration;
					}else{
						acc=acceleration*1.472;
					}
				}else{
					if(line==lineNum/2+0.5){
						acc=acceleration*1.472;
					}else if (line<lineNum/2){
						acc=-acceleration;
					}else{//line>lineNum
						acc=acceleration;
					}
				}
				velNow = velLast + acc * timeStep;
				posNow = posLast + (velLast + velNow)/2 * timeStep;
				radians = -posNow*24/wheelbase;//360/(wheelbase*Math.PI)/4.77464829275769;//magic num??????
				//x+=Math.sin(radians)*(posNow-posLast);
				//y+=Math.cos(radians)*(posNow-posLast);
	
				double startAngle = Math.PI/2;//Math.PI/2;
				x=wheelbase/24 - wheelbase/24*Math.sin(startAngle)+wheelbase/24*Math.sin((radians/(line*0.02-0.0))*(line*0.02-0.0)+startAngle);
				y=0.0 + wheelbase/24*Math.cos(startAngle)+wheelbase/24*Math.cos((radians/(line*0.02-0.0))*(line*0.02-0.0)+startAngle);
	
	
				//x=-wheelbase/24 - wheelbase/24*Math.sin(startAngle)+wheelbase/24*Math.sin((radians/(line*0.02-0.0))*(line*0.02-0.0)+startAngle);
				//y=0.0 - wheelbase/24*Math.cos(startAngle)+wheelbase/24*Math.cos((radians/(line*0.02-0.0))*(line*0.02-0.0)+startAngle);
				
				velLast = velNow;
				posLast = posNow;
				
			    if(m_writer != null){try{
						m_writer.write(posNow + " "+velNow+" "+(int)acc+" 0 "+radians+" "+timeStep+" "+x+" "+y+"\n");// jerk, x, y = 0
					}catch(Exception e){}}
	
				if(line+1==lineNum){//last one
					System.out.println("right side");
					System.out.println("posNow: "+posNow+" endDegrees: "+radians*180/Math.PI);
					System.out.println("posError: "+(totalDistance-Math.abs(posNow))+" degreesError: "+(totalDegrees-radians*180/Math.PI));
				}
			}else{
				 if(m_writer != null){try{
						m_writer.write("0 0 0 0 0 "+timeStep+" "+x+" "+y+"\n");//first line 0 everything
					}catch(Exception e){}}
			}
		}
		
		try{m_writer.close();System.out.println("Wrote to "+filepath);}catch(Exception e){}
	}
}
