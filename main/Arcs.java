package main;

public class Arcs {
	double acc1;
	double acc2;
	double acc3;
	double acc1Lines;
	double acc2Lines;
	double acc3Lines;
	double flatAccLines;
	boolean backwards;
	double lineNum;
	double acc;
	double wheelbase = Constants.WHEELBASE;
	double velLast;
	double deltaVel;
	double deltaPos;
	double deltaAcc;
	double posLast;
//	double accLast;
	double accLast2;
	double radLast;
	double startVel;
	double timeStep = Constants.TIMESTEP;
	double deltaRad;
	double totalDegrees;
	double radius;
	double deltaX,deltaY;
    Writer w = new Writer();
    double totalDistance;
	
	private void drawArcPath1(double totalDegreesX,double radiusX,double startVelX,double endVel) {
		startVel=startVelX;
		totalDegrees=totalDegreesX;
		radius=radiusX;
	    if(radius<0){
	    	radius *= -1;
	    	backwards = true;
	    }else{
	    	backwards = false;
	    }
	    totalDistance=Math.abs((2*Math.PI*radius)*(totalDegrees/360));
		double acceleration = Constants.ACC_MAX;
		double velocityMax = Constants.VEL_MAX;
		
		double trapTestAcc=0;
		double trapTestVel=0;

		double totalTime=0;
		double totalLineNum = 0;
		double trapDist=0;
		double trapTime=0;
		double trapLineNum=0;
		
		double startTriTime=0;
		double endTriTime=0;
		double startTriDist=0;
		double endTriDist=0;
		double startTriTestAcc=0;
		double endTriTestAcc=0;
		double startTriLineNum=0;
		double endTriLineNum=0;
		double hexDist=0;
		
		double trapRectLines=0;
		double trapSingleTriLines=0;

		if(startVel>endVel){
			//find endVel triangle
			endTriTime=startVel/acceleration;
			endTriTime=findTime(endTriTime);
			endTriLineNum=endTriTime/timeStep;
			endTriDist=endTriTime*startVel/2;
			endTriTestAcc = -(2*endTriDist)/(endTriTime*endTriTime);
			trapDist=totalDistance-endTriDist;
			acc3 = endTriTestAcc;
			acc3Lines = endTriLineNum;
		}else if(endVel>startVel){
			//find startVel triangle
			startTriTime=endVel/acceleration;
			startTriTime=findTime(startTriTime);
			startTriLineNum=startTriTime/timeStep;
			startTriDist=startTriTime*endVel/2;
			startTriTestAcc = (2*startTriDist)/(startTriTime*startTriTime);
			trapDist=totalDistance-startTriDist;
			acc1 = startTriTestAcc;
			acc1Lines = startTriLineNum;
		}else if(endVel==startVel&&startVel>0){
			//both = and > 0
			double ghostSingleTriTime=startVel/acceleration;
			double ghostDoubleTriDist=startVel*ghostSingleTriTime;
			double ghostTotalDist = ghostDoubleTriDist+totalDistance;
			trapTime = Math.sqrt(4*ghostTotalDist/acceleration);
			trapTime -= (ghostSingleTriTime*2);
			trapTime = findTime(trapTime);
			trapLineNum = trapTime/timeStep;
			totalTime = trapTime;
			
			boolean goodTrap=false;
			boolean forwards=true;
			boolean backwards=false;
			int i=-1;
			while(goodTrap==false){
				double lastError=9999999999.0;
				int lastI=0;
				while(forwards){
					i++;
					trapTime=totalTime-(i*timeStep);
					trapTime = findTime(trapTime);
					trapLineNum = trapTime/timeStep;
					trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
					if(trapTime%0.04>=0.019){
						trapTestAcc = trapDist/(((trapTime-0.02)/2)*((trapTime+0.02)/2));
						trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
					}else{
						trapTestAcc=(4*trapDist)/(trapTime*trapTime);
						trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
					}
					double nowError = Math.abs(trapTestAcc-acceleration);
					if(nowError>lastError){
						forwards=false;
						backwards=true;
						lastError=9999999999.0;
						lastI=i;
						i=-1;
					}
					lastError=nowError;
				}
				int secondLastI=0;
				while(backwards){
					i++;
					trapTime=totalTime-(endTriTime*2)-(lastI*timeStep)+(i*timeStep);
					trapTime = findTime(trapTime);
					trapLineNum = trapTime/timeStep;
					trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
					if(trapTime%0.04>=0.019){
						trapTestAcc = trapDist/(((trapTime-0.02)/2)*((trapTime+0.02)/2));
						trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
					}else{
						trapTestAcc=(4*trapDist)/(trapTime*trapTime);
						trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
					}
					double nowError = Math.abs(trapTestAcc-acceleration);
					if(nowError>lastError){
						forwards=false;
						backwards=false;
						secondLastI=i;
					}
					lastError=nowError;
				}
				int finalI = secondLastI-lastI-1;
				trapTime=totalTime-(endTriTime*2)+(finalI*timeStep);
				trapTime = findTime(trapTime);
				trapLineNum = trapTime/timeStep;
				trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
				if(trapTime%0.04>=0.019){
					trapTestAcc = trapDist/(((trapTime-0.02)/2)*((trapTime+0.02)/2));
					trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
				}else{
					trapTestAcc=(4*trapDist)/(trapTime*trapTime);
					trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
				}
				goodTrap=true;
			}
			trapTestVel+=startVel;
			
			if(trapTestVel<=velocityMax){
				acc1=Math.abs(trapTestAcc);
				acc1Lines=Math.round(trapTime/2/0.02*10);
				acc1Lines/=10;
				acc2=-Math.abs(trapTestAcc);
				acc2Lines=Math.round(trapTime/2/0.02*10);
				acc2Lines/=10;
				
				trapLineNum=Math.round(trapTime/0.02);
			}else{
				double trapSingleTriTime=(velocityMax-startVel)/acceleration;
				trapSingleTriTime=findTime(trapSingleTriTime);
				double trapDoubleTriDist=(velocityMax-startVel)*trapSingleTriTime;
				double trapSingleTriTestAcc=(velocityMax-startVel)/trapSingleTriTime;
				
				double trapDoubleTriRectDist=(trapSingleTriTime*2)*startVel;
				double bigRectDist=totalDistance-trapDoubleTriRectDist-trapDoubleTriDist;
				double bigRectTime=bigRectDist/velocityMax;
				bigRectTime=findTime(bigRectTime);
				double newMaxVel=startVel+((bigRectDist-(bigRectTime*startVel))+trapDoubleTriDist)/(bigRectTime+trapSingleTriTime);
				trapDoubleTriDist=(newMaxVel-startVel)*trapSingleTriTime;
				trapSingleTriTestAcc=(newMaxVel-startVel)/trapSingleTriTime;
				bigRectDist=bigRectTime*newMaxVel;
				
				acc1=trapSingleTriTestAcc;
				acc1Lines=Math.round(trapSingleTriTime/0.02*10);
				acc1Lines/=10;
				acc2=-trapSingleTriTestAcc;
				acc2Lines=Math.round(trapSingleTriTime/0.02*10);
				acc2Lines/=10;
				flatAccLines=Math.round(bigRectTime/0.02*10);
				flatAccLines/=10;
				trapLineNum=acc1Lines+acc2Lines+flatAccLines;
			}
			hexDist=totalDistance;
			System.out.println("acc1: "+acc1+" acc2: "+acc2+" acc1Lines: "+acc1Lines+" acc2Lines: "+acc2Lines);
		}else{
			//both 0
			trapDist=totalDistance;
			hexDist=trapDist;
			trapTime = Math.sqrt(4*trapDist/acceleration);
			trapTime = findTime(trapTime);
			trapLineNum = trapTime/timeStep;
			trapTestAcc = (4*trapDist)/(trapTime*trapTime);
			if(trapTime%(timeStep*2)>=timeStep*0.9){
				trapTestVel = trapTestAcc*((trapTime-timeStep)/2);
			}else{
				trapTestVel = trapTestAcc*(trapTime/2);
			}
			acc1=trapTestAcc;
			acc2=-trapTestAcc;
			acc1Lines=Math.round(trapLineNum/2*10);
			acc1Lines/=10;
			acc2Lines=Math.round(trapLineNum/2*10);
			acc2Lines/=10;
			if(trapTestVel>velocityMax){
				//trapezoid, no triangle
				double trapSingleTriTime = velocityMax / acceleration;
				trapSingleTriTime = findTime(trapSingleTriTime);
				trapSingleTriLines = trapSingleTriTime/timeStep;
				double trapTriTestAcc = velocityMax / trapSingleTriTime;
				double trapDoubleTriDist = velocityMax * trapSingleTriTime;
				
				double trapRectDist = trapDist-trapDoubleTriDist;
				double trapRectTime = trapRectDist / velocityMax;
				trapRectTime = findTime(trapRectTime);
				trapRectLines = trapRectTime/timeStep;
				double newMaxVel = trapDist/(trapRectTime+trapSingleTriTime);
				trapTriTestAcc = newMaxVel / trapSingleTriTime;
				trapDoubleTriDist = newMaxVel * trapSingleTriTime;
				trapRectDist = trapDist-trapDoubleTriDist;
				
				acc1=trapTriTestAcc;
				acc2=-trapTriTestAcc;
				acc1Lines=Math.round(trapSingleTriLines*10);
				acc1Lines/=10;
				acc2Lines=Math.round(trapSingleTriLines*10);
				acc2Lines/=10;
				flatAccLines=trapRectLines;
				trapLineNum=trapRectLines+(trapSingleTriLines*2);
			}
		}
		double ghostTestVel;
		if(trapDist<totalDistance){
			if(startVel>endVel){
				//real endTri, ghostStartTri
				totalTime = Math.sqrt(4*(totalDistance+endTriDist)/acceleration);
				totalTime = findTime(totalTime);
				totalLineNum = findLineNum(totalTime);
				
				boolean goodTrap=false;
				boolean forwards=true;
				boolean backwards=false;
				int i=-1;
				while(goodTrap==false){
					double lastError=9999999999.0;
					int lastI=0;
					while(forwards){
						i++;
						trapTime=totalTime-(endTriTime*2)-(i*timeStep);
						trapTime = findTime(trapTime);
						trapLineNum = trapTime/timeStep;
						trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
						if(trapTime%0.04>=0.019){
							trapTestAcc = trapDist/(((trapTime-0.02)/2)*((trapTime+0.02)/2));
							trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
						}else{
							trapTestAcc=(4*trapDist)/(trapTime*trapTime);
							trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
						}
						double nowError = Math.abs(trapTestAcc-acceleration);
						if(nowError>lastError){
							forwards=false;
							backwards=true;
							lastError=9999999999.0;
							lastI=i;
							i=-1;
						}
						lastError=nowError;
					}
					int secondLastI=0;
					while(backwards){
						i++;
						trapTime=totalTime-(endTriTime*2)-(lastI*timeStep)+(i*timeStep);
						trapTime = findTime(trapTime);
						trapLineNum = trapTime/timeStep;
						trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
						if(trapTime%0.04>=0.019){
							trapTestAcc = trapDist/(((trapTime-0.02)/2)*((trapTime+0.02)/2));
							trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
						}else{
							trapTestAcc=(4*trapDist)/(trapTime*trapTime);
							trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
						}
						double nowError = Math.abs(trapTestAcc-acceleration);
						if(nowError>lastError){
							forwards=false;
							backwards=false;
							secondLastI=i;
						}
						lastError=nowError;
					}
					int finalI = secondLastI-lastI-1;
					trapTime=totalTime-(endTriTime*2)+(finalI*timeStep);
					trapTime = findTime(trapTime);
					trapLineNum = trapTime/timeStep;
					trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
					if(trapTime%0.04>=0.019){
						trapTestAcc = trapDist/(((trapTime-0.02)/2)*((trapTime+0.02)/2));
						trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
					}else{
						trapTestAcc=(4*trapDist)/(trapTime*trapTime);
						trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
					}
					goodTrap=true;
				}
				hexDist=trapDist+(trapTime*startVel);
				ghostTestVel=trapTestVel+startVel;
				if(trapTestVel+startVel<=velocityMax){
					acc1=trapTestAcc;
					acc1Lines=Math.round(trapTime/2/0.02*10);
					acc1Lines/=10;
					acc2=-trapTestAcc;
					acc2Lines=Math.round(trapTime/2/0.02*10);
					acc2Lines/=10;
					trapLineNum=acc1Lines+acc2Lines;
				}else{
					//trapezoid path
					double trapSingleTriTime=(velocityMax-startVel)/acceleration;
					trapSingleTriTime=findTime(trapSingleTriTime);
					double trapDoubleTriDist=(velocityMax-startVel)*trapSingleTriTime;
					double trapSingleTriTestAcc=(velocityMax-startVel)/trapSingleTriTime;
					
					double trapDoubleTriRectDist=(trapSingleTriTime*2)*startVel;
					double bigRectDist=hexDist-trapDoubleTriRectDist-trapDoubleTriDist;
					double bigRectTime=bigRectDist/velocityMax;
					bigRectTime=findTime(bigRectTime);
					double newMaxVel=startVel+((bigRectDist-(bigRectTime*startVel))+trapDoubleTriDist)/(bigRectTime+trapSingleTriTime);
					trapDoubleTriDist=(newMaxVel-startVel)*trapSingleTriTime;
					trapSingleTriTestAcc=(newMaxVel-startVel)/trapSingleTriTime;
					bigRectDist=bigRectTime*newMaxVel;
					
					acc1=trapSingleTriTestAcc;
					acc1Lines=Math.round(trapSingleTriTime/0.02*10);
					acc1Lines/=10;
					acc2=-trapSingleTriTestAcc;
					acc2Lines=Math.round(trapSingleTriTime/0.02*10);
					acc2Lines/=10;
					flatAccLines=Math.round(bigRectTime/0.02*10);
					flatAccLines/=10;
					trapLineNum=acc1Lines+acc2Lines+flatAccLines;
				}
				if(ghostTestVel>velocityMax){
					//trapezoid path
					double trapTriTime = (velocityMax-startVel)/acceleration;
					double trapTriSteps = trapTriTime/timeStep;
					double trapTriTimeOff = trapTriSteps%1;
					trapTriSteps -= trapTriTimeOff;
					if(trapTriTimeOff>=0.5){
						trapTriSteps+=1;
					}
					trapTriTime = trapTriSteps*timeStep;
					double trapDoubleTriDist = trapTriTime*(velocityMax-startVel);
					
					double trapDistLeftover = hexDist - (trapDoubleTriDist+(trapTriTime*startVel));
					double trapTimeAtMaxVel = trapDistLeftover/velocityMax;
					double trapTotalTime = (trapTriTime)+trapTimeAtMaxVel;
					double trapLineNum2 = trapTotalTime / timeStep;// + 1;// +1 = line of 0's
					if (trapLineNum2%1>0){
						if(trapLineNum2%1>=0.5){
							trapLineNum2+=1;
						}
						trapLineNum2-=trapLineNum2%1;
					}
					trapTimeAtMaxVel = (trapLineNum2-(trapTriSteps))*timeStep;
					trapTotalTime = (trapTriTime)+trapTimeAtMaxVel;
					double rectDist = trapTotalTime*startVel;
					double TRAPDIST = hexDist-rectDist;
					double trapTestMaxVel=TRAPDIST/(trapTotalTime-(trapTriTime/2));
					//change max velocity to get exact distance
					trapDoubleTriDist = trapTriTime*(trapTestMaxVel-startVel);
					}
			}else if(endVel>startVel){
				//real startTri, ghostEndTri
				ghostTestVel = startTriTestAcc*(totalLineNum-1)/2*timeStep;
				//real endTri, ghostStartTri
				totalTime = Math.sqrt(4*(totalDistance+startTriDist)/acceleration);
				totalTime = findTime(totalTime);
				totalLineNum = findLineNum(totalTime);
				
				boolean goodTrap=false;
				boolean forwards=true;
				boolean backwards=false;
				int i=-1;
				while(goodTrap==false){
					double lastError=9999999999.0;
					int lastI=0;
					while(forwards){
						i++;
						trapTime=totalTime-(startTriTime*2)-(i*timeStep);
						trapTime = findTime(trapTime);
						trapLineNum = trapTime/timeStep;
						trapDist=(totalDistance-startTriDist)-(trapTime*endVel);
						if(trapTime%(timeStep*2)>=(timeStep*0.99)){
							trapTestAcc = trapDist/(((trapTime-timeStep)/2)*((trapTime+timeStep)/2));
							trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
						}else{
							trapTestAcc=(4*trapDist)/(trapTime*trapTime);
							trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
						}
						double nowError = Math.abs(trapTestAcc-acceleration);
						if(nowError>lastError){
							forwards=false;
							backwards=true;
							lastError=9999999999.0;
							lastI=i;
							i=-1;
						}
						lastError=nowError;
					}
					int secondLastI=0;
					while(backwards){
						i++;
						trapTime=totalTime-(startTriTime*2)-(lastI*timeStep)+(i*timeStep);
						trapTime = findTime(trapTime);
						trapLineNum = trapTime/timeStep;
						trapDist=(totalDistance-startTriDist)-(trapTime*endVel);
						if(trapTime%(timeStep*2)>=timeStep*0.99){
							trapTestAcc = trapDist/(((trapTime-timeStep)/2)*((trapTime+timeStep)/2));
							trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
						}else{
							trapTestAcc=(4*trapDist)/(trapTime*trapTime);
							trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
						}
						double nowError = Math.abs(trapTestAcc-acceleration);
						if(nowError>lastError){
							forwards=false;
							backwards=false;
							secondLastI=i;
						}
						lastError=nowError;
					}
					int finalI = secondLastI-lastI-1;
					trapTime=totalTime-(startTriTime*2)+(finalI*timeStep);
					trapTime = findTime(trapTime);
					trapLineNum = trapTime/timeStep;
					trapDist=(totalDistance-startTriDist)-(trapTime*endVel);
					if(trapTime%(timeStep*2)>=timeStep*0.99){
						trapTestAcc = trapDist/(((trapTime-timeStep)/2)*((trapTime+timeStep)/2));
						trapTestVel=trapTestAcc*(trapLineNum-1)/2*timeStep;
					}else{
						trapTestAcc=(4*trapDist)/(trapTime*trapTime);
						trapTestVel=trapTestAcc*(trapLineNum)/2*timeStep;
					}
					goodTrap=true;
				}
				hexDist=trapDist+(trapTime*endVel);
				ghostTestVel=trapTestVel+endVel;
				if(trapTestVel+endVel<=velocityMax){
					acc2=trapTestAcc;
					acc2Lines=Math.round(trapTime/2/0.02*10);
					acc2Lines/=10;
					acc3=-trapTestAcc;
					acc3Lines=Math.round(trapTime/2/0.02*10);
					acc3Lines/=10;
					trapLineNum=acc2Lines+acc3Lines;
				}else{
					//trapezoid path
					double trapSingleTriTime=(velocityMax-endVel)/acceleration;
					trapSingleTriTime=findTime(trapSingleTriTime);
					double trapDoubleTriDist=(velocityMax-endVel)*trapSingleTriTime;
					double trapSingleTriTestAcc=(velocityMax-endVel)/trapSingleTriTime;
					
					double trapDoubleTriRectDist=(trapSingleTriTime*2)*endVel;
					double bigRectDist=hexDist-trapDoubleTriRectDist-trapDoubleTriDist;
					double bigRectTime=bigRectDist/velocityMax;
					bigRectTime=findTime(bigRectTime);
					double newMaxVel=endVel+((bigRectDist-(bigRectTime*endVel))+trapDoubleTriDist)/(bigRectTime+trapSingleTriTime);
					trapDoubleTriDist=(newMaxVel-endVel)*trapSingleTriTime;
					trapSingleTriTestAcc=(newMaxVel-endVel)/trapSingleTriTime;
					bigRectDist=bigRectTime*newMaxVel;
					
					acc2=trapSingleTriTestAcc;
					acc2Lines=Math.round(trapSingleTriTime/0.02*10);
					acc2Lines/=10;
					acc3=-trapSingleTriTestAcc;
					acc3Lines=Math.round(trapSingleTriTime/0.02*10);
					acc3Lines/=10;
					flatAccLines=Math.round(bigRectTime/0.02*10);
					flatAccLines/=10;
					trapLineNum=acc2Lines+acc3Lines+flatAccLines;
				}
				
				if(ghostTestVel>velocityMax){
					//trapezoid path
					double trapTriTime = (velocityMax-endVel)/acceleration;///timeStep;
					double trapTriSteps = trapTriTime/timeStep;
					double trapTriTimeOff = trapTriSteps%1;
					trapTriSteps -= trapTriTimeOff;
					if(trapTriTimeOff>=0.5){
						trapTriSteps+=1;
					}
					trapTriTime = trapTriSteps*timeStep;
					double trapDoubleTriDist = trapTriTime*(velocityMax-endVel);
					
					double trapDistLeftover = hexDist - (trapDoubleTriDist+(trapTriTime*endVel));
					double trapTimeAtMaxVel = trapDistLeftover/velocityMax;
					double trapTotalTime = (trapTriTime)+trapTimeAtMaxVel;
					double trapLineNum2 = trapTotalTime / timeStep;// + 1;// +1 = line of 0's
					if (trapLineNum2%1>0){
						if(trapLineNum2%1>=0.5){
							trapLineNum2+=1;
						}
						trapLineNum2-=trapLineNum2%1;
					}
					trapTimeAtMaxVel = (trapLineNum2-(trapTriSteps))*timeStep;
					trapTotalTime = (trapTriTime)+trapTimeAtMaxVel;
					double rectDist = trapTotalTime*endVel;
					double TRAPDIST = hexDist-rectDist;
					double trapTestMaxVel=TRAPDIST/(trapTotalTime-(trapTriTime/2));
					trapDoubleTriDist = trapTriTime*(trapTestMaxVel-endVel);
				}else{
					//triangle path
					
				}
			}
		}
		
		trapTestVel = trapTestAcc*((trapLineNum-1)/2)*timeStep;
		if(Math.abs(hexDist)<=0.01){
			System.out.println("ERROR: HexDist is: "+hexDist);
			trapLineNum=0;
			acc2Lines=0;
			if(acc1==trapTestAcc){
				acc1Lines=0;
			}else{
				acc3Lines=0;
			}
		}
		if(hexDist<0){
			System.out.println("ERROR: Bad Path - hexDist: "+hexDist+"ft");
			return;
		}
		lineNum=endTriLineNum+startTriLineNum+trapLineNum;//zero line
		lineNum = Math.round(lineNum);
	}
	
	private void drawArcPath2() {	
		double radiansList[]=new double[(int)lineNum];
	    //first side
		accLast2=0;
		radLast=w.getStartAng();
		velLast=startVel;
		double startPos = posLast;
		for(int line = 0;line < lineNum;line++){
				acc = findAcc(line);
				deltaAcc = acc - accLast2;
				deltaVel = acc * timeStep;
				//if(line == 0 && startVel > 0) {deltaVel+=(startVel * Constants.MAGIC_VEL_CONST);}
				deltaPos = (velLast + velLast + deltaVel)/2 * timeStep;
				if(totalDegrees<0){
					deltaRad = -deltaPos/radius;
				}else{
					deltaRad = deltaPos/radius;
				}
				
				deltaX=(deltaPos*Math.cos(radLast+deltaRad));
				deltaY=(deltaPos*Math.sin(radLast+deltaRad));

				if(deltaPos!=0) {posLast+=deltaPos;}
				if(deltaVel!=0) {velLast+=deltaVel;}
				if(deltaRad!=0) {radLast+=deltaRad;}
				
				radiansList[line]=deltaRad;
				if(backwards) {
					deltaVel *= -1;
					acc *= -1;
					deltaPos *= -1;
					deltaX *= -1;
					deltaY *= -1;
				}
				Double[] accVel = {deltaPos,deltaVel,(acc),deltaRad,deltaX,deltaY};
				accLast2=acc;

				if(totalDegrees<0){
					if(backwards) {
						w.addLeftAcc(accVel);
					}else {
						w.addRightAcc(accVel);
					}
				}else{
					if(backwards) {
						w.addRightAcc(accVel);
					}else {
						w.addLeftAcc(accVel);
					}
				}
				
				
				if(line+1==lineNum){//last one
					System.out.println("---left side---");
					System.out.println("posNow: "+(posLast+deltaPos));
					System.out.println("posError: "+((startPos+totalDistance)-Math.abs((posLast+deltaPos))));
					String radList="";
					for(int i=0;i<=lineNum-1;i++){
						radList=radList+radiansList[i]+" ";
					}
				}
		}
		//second side
		posLast =0.0;
		velLast=startVel;
		accLast2=0;
		radius-=(wheelbase/12);
		double startAng = w.getStartAng();
		radLast=startAng;
		for(int line = 0;line < lineNum;line++){
			//if(line != -1){
				deltaRad = radiansList[line];
				if(totalDegrees<0){
					deltaPos = (-radius*(deltaRad+radLast-startAng)) - posLast;
				}else{
					deltaPos = (radius*(deltaRad+radLast-startAng)) - posLast;
				}
				deltaVel = ((deltaPos)/timeStep) - velLast;//*2-velLast;
				acc = ((deltaVel)/timeStep);
				if(acc<-Constants.ACC_MAX) {
					//acc=Constants.ACC_MAX * -2;
					//deltaVel = (acc * timeStep);
				}else if(acc>Constants.ACC_MAX) {
					//acc=Constants.ACC_MAX * 2;
					//deltaVel = (acc * timeStep);
				}

				//System.out.println("acc: "+acc+" vel: "+velNow+" pos: "+posNow+" rad: "+radians);
				
				deltaX=(deltaPos*Math.cos((deltaRad+radLast)));
				deltaY=(deltaPos*Math.sin((deltaRad+radLast)));

				if(deltaPos!=0) {posLast+=deltaPos;}
				if(deltaVel!=0) {velLast+=deltaVel;}
				if(deltaRad!=0) {radLast+=deltaRad;}
				
				if(backwards) {
					deltaVel *= -1;
					acc *= -1;
					deltaPos *= -1;
					deltaX *= -1;
					deltaY *= -1;
				}
				Double[] accVel = {deltaPos,deltaVel,(acc),deltaRad,deltaX,deltaY};
				accLast2=acc;
				
				if(totalDegrees<0){
					if(backwards) {
						w.addRightAcc(accVel);
					}else {
						w.addLeftAcc(accVel);
					}
				}else{
					if(backwards) {
						w.addLeftAcc(accVel);
					}else {
						w.addRightAcc(accVel);
					}
				}
				
				if(line+1==lineNum){//last one
					//System.out.println("---right side---");
					//System.out.println("posNow: "+(posLast+posNow));
					System.out.println("posError2: "+((startPos+totalDistance)-Math.abs((posLast+deltaPos))));
				}
		}
		w.setStartAng(radLast);
		//System.out.println("acc1Lines: "+acc1Lines+" acc2Lines: "+acc2Lines+" acc3Lines: "+acc3Lines+" flatAccLines: "+flatAccLines);
		//System.out.println("-------------------------------------------------------------");
	}
	
