package main;

public class Main {
	public static void main(String[] args){
	    Circles c = new Circles();
		Lines l = new Lines();
		Arcs a = new Arcs();
		

		a.drawArc(180, -7.5, 0.0, 3.0);
		l.drawStraightPath(-15, 3.0, 3.0);
		a.drawArc(70, -5.0, 3.0, 0.0);
		
		Writer w = new Writer();
		//w.printArrays();
		w.writeFile("testPath");
		//w.writeFile("sideGearDownfieldPath0");
		
		Reader r = new Reader("testPath");
		Example ex = new Example(Plot.data().xy(r.getX(), r.getY()));
		//Example ex1 = new Example(Plot.data().xy(r.getLinex3(), r.getPosVelAcc()));
	}
}
