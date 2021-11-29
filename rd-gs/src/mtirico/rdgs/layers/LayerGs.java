package mtirico.rdgs.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import mtirico.tools.generictools.Tools;

public class LayerGs {
 	private int[] sizeGrid ;
	private Cell[][] cells ;
 	private double[ ] initVals ;
 	private int numCellOver; 
	
	/**
	 * parameters for Rd 
	 */
	private static double f ,  k ,  Da,  Db ;
	protected  ArrayList<Cell> listCell = new ArrayList<Cell> ();

	public LayerGs(  int[] sizeGrid , double [] initVals) {
		this.sizeGrid = sizeGrid;
		this.initVals = initVals ;
 		cells = new Cell[sizeGrid[0]][sizeGrid[1]];
		numCellOver  = sizeGrid[0] * sizeGrid[1] ;
	}

// INITIALIZATION GRID ------------------------------------------------------------------------------------------------------------------------------
	public void initializeCostVal ( double[] vals ) {
		initVals = vals ;
		for (int x = 0; x< sizeGrid[0]; x++)
			for (int y = sizeGrid[1] - 1 ; y >= 0 ; y--) {
				Cell c = new Cell(x,y, initVals[0] , initVals[1]);
 				cells[x][y] = c ;
 				listCell.add(c);				
		}
 	}
	
	public void initializeRandomVals ( int seedRd, double[] minVal,  double[] maxVal ) {	
		Random rd = new Random( seedRd );
		for (int x = 0; x<sizeGrid[0]; x++)
			for (int y = 0; y<sizeGrid[1]; y++) {
				double[] vals = new double[2] ;
				vals[0] = minVal[0] + rd.nextDouble() * (maxVal[0]-minVal[0]);
				vals[1] = minVal[1] + rd.nextDouble() * (maxVal[1]-minVal[1]);
				Cell c = cells[x][y] ;	
				System.out.println(c + " " + vals[0] + " " + vals[1]);
				c.setAB(vals);
		}
	}	
	

	
// SET RD PARAMETERS --------------------------------------------------------------------------------------------------------------------------------
	// set initial parameters of gray scott model
	public void setGsParameters ( double f , double k , double Da, double Db ) {
		this.f = f ;
		this.k = k ;
		this.Da = Da ;
		this.Db = Db ;
	}
	
// COMPUTE VALS -------------------------------------------------------------------------------------------------------------------------------------	

	
	// set perturbation 
	public void setValueOfCell ( double[] ab , int cellX, int cellY ) {
		cells[cellX][cellY].setAB(ab);		
	}
	
	public void setMultiPerturbs (int seed, int num, double[] ab ,double bord ,double radius) {
		for (int[] xy :Tools.getListRandomCell(seed, num, sizeGrid, bord)) 
			setValueOfCellAround(ab, xy[0], xy[1], radius);	
	}
	
	// set perturbation in radius
	public void setValueOfCellAround  ( double[] ab , int cellX, int cellY, double radius ) {
 		for ( int x = (int) Math.floor(cellX - radius) ; x <= (int) Math.ceil(cellX + radius ) ; x++  )
			for ( int y = (int) Math.floor(cellY - radius ) ; y <= (int) Math.ceil(cellY + radius ) ; y++  ) {
				cells[x][y].setAB(ab);							
			}		
	}
	 
	// update cells 
	public int updateLayer (  ) {
		numCellOver = 0 ;
		int p = 0 ;
		double eps = 0.00001 ;
		for ( int x = 0 ; x < sizeGrid[0] ; x++) {
			for ( int y = 0 ; y < sizeGrid[1] ; y++) {
				Cell c = cells[x][y] ;
				double[] vals = c.getAB();
				double 	val0 = vals[0],
						val1 = vals[1];
			
				double [] diff  = getDiffusion(c) ;
				double 	diff0 = Da * diff[0],
						diff1 = Db * diff[1] ,
						react = val0 * val1 * val1 ,
						extA = f * ( 1 - val0 ) ,
						extB = ( f + k ) * val1 ;
				double	newval0 =  val0 + diff0 - react + extA,
						newval1 =  val1 + diff1 + react - extB;
				double [] newVals = new double[] { newval0 ,newval1 } ;	 	
				c.setAB(newVals);
				if ( Math.abs(newVals[0] - val0) < eps || Math.abs(newVals[1] - val1) < eps  )		p++;
			}
		}
//		System.out.println(p);
		return p ; 
	}
	