	public void drawArc(double totalDegrees, double radius, double startVel, double endVel) {
		//accLast=0;
		flatAccLines = 0.0;//DON'T DELETE THIS LINE
		drawArcPath1(totalDegrees,radius,startVel,endVel);
		drawArcPath2();
	}
	
	private double findAcc(double line){
		if(line<=acc1Lines-1){
			return acc1;
		}else if(flatAccLines>0){
			if(acc1==-acc2){
				if(line<acc1Lines+flatAccLines){
					System.out.println("ERROR 1; acc1Lines: "+acc1Lines+" ; flatAccLines: "+flatAccLines);
					return 0.0;
				}else {
					if(line<acc1Lines+flatAccLines+acc2Lines){
						return acc2;
					}else{
						return acc3;
					}
				}
			}else{
				if(line<acc1Lines+acc2Lines){
					return acc2;
				}else if(line<acc1Lines+acc2Lines+flatAccLines){
					System.out.println("ERROR 2; acc1Lines: "+acc1Lines+" ; flatAccLines: "+flatAccLines);
					return 0.0;
				}else{
					return acc3;
				}
			}
		}else{
			if(Math.abs(acc2Lines%1-0.5)<=0.1){//should be 0.5
				if(Math.abs(Math.abs(line-0.5)-acc1Lines+1)<=0.1){
					System.out.println("ERROR 3; acc1Lines: "+acc1Lines+" ; flatAccLines: "+flatAccLines);
					return 0.0;
				}else if(Math.abs(Math.abs(line-0.5)-(acc1Lines+acc2Lines-1))<=0.1){
					System.out.println("ERROR 4; acc1Lines: "+acc1Lines+" ; flatAccLines: "+flatAccLines);
					return 0.0;
				}else if(line<=acc1Lines+acc2Lines-1){
					return acc2;
				}else{
					return acc3;
				}
			}else{
				if(line<acc1Lines+acc2Lines){
					return acc2;
				}else{
					return acc3;
				}
			}
		}
	}

	
	private double findTime(double time){
		double timeOff;
		double timeStep = Constants.TIMESTEP;
		timeOff = time % timeStep;
		time -= timeOff;
		if(timeOff >= timeStep/2){
			time+=timeStep;
		}
		time*=100;
		time=Math.round(time);
		time/=100;
		return time;
	}
	private double findLineNum(double time){	
		double timeStep = Constants.TIMESTEP;
		double lineNum = time / timeStep + 1;// +1 = line of 0's
		return lineNum;
	}
	
}