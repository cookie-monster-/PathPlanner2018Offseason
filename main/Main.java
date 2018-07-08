package main;

public class Main {
	public static void main(String[] args){
	    Circles c = new Circles();
		Lines l = new Lines();
		Arcs a = new Arcs();
		

		a.drawArc(-90, -3.5, 0.0, 0.0);
		//l.drawStraightPath(-1.0, 2.0, 2.0);
		//a.drawArc(20, -3.5, 2.0, 0.0);
		
		
		Writer w = new Writer();
		//w.printArrays();
		w.writeFile("testArc");
		//w.writeFile("sideGearDownfieldPath0");
		
		Reader r = new Reader("testArc");
		Example ex = new Example(Plot.data().xy(r.getX(), r.getY()));
		//Example ex1 = new Example(Plot.data().xy(r.getLinex3(), r.getPosVelAcc()));
	}
}