	public int updateLayerTuring (  double a0,double b0,double c0,double a1,double b1,double c1) {
		numCellOver = 0 ;
		int p = 0 ;
		double eps = 0.00001 ;
		for ( int x = 0 ; x < sizeGrid[0] ; x++) {
			for ( int y = 0 ; y < sizeGrid[1] ; y++) {
				Cell c = cells[x][y] ;
				double[] vals = c.getAB();
				double 	val0 = vals[0],
						val1 = vals[1];
			
				double [] diff  = getDiffusion(c) ;
				double 	diff0 = Da * diff[0],
						diff1 = Db * diff[1] ,
						react = val0 * val1 * val1 ,
						extA = f * ( 1 - val0 ) ,
						extB = ( f + k ) * val1 ;
				double newval0 = diff0 + a0 * val0 + b0 * val1+ c0,
					   newval1 = diff1 + a1 * val0 + b1*val1 + c1 ;
				double [] newVals = new double[] { newval0 ,newval1 } ;	 	
				c.setAB(newVals);
				if ( Math.abs(newVals[0] - val0) < eps || Math.abs(newVals[1] - val1) < eps  )		p++;
			}
		}
//		System.out.println(p);
		return p ; 
	}
	
	
	private static double[][] kernel = new double[][] { new double[] { 0.05, 0.20, 0.05 },
		new double[] { 0.20, -1 , 0.20 }, new double[] { 0.05, 0.20, 0.05 } };

	// get Fick's diffusion 
	private double[] getDiffusion (  Cell c  ) {
		int r = 1;
		int[] posCore = c.getXY();
 		double sumA = 0 , sumB = 0 ;
		for ( int x = 0 ; x < 3 ;x++) {
			for ( int y = 0 ; y < 3 ;y ++) {
				int posX =posCore[0] + x -1, posY = posCore[1] + y -1 ;

				if ( posCore[0] == 0 ) posX= sizeGrid[0]-1;
				if ( posCore[1] == 0 ) posY= sizeGrid[1]-1;
				if ( posCore[0] == sizeGrid[0]-1) posX = 0 ; 
				if ( posCore[1] == sizeGrid[1]-1) posY = 0 ; 
				
				sumA = sumA + kernel[x][y] * cells[posX][posY].getA();
				sumB = sumB + kernel[x][y] * cells[posX][posY].getB();
			}
		}
//		System.out.print(p);
//		return new double[] { - valCoreA + sumA , - valCoreB + sumB };
	

		return new double[] { sumA , sumB };
	}
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	// get center of the world
	public ArrayList<Cell> getListCells ( ) { return listCell ; } 
	
	protected double[] getCellVals ( Cell c ) {
		return c.getAB();
	}
	public Cell getCell ( int x , int y ) {
		return cells[x][y];
	}
 
	public int[] getSizeGrid() {
		return sizeGrid;
	}
	
	public Collection<Double> getCollectionVals ( int pos ) {
		Collection<Double> coll = new HashSet<Double>();
		for ( Cell c : listCell) 
			coll.add(c.getAB()[pos]) ;
		return coll;
	}
	
	public ArrayList<Double> getArrayVals ( int pos ) {
		ArrayList<Double> arr = new ArrayList<Double>();
		for ( Cell c : listCell) 
			arr.add(c.getAB()[pos]) ;
		return arr;
	}
	
	public Collection<Double> getCollectionVals ( int pos  , int bord ) {
		Collection<Double> coll = new HashSet<Double>();
		for ( Cell c : listCell) {
			if ( c.getX() != 0 && c.getX() != sizeGrid[0] && c.getY() != 0 && c.getY() != sizeGrid[1] )
			coll.add(c.getAB()[pos]) ;
		}
		return coll;
	}
	
//	public void storeInfoGs ( String path ) throws IOException {
//		FileWriter 	fw = new FileWriter(path  , true) ;
//		expCsv.addCsv_header( fw , "en" ) ;
//		ArrayList<Double> freq = new ArrayList<Double>();
//		for ( double[] val : Frequency.getListFrequencyAss( this.getArrayVals(0), sizeGrid[0]* sizeGrid[1] , 0 , 1 , true)  ) {
////			System.out.print(String.format("%.3f",val[1]) + " - ") ;
//			freq.add(val[1]);
//		}
//		double entropy = DiversityIndex.getShannonEntropy(freq) ;
//		
// 		
//		expCsv.writeLine(fw,  new String [] {} ) ;
// 
//		fw.close();
//		
//		
//	}
	
}


