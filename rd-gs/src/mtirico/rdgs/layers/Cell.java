package mtirico.rdgs.layers;

public class Cell {
	private int X, Y ;
	private int[] posXY = new int[2];

	private double a , b ;
	
	public Cell(int X, int Y, double a , double b ) {
		this.X = X;
		this.Y = Y;
		this.a = a;
		this.b = b;
		posXY = new int[] { X, Y };
	}

// GET METHODS -------------------------------------------------------------------------------------------------------------------------------------- 
	public int getX() { 	return X; }
	public int getY() { 	return Y; 	}
	public int[] getXY() { 	return new int[] {X,Y} ; 	}
	
	public double getA() {	return a;	}
	public double getB() {	return b;	}
	public double[] getAB() {	return new double[] {a,b};	}
	
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public void setX(int X) {
		this.X = X;
		posXY[0] = X;
	}

	public void setY(int Y) {
		this.Y = Y;
		posXY[1] = Y;
	}

	public void setPosXY(int[] posXY) {
		this.posXY = posXY;
		this.X = posXY[0]  ;
		this.Y = posXY[Y]  ;
	}
	
	public void setA (double a ) { this.a = a; } 
	public void setB (double b ) { this.b = b; } 
	public void setAB (double[] ab ) { this.a = ab[0]; this.b = ab[1]; } 
	 


// --------------------------------------------------------------------------------------------------------------------------------------------------

}