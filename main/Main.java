package main;

public class Main {
	public static void main(String[] args){
	    Circles c = new Circles();
		Lines l = new Lines();
		Arcs a = new Arcs();
		
		/* 
		 * Draw lines format = l.drawStraightPath(distance(ft), startVelocity(ft/s), endVelocity(ft/s));
		 * 
		 * Draw arcs format = a.drawArc(angle(degrees), circleRadius(ft), startVelocity(ft/s), endVelocity(ft/s));
		 * the code will determine the distance needed based on the radius of the circle 
		 * and how many degrees along the circle you want to turn
		 * 
		 * You shouldn't ever need to change the start or end velocities once the path is set up, just tweak the distances,
		 * but if you do, it is possible to create a "bad" path, ex: we can't go from 0 to 10 ft/s in 6 inches,
		 * if the path doesn't work, there will be an error in the console that looks something like "ERROR: Bad Path"
		 * if that happens you need to lower either the start or end velocity, or both 
		 * (technically you could also increase distance, but you probably don't want to because that would mess up the path)
		*/
		
		
		l.drawStraightPath(10, 0.0, Constants.TEST_VEL);
		a.drawArc(40, 5, Constants.TEST_VEL, 0.0);
		/*a.drawArc(-70, -5, 0.0, 0.0);
		a.drawArc(70, 5, 0.0, 0.0);
		a.drawArc(-110, -5, 0.0, 0.0);
		a.drawArc(110, 5, 0.0, 0.0);
		*/
		Writer w = new Writer();
		//w.printArrays();
		w.writeFile("testPath");
		//w.writeFile("sideGearDownfieldPath0");
		
		Reader r = new Reader("testPath");
		//Example ex = new Example(Plot.data().xy(r.getX(), r.getY()));
		Example ex1 = new Example(Plot.data().xy(r.getLinex3(), r.getPosVelAcc()));
	}
}
