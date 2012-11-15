package ca.ubc.cpsc310.parkme.client;

import com.google.maps.gwt.client.LatLng;

public class Vector {
	final double x;
	final double y;

	//PUBLIC INTERFACE FUNCTIONS	
	public static double distanceToLine(double xPoint, double yPoint,double xLineStart, double yLineStart,double xLineEnd, double yLineEnd){
		Vector point = new Vector(xPoint,yPoint);
		Vector lineStart = new Vector(xLineStart,yLineStart);
		Vector lineEnd = new Vector(xLineEnd,yLineEnd);
		return distanceToLine(point,lineStart,lineEnd);
	}

	public static double distanceToLine(double[] point, double[] lineStart, double[] lineEnd) {
		return distanceToLine(new Vector(point), new Vector(lineStart),	new Vector(lineEnd));
	}

	public static double distanceToLine(Vector point, Vector lineStart, Vector lineEnd) {
		Vector vectToStart = Vector.subtraction(lineStart,point);
		Vector vectToEnd = Vector.subtraction(lineEnd,point);
		Vector vectLine = Vector.subtraction(lineEnd,lineStart);
		if(Vector.dotProduct(vectToStart,vectLine)*Vector.dotProduct(vectToEnd,vectLine)>0){
			//In this case one of the two end points is closest
			if(Vector.dotProduct(vectToStart,vectLine)>0)
				return vectToStart.norm();
			else
				return vectToEnd.norm();				
		}
		else{
			//In this case the nearest point is in the middle of the line
			Vector perpendicular = Vector.subtraction(vectToStart,vectToStart.projectionOnto(vectLine));
			return perpendicular.norm();
		}
	}
	
	//CONSTRUCTORS
	public Vector(double[] coords){
		assert(coords.length==2);
		x=coords[0];
		y=coords[1];
	}
	
	public Vector(double vectorX, double vectorY){
		x=vectorX;
		y=vectorY;
	}
	
	public Vector(Vector vect){
		x=vect.x;
		y=vect.y;
	}
	
	//FUNCTIONS
	public static Vector addition(Vector v1, Vector v2){
		return new Vector(v1.x+v2.x, v1.y+v2.y);
	}
	
	public static Vector subtraction(Vector v1, Vector v2){
		return addition(v1,multiplication(v2,-1));
	}
	
	public static Vector multiplication(Vector v,double d){
		return new Vector(v.x*d,v.y*d);
	}
	
	public static Vector division(Vector v,double d){
		assert(d!=0);
		return multiplication(v,1/d);		
	}
	
	public static Vector normalized(Vector v){
		return division(v,v.norm());
	}
	
	public double norm(){
		return Math.sqrt(normSquared());
	}
	
	public double normSquared(){
		return x*x+y*y;
	}
	
	public Vector projectionOnto(Vector v2){
		v2=normalized(v2);
		return new Vector(multiplication(v2,dotProduct(this,v2)));
	}
	
	public static double dotProduct(Vector v1,Vector v2){
		return v1.x*v2.x+v1.y*v2.y;
	}
	
	public static boolean equal(Vector v1,Vector v2){
		return (v1.x-v2.x<0.000001)&&(v1.y-v2.y<0.000001);
	}

	//TESTING
	public static void main(String[] args) {
		// TESTS
		test(0,0,3,0,0,4,12.0/5);
		System.out.println("test 1 passed in Vector.java");
		test(2,4,4,7,-3,10,Math.sqrt(13));
		System.out.println("test 2 passed in Vector.java");
		test(-8,-5,2,1,-6,3,Math.sqrt(68));		
		System.out.println("test 3 passed in Vector.java");
	}
	
	private static void test(double x1,double y1,double x2,double y2,double x3,double y3,double ans){
		//All reflections should give the same answer
		testSingle(x1,y1,x2,y2,x3,y3,ans);
		testSingle(-x1,y1,-x2,y2,-x3,y3,ans);
		testSingle(x1,-y1,x2,-y2,x3,-y3,ans);
		testSingle(-x1,-y1,-x2,-y2,-x3,-y3,ans);
	}
	
	private static void testSingle(double x1,double y1,double x2,double y2,double x3,double y3,double ans){
		//All three functions should give the same answer
		Vector v1=new Vector(x1,y1);
		Vector v2=new Vector(x2,y2);
		Vector v3=new Vector(x3,y3);
		assert(distanceToLine(v1,v2,v3)-ans<.000001);
		assert(distanceToLine(x1,y1,x2,y2,x3,y3)-ans<.000001);
		double[] v4={x1,y1};
		double[] v5={x2,y2};
		double[] v6={x3,y3};		
		assert(distanceToLine(v4,v5,v6)-ans<.000001);
	}
	
}
