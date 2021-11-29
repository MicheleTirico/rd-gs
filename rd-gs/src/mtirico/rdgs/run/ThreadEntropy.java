package mtirico.rdgs.run;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import mtirico.tools.generictools.Tools;
import mtirico.tools.handleFile.ExpCsv;
import mtirico.tools.handleFile.HandleFolder;

public class ThreadEntropy extends Thread  {
	
	final static int numThread = 8 ;

	private int th ; 
	public ThreadEntropy (int th) {
		this.th = th; 
		
	}
	static FileWriter 	fw   ;
	
	protected static double  minF = 0.00, maxF = 0.101, incremF = 0.001  ,
	minK = 0.00 , maxK = 0.101, incremK = 0.001 ;
	
	private static  double [] arrayF = Tools.getArrayVals(minF, maxF, incremF);
	private static   double [] arrayK = Tools.getArrayVals(minK , maxK , incremK );
	private static ArrayList<double[]> params = Tools.getListParams(false , arrayF, arrayK);
	private static ArrayList<double[]> paramsVisited = new ArrayList<double[]>();
	static   int pos = 0 ;
	public void run ( ) { 
	//	System.out.println(params.size());
		
		while ( paramsVisited.size() <= params.size() && params.size() > 1 ) {
			double[] fk ;
			try {
				fk = params.get(pos);
			} catch (IndexOutOfBoundsException e) {
				break ;
			}
			paramsVisited.add(fk);
			pos++;
			 
			RunGs_Multiperturb sim = new RunGs_Multiperturb( new String [] {
				Boolean.toString(false),
				Integer.toString(10000) , //stepMax  
				Integer.toString(50) , // num pert 
				Integer.toString(512/2) ,
				Integer.toString(512/2) ,
				Double.toString(fk[0]) ,
				Double.toString(fk[1]),
				Double.toString(0.2),
				Double.toString(0.1),  	} ) ;
		
	 		System.out.println(pos +" - " + params.size() +" / " +  sim.getLine());
	 		 try {
				ExpCsv.writeLine(fw, new String [] {Double.toString(fk[0]),Double.toString(fk[1]),Double.toString(sim.getStepMax()) ,Double.toString(sim.getEntropy())} , ';' ) ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 	  
		}
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
 		
		String path = System.getProperty("user.home") + "/";
		HandleFolder hF = new HandleFolder(path) ;
		String nameFile = "test.csv";
		path = hF.createNewGenericFolder("entropy")  ;
		HandleFolder.removeFileIfExist(new String[] {path +"/" +nameFile });
		path =  path +"/" +nameFile ;
		
		fw = new FileWriter(path  , true) ;
		ExpCsv.addCsv_header( fw , "f;k;t;en" ) ;
		
		int a = 0 ;
		while ( a < numThread ) {
			ThreadEntropy m = new ThreadEntropy(a++);
			m.start();	
		}
		
	}
	


}

