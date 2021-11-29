package mtirico.rdgs.run;

import java.io.IOException;
import mtirico.rdgs.layers.LayerGs;
import mtirico.rdgs.layers.VizLayerGs;

public class get_images {
	
	static int 
			stepMax = 3500 ,
			seed = 0 ,
			numPerturbs = 100, radius = 2 ;

	static int[] sizeGrid = {512/2 , 512/2 };
	static double [] initVals = { 1.0, 0.0 } , 
			perturb = {0.5 , 0.25 } ;
	static double f = 0.025 , k = 0.06 , Da = 0.2, Db = 0.1 ;

	public static void main(String[] args) throws IOException {
 
		LayerGs lgs = new LayerGs(sizeGrid, initVals);
		lgs.initializeCostVal(initVals);
		lgs.setGsParameters(f, k, Da, Db);
		lgs.setMultiPerturbs(seed, numPerturbs, perturb, 5, radius);
		VizLayerGs viz = new VizLayerGs(lgs, 1);
		
		int t = 0 ;
		int cellStable = 0 ;
		while ( t < stepMax  ) {
			System.out.println(t);
			cellStable = lgs.updateLayer();
			viz.step();
			t++;
		}
		String path = "/home/mtirico/results/rd_gs/diffusion/bubble_small"+Da;
		
		viz.savePNG(path);	 
		
	}

}
