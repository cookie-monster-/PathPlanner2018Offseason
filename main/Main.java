package main;

public class Main {
	public static void main(String[] args){
	    Circles c = new Circles();
		Lines l = new Lines();
		Arcs a = new Arcs();
		
    //PYRAMID TO LEFT SCALE
		a.drawArc(-170, -5.25, 0.0, 4.5);
		l.drawStraightPath(-19.5, 4.5, 3.0);
		a.drawArc(-100, -4.25, 3.0, 0.0);
		
/*	RIGHT SIDE TO RIGHT SCALE 90 DEGREES	
 * l.drawStraightPath(-22.5, 0.0, 3.0);
		a.drawArc(90, -4.0, 3.0, 0.0);
	*/	
		Writer w = new Writer();
		//w.printArrays();
		w.writeFile("testArc");
		//w.writeFile("sideGearDownfieldPath0");
		
		Reader r = new Reader("testArc");
		Example ex = new Example(Plot.data().xy(r.getX(), r.getY()));
		//Example ex1 = new Example(Plot.data().xy(r.getLinex3(), r.getPosVelAcc()));
	}
}
