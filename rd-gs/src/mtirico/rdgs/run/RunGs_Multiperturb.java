package mtirico.rdgs.run;

import java.util.ArrayList;
import mtirico.rdgs.layers.LayerGs;
import mtirico.rdgs.layers.VizLayerGs;
import mtirico.tools.generictools.Tools;
import mtirico.tools.statisticaltools.DiversityIndex;
import mtirico.tools.statisticaltools.Frequency;

public class RunGs_Multiperturb {
	private String line ;
	private double entropy ;
	private int stepMax ;
	public static void main(String[] args) {
		try  {
			new RunGs_Multiperturb(args);
		}
		catch (Exception e) {
			System.out.println("Parameters required : " + "\n" 
					+ "viz -> boolean " + "\n" 
					+ "stepMax -> t > 0 " + "\n"
					+ "num pert -> n > 1 "+ "\n" 
					+ "size grid X -> x = [0 - 1024] " + "\n"
					+ "size grid Y -> y = [0 - 1024]" + "\n" 
					+ "feed -> f = [ 0.00 - 0.070 ] " + "\n"
					+ "kill -> k = [ 0.00 - 0.070 ]" + "\n" 
					+ "diff a -> Da = [0.0 - 1.0 ]" + "\n"  
					+ "diff b -> Db = [0.0 - 1.0 ]" + "\n" 
					+ "tip: start with -> true 1000 50 512 512 0.025 0.050 0.2 0.1 " );			
		} 
		
		new RunGs_Multiperturb( new String [] {
				Boolean.toString(true),
				Integer.toString(2000) ,
				Integer.toString(50) ,
				Integer.toString(512/2) ,
				Integer.toString(512/2) ,
				
				Double.toString(0.0) , 	// f
				Double.toString(0.005),		// k
				Double.toString(0.2),
				Double.toString(0.1),  	} ) ;
		
	}
		
	public RunGs_Multiperturb ( String[] args ) {	
	//	System.out.println(Arrays.asList(args));
		int posArgs = 0 ;
		boolean viz = Boolean.parseBoolean(args[posArgs++]) ;
		int  tmax=  Integer.parseInt(args[posArgs++]) ;
		int  nPer =  Integer.parseInt(args[posArgs++]) ;
		
		int[] sizeGrid = { Integer.parseInt(args[posArgs++]), Integer.parseInt(args[posArgs++])};
		
		double 	f  = Double.parseDouble(args[posArgs++]) , 
				k  = Double.parseDouble(args[posArgs++])  ,
				Da = Double.parseDouble(args[posArgs++])  , 
				Db = Double.parseDouble(args[posArgs++])  ;
		
		double [] initVals = { 1.0, 0.0 } , 
				perturb = {0.5 , 0.25 } ;
		
		LayerGs lgs = new LayerGs(sizeGrid, initVals);
		lgs.initializeCostVal(initVals);
		lgs.setGsParameters(f, k, Da, Db);
		lgs.setValueOfCellAround( perturb , sizeGrid[0]/2, sizeGrid[1]/2, 3);
	 
		for ( double[] point : Tools.getListRandomCoords(10, nPer, sizeGrid , 10) )
			lgs.setValueOfCellAround( perturb , (int) point[0], (int) point[1] , 3);
		
		VizLayerGs vizLayer = null;
		if ( viz ) vizLayer = new VizLayerGs(lgs, 1);

		int t = 0 ;
		int cellStable = 0 ;
		while ( t < tmax && cellStable < sizeGrid[0] * sizeGrid[1]  ) {
			cellStable = lgs.updateLayer();
			t++; //	System.out.println(t);
			if ( viz ) vizLayer.step();
		}
		
		ArrayList<Double> freq = new ArrayList<Double>();
		for ( double[] val : Frequency.getListFrequencyAss( lgs.getArrayVals(0), sizeGrid[0]* sizeGrid[1] , 0 , 1 , true)  ) {
//			System.out.print(String.format("%.3f",val[1]) + " - ") ;
			freq.add(val[1]);
		}
	
		stepMax = t ;
		entropy = DiversityIndex.getShannonEntropy(freq) ;
		
		line = String.format("%.3f", f) + ";" + String.format("%.3f", k)+";" +    stepMax  +";" + String.format("%.3f",   entropy)   ; 
	//	System.out.println();
	//	System.out.println(line);
	}
	public String getLine ( ) {return line ; } 
	public double getEntropy ( ) {return entropy ; } 
	public double getStepMax ( ) {return stepMax ; } 
	
}
