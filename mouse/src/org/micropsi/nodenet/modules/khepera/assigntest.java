package org.micropsi.nodenet.modules.khepera;
//import cern.colt.matrix.DoubleFactory1D;
import java.util.Date;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.*;//DateFormat;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.AbstractMatrix;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

public class assigntest {
	static long netstep = 1;

	static boolean loadGauss = true;

	static final String readpath = "/home/staff/n/navigate/eclipse/simulation/mouse/matrices/";
	// static final String readpath = "/Users/cmuehl/Applications/eclipse/workspace/matrices/";
	
	static final String writepath = "/home/staff/n/navigate/matlab_stuff/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LookupGaussian places = new LookupGaussian(1000,1000,400);
		
		if (loadGauss) {
			// loadGaussLookupTable("/home/staff/n/navigate/eclipse/simulation/mouse/matrices/gaussRandLookup.dat");
			// loadGaussLookupTable2("/home/staff/n/navigate/eclipse/simulation/mouse/matrices/gaussRandLookup.dat");
			// loadGaussLookupTable("/home/staff/n/navigate/eclipse/simulation/mouse/matrices/gaussRandLookup2.dat");
			// loadGaussLookupTable2("/home/staff/n/navigate/eclipse/simulation/mouse/matrices/gaussRandLookup2.dat");
			// loadGaussLookupTable(changepath + "/gaussRandLookup.dat");
			// loadGaussLookupTable2(changepath + "/gaussRandLookup.dat");
			
			try{
				places.setArray(ArrayReaderWriter.read2DArray(readpath + "gaussRandLookup.dat"));
			}catch(IOException e){
				e.printStackTrace();
			}
			places.writeGaussMatlabreadable(writepath+"gaussRandLookupback.dat");
			System.out.println("fine");
		}
	}

	static private void loadGaussLookupTable(String filename) {
		System.out.println("let#s see what we read in the following !");
		// read out the data column from the text file into the matrix

		double[][] temp = new double[1000][1000];

		try {
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(filename)));

			// go through the rows first
			for (int i = 0; i < temp.length; i++) {
				// go through the row and fill in the values from the text file
				for (int j = 0; j < temp[1].length; j++) {
					temp[i][j] = in.readDouble();
				}

				System.out.println("let's see what we read: " + temp[i][20]);

			}
			in.close();
		} catch (IOException e) {
			System.out
					.println("The reading process from external files is erroneous!");
		}
		// placefields.setArray(temp);

	}

	static private void loadGaussLookupTable2(String filename) {
		System.out.println("let#s see what we read in the following !");
		// read out the data column from the text file into the matrix

		double[][] temp = new double[1000][1000];

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename)));
			String line = new String();
			String[] sta = null;
			int lineCount = 0;
			do {
				line = in.readLine();
				sta = line.split(" +");
				for (int i = 0; i < sta.length; i++) {
					System.out.println("laenge : "+sta.length);
					//temp[lineCount][i] = Double.parseDouble(sta[i]);
					//System.out.println(sta[i]);
				}
				
				lineCount++;
			} while (line != null);
			for (int i = 0; i < temp[0].length; i++) {
				System.out.println(temp[i][20]);
			}
			// go through the rows first
			// for (int i = 0; i < temp.length; i++) {
			// // go through the row and fill in the values from the text file
			// for (int j = 0; j < temp[1].length; j++) {
			// temp[i][j] = Double.parseDouble(in.readLine());
			// }
			//
		// System.out.println("let#s see what we read: "+temp[i][20]);
			// }
			in.close();
		} catch (IOException e) {
//			System.out
//					.println("The reading process from external files is erroneous!");
			e.printStackTrace();
		}
		// placefields.setArray(temp);

	}

	static private int[] getPlaceCellInfo() {

		int[] placeInfo = new int[2];
		placeInfo[0] = 7;
		placeInfo[1] = 8;

		return placeInfo;
	}
}

//	
// private static void save1DMatrixMatlabreadable(DoubleMatrix1D matrix, String
// filename) {
//
// AbstractMatrix slice, row;
// slice = null;
// row = null;
//
// try {
// PrintWriter out = new PrintWriter(new FileWriter(filename));
//			      			
// //logger.debug("writing vector 1");
// for (int j = 0; j < matrix.size(); j++) {
// //if(j%100==0) logger.debug("writing vector at index "+j);
// out.println(matrix.get(j));
// //if(j%100==0) logger.debug("written vector at index "+j);
// }
// //logger.debug("writing vector 2");
// out.close();
// } catch (IOException e) {
// //logger.error("The writing process to external files is erroneous!");
// }
//
// // read out the data as column from the matrix into the text file
//			              
// // try {
// // PrintWriter out = new PrintWriter(new ObjectOutputStream(new
// FileWriter(filename)));
// //// // go through the row and fill in the values into the text file
// // for (int j = 0; j < 400; j++) {
// // out.println(((DoubleMatrix1D) row).get(j));
// // }
// //
// //
// // out.close();
// // } catch (IOException e) {logger.error("The writing process to external
// files is erroneous!");
// // }
//
// }
// }
//

