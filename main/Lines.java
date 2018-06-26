package main;

public class Lines {
	double acc1;
	double acc2;
	double acc3;
	double acc1Lines;
	double acc2Lines;
	double acc3Lines;
	double flatAccLines;
	boolean backwards;
	public void drawStraightPath(double totalDistance,double startVel,double endVel) {
	    Writer w = new Writer();
	    if(totalDistance<0){
	    	totalDistance *= -1;
	    	backwards = true;
	    }else{
	    	backwards = false;
	    }
		double acceleration = Constants.ACC_MAX;
		double velocityMax = Constants.VEL_MAX;
		double wheelbase = Constants.WHEELBASE;
		double velLast=startVel;
		double velNow;
		double posNow;
		double posLast=0.0;
		double acc;
		double timeStep = Constants.TIMESTEP;
		
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
					trapTime=totalTime-(endTriTime*2)-(lastI*timeStep)+(i*timeStep);
					trapTime = findTime(trapTime);
					trapLineNum = trapTime/timeStep;
					trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
					if(trapTime%0.04>=0.019){
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
				trapTime=totalTime-(endTriTime*2)+(finalI*timeStep);
				trapTime = findTime(trapTime);
				trapLineNum = trapTime/timeStep;
				trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
				if(trapTime%0.04>=0.019){
					trapTestAcc = trapDist/(((trapTime-timeStep)/2)*((trapTime+timeStep)/2));
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
				acc1Lines=Math.round(trapTime/2/timeStep*10);
				acc1Lines/=10;
				acc2=-Math.abs(trapTestAcc);
				acc2Lines=Math.round(trapTime/2/timeStep*10);
				acc2Lines/=10;
				
				trapLineNum=Math.round(trapTime/timeStep);
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
				acc1Lines=Math.round(trapSingleTriTime/timeStep*10);
				acc1Lines/=10;
				acc2=-trapSingleTriTestAcc;
				acc2Lines=Math.round(trapSingleTriTime/timeStep*10);
				acc2Lines/=10;
				flatAccLines=Math.round(bigRectTime/timeStep*10);
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
				totalLineNum = Math.round(totalTime/timeStep*10);
				totalLineNum/=10;
				
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
						trapTime=totalTime-(endTriTime*2)-(lastI*timeStep)+(i*timeStep);
						trapTime = findTime(trapTime);
						trapLineNum = trapTime/timeStep;
						trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
						if(trapTime%(timeStep*2)>=0.019){
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
					trapTime=totalTime-(endTriTime*2)+(finalI*timeStep);
					trapTime = findTime(trapTime);
					trapLineNum = trapTime/timeStep;
					trapDist=(totalDistance-endTriDist)-(trapTime*startVel);
					if(trapTime%(timeStep*2)>=0.019){
						trapTestAcc = trapDist/(((trapTime-timeStep)/2)*((trapTime+timeStep)/2));
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
					acc1Lines=Math.round(trapTime/2/timeStep*10);
					acc1Lines/=10;
					acc2=-trapTestAcc;
					acc2Lines=Math.round(trapTime/2/timeStep*10);
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
					acc1Lines=Math.round(trapSingleTriTime/timeStep*10);
					acc1Lines/=10;
					acc2=-trapSingleTriTestAcc;
					acc2Lines=Math.round(trapSingleTriTime/timeStep*10);
					acc2Lines/=10;
					flatAccLines=Math.round(bigRectTime/timeStep*10);
					flatAccLines/=10;
					trapLineNum=acc1Lines+acc2Lines+flatAccLines;
				}
				if(ghostTestVel>velocityMax){
					//trapezoid path
					
					double trapTriTime = (velocityMax-startVel)/acceleration;///timeStep;
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
					trapDoubleTriDist = trapTriTime*(trapTestMaxVel-startVel);
				}
			}else if(endVel>startVel){
				//real startTri, ghostEndTri
				ghostTestVel = startTriTestAcc*(totalLineNum-1)/2*timeStep;
				//real endTri, ghostStartTri
				totalTime = Math.sqrt(4*(totalDistance+startTriDist)/acceleration);
				totalTime = findTime(totalTime);
				totalLineNum = Math.round(totalTime/timeStep*10);
				totalLineNum/=10;
				
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
					acc2Lines=Math.round(trapTime/2/timeStep*10);
					acc2Lines/=10;
					acc3=-trapTestAcc;
					acc3Lines=Math.round(trapTime/2/timeStep*10);
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
					acc2Lines=Math.round(trapSingleTriTime/timeStep*10);
					acc2Lines/=10;
					acc3=-trapSingleTriTestAcc;
					acc3Lines=Math.round(trapSingleTriTime/timeStep*10);
					acc3Lines/=10;
					flatAccLines=Math.round(bigRectTime/timeStep*10);
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
					//change max velocity to get exact distance
					trapDoubleTriDist = trapTriTime*(trapTestMaxVel-endVel);
				}
			}
		}trapTestVel = trapTestAcc*((trapLineNum-1)/2)*timeStep;
		if(Math.abs(hexDist)<=0.01){
			trapLineNum=0;
			//acc2 is always part of trapezoid, 1 and 3 swap depending on if startVel or endVel is greater
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
		double lineNum=endTriLineNum+startTriLineNum+trapLineNum;//zero line
		System.out.println("endTriLineNum: "+endTriLineNum+" startTriLineNum: "+startTriLineNum+" trapLineNum: "+trapLineNum );
		lineNum = Math.round(lineNum);
	    //first side
		velLast=startVel;
		double startRad=w.getStartAng();
		double accLast=0;
		double deltaPos=0;
		double deltaVel=0;
		double deltaAcc=0;
		double deltaX=0;
		double deltaY=0;
		double pos,vel,accel,x,y;
		for(int line = 0;line < lineNum;line++){
				acc = findAcc(line);
				//System.out.println("line: "+line+" lineNum: "+lineNum+" acc: "+acc);
				deltaAcc = acc - accLast;
				deltaVel = acc * timeStep;
				deltaPos = (velLast + velLast + deltaVel)/2 * timeStep;
				
				deltaX=(deltaPos*Math.cos(startRad));
				deltaY=(deltaPos*Math.sin(startRad));
				if(backwards) {
					pos = deltaPos*-1;
					vel=deltaVel*-1;
					accel=acc*-1;
					x=deltaX*-1;
					y=deltaY*-1;
				}else {
					pos=deltaPos;
					vel=deltaVel;
					accel=acc;
					x=deltaX;
					y=deltaY;
				}

				Double[] accVel = {pos,vel,accel,0.0,x,y};
				w.addLeftAcc(accVel);

				if(deltaPos!=0) {posLast+=deltaPos;}
				if(deltaVel!=0) {velLast+=deltaVel;}
				if(deltaAcc!=0) {accLast+=deltaAcc;}
				
			    if(line+1==lineNum){//last one
					//System.out.println("---left side---");
					//System.out.println("posNow: "+(deltaPos+posLast));
					System.out.println("posError: "+(totalDistance-Math.abs(deltaPos+posLast)));
				}
				
		}
		//second side
		posLast =0.0;
		accLast=0.0;
		velLast=startVel;
		for(int line = 0;line < lineNum;line++){
			acc = findAcc(line);
			deltaAcc = acc - accLast;
			deltaVel = acc * timeStep;
			deltaPos = (velLast + velLast + deltaVel)/2 * timeStep;
			
			deltaX=(deltaPos*Math.cos(startRad));
			deltaY=(deltaPos*Math.sin(startRad));
			if(backwards) {
				pos = deltaPos*-1;
				vel=deltaVel*-1;
				accel=acc*-1;
				x=deltaX*-1;
				y=deltaY*-1;
			}else {
				pos=deltaPos;
				vel=deltaVel;
				accel=acc;
				x=deltaX;
				y=deltaY;
			}

			Double[] accVel = {pos,vel,accel,0.0,x,y};
			w.addRightAcc(accVel);

			if(deltaPos!=0) {posLast+=deltaPos;}
			if(deltaVel!=0) {velLast+=deltaVel;}
			if(deltaAcc!=0) {accLast+=deltaAcc;}
			
		    if(line+1==lineNum){//last one
				//System.out.println("---left side---");
				//System.out.println("posNow: "+(deltaPos+posLast));
				//System.out.println("posError: "+(totalDistance-Math.abs(deltaPos+posLast)));
			}
				
		}
		//System.out.println("acc1Lines: "+acc1Lines+" acc2Lines: "+acc2Lines+" acc3Lines: "+acc3Lines+" flatAccLines: "+flatAccLines);
		//System.out.println("-------------------------------------------------------------");
	}
	
	private double findAcc(double line){
		if(line<=acc1Lines-1){
			return acc1;
		}else if(flatAccLines>0){
			if(acc1==-acc2){
				if(line<acc1Lines+flatAccLines){
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
					return 0.0;
				}else{
					return acc3;
				}
			}
		}else{
			if(Math.abs(acc2Lines%1-0.5)<=0.1){//should be 0.5
				if(Math.abs(Math.abs(line-0.5)-acc1Lines+1)<=0.1){
					return 0.0;
				}else if(Math.abs(Math.abs(line-0.5)-(acc1Lines+acc2Lines-1))<=0.1){
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
}
