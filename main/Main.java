package main;

public class Main {
	public static void main(String[] args){
	    Circles c = new Circles();
		Lines l = new Lines();
		Arcs a = new Arcs();
		

		a.drawArc(180, -7.5, 0.0, 0.0);
		l.drawStraightPath(-15, 0.0, 0.0);
		a.drawArc(90, -5.0, 0.0, 0.0);
		
		Writer w = new Writer();
		//w.printArrays();
		w.writeFile("testPath1");
		//w.writeFile("sideGearDownfieldPath0");
		
		Reader r = new Reader("testPath1");
		Example ex = new Example(Plot.data().xy(r.getX(), r.getY()));
		//Example ex1 = new Example(Plot.data().xy(r.getLinex3(), r.getPosVelAcc()));
	}
}
