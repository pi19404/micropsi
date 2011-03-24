package org.micropsi.nodenet.modules.khepera;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * LookUpGaussian
 * 
 * Calculates, filesaves & filewrites (symmetric) 2D arrays covered with (the
 * indices and activations of) gaussians.
 * 
 * @author Christian Muehl
 * 
 */

public class LookupGaussian {

	// TODO change LookupGaussian into LookUpGaussian!!

	double[][] array;

	int fieldsizex;

	int fieldsizey;

	int gaussnumber;

	int gausszahl;

	double placefieldadjustment;

	double sigma;

	double threshold;
/**
 * Important: placefieldadjustement is the number of units that is cut from the radius 
 * of the full placefield (a full placefield is extending to the border of the next PF)
 * 
 * @param rows
 * @param columns
 * @param gausszahl
 * @param placefieldadjustment
 */
	public LookupGaussian(int rows, int columns, int gausszahl,
			double placefieldadjustment) {
		this.array = new double[rows][columns];
		this.gausszahl = gausszahl;
		this.placefieldadjustment = placefieldadjustment;
		this.sigma = 0;
		this.threshold = 0;
		this.standardDev();
		this.threshold();
		this.fillMeUp();
		

	};
	
	public LookupGaussian(int rows, int columns, int gausszahl) {
		this.array = new double[rows][columns];
		this.gausszahl = gausszahl;
		this.placefieldadjustment = 0;
		this.sigma = 0;
		this.threshold = 0;
		
	};

	/**
	 * might later be unnecessary - when just the methods are used TODO I have
	 * to change the methods from privat to public then??
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		System.out.println("Berechnung laeuft!");

		// change your environmental size here

		LookupGaussian placefields = new LookupGaussian(1000, 1000, 20, 0);

		String filename = "Placefieldsthen";

		// placefields.writeGauss(filename);

		placefields.writeGaussMatlabreadable(filename);

		// readGauss(filename);

		// printGauss();

		System.out.println("Berechnung beendet!");
	}

	/**
	 * Returns the index of the placecell with its placefield on the x,y
	 * position.
	 */
	
	public double getPlaceCellEntry(int x, int y) {
		return this.array[x][y];
		//Math.floor for the array
	}
	
	
	
	public int SizeArray(){
		return this.array.length;
	}
	
	/**
	 * 
	 * @param 
	 */
	
	public boolean loadMatrixMatlabreadable(String path){
		
		try{
			this.array=(ArrayReaderWriter.read2DArray(path));
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
//	public double getPlaceCellActivationAt(int x, int y) {
//		return this.array[x][y] % 1;
//	}
	
	public void setArray(double[][] array) {
		this.array = array;
	}

	/**
	 * Calls the array marking method for each gaussian
	 * 
	 */

	private void fillMeUp() {
		int gaussnumber = 1;
		for (int l = 1; l <= this.gausszahl; l++) {
			int centerX = this.gaussCenter(l);
			for (int n = 1; n <= this.gausszahl; n++) {
				int centerY = this.gaussCenter(n);
				this.gaussX(centerX, centerY, gaussnumber++);
			}
		}
	}

	/**
	 * Computes gausscenter by taking into account the parameters below (the
	 * order of the gaussnumber-to-center relation is (linewise) from left to
	 * right in the array)
	 * 
	 * @param gaussnumber
	 * 
	 */
	private int gaussCenter(int gaussnumber) {
		return (int) (this.array.length / this.gausszahl * (gaussnumber - 1) + (this.array.length / this.gausszahl) / 2);
	}

	/**
	 * Computes the standard deviation necessary to cover the array with
	 * non-overlapping gaussians each cut below 99,73% (3 standard deviations)
	 * 
	 */

	private void standardDev() {
		sigma = ((double) this.array.length / (double) this.gausszahl) / 6;
	}

	/**
	 * Computes the threshold value at (under) which to cut (3 standard
	 * deviations)  -  see gaussX for the effect of "placefieldjustment"
	 * 
	 */

	private void threshold() {
		double center = ((this.array.length / this.gausszahl) / 2) - this.placefieldadjustment;
		this.threshold = ((1 / (2 * Math.PI * Math.pow(this.sigma, 2))) * Math
				.exp(-(Math.pow(center - center, 2) + Math.pow(0 - center, 2))
						/ (2 * Math.pow(this.sigma, 2))));
	}

	/**
	 * Prints gauss array as a matrix
	 * 
	 */

	private void printGauss() {
		for (int i = 0; i < this.array.length; i++) {
			for (int j = 0; j < this.array[i].length; j++) {
				System.out.print(this.array[i][j] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Writes gauss array to a data file
	 * 
	 * @param filename
	 */

	private void writeGauss(String filename) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			DataOutputStream fl = new DataOutputStream(out);
			for (int i = 0; i < this.array.length; i++) {
				for (int j = 0; j < this.array[i].length; j++) {
					fl.writeDouble(this.array[i][j]);
				}
			}
			fl.close();
		} catch (IOException e) {
			System.out.println("Problem with the creation of the file.");
		}
	}

	/**
	 * Writes gauss array to a data file that can be sensefully accessed by
	 * Matlab
	 * 
	 * @param filename
	 */

	public void writeGaussMatlabreadable(String filename) {

		try {
			PrintWriter out = new PrintWriter(new FileWriter(filename));
			for (int i = 0; i < this.array.length; i++) {
				for (int j = 0; j < this.array[i].length; j++) {
					out.println(this.array[i][j]);
				}
			}

			out.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Retrieves gauss array from a datafile
	 *	
	 * @param filename
	 */

	public void readGauss(String filename) { // throws IOException {
		// String path = File.separator + "a" + File.separator + "b";
		try {
			DataInputStream dataIn = new DataInputStream(new FileInputStream(
					filename));
			// int i = dataIn.readInt();
			
			

			
			for (int i = 0; i < this.array.length; i++) {
				for (int j = 0; j < this.array[i].length; j++) {
					this.array[i][j] = dataIn.readInt();

					System.out.println(i + " x " + j + " " + this.array[i][j]);
				}
			}
			dataIn.close();
		} catch (IOException e) {
			System.out.println("Problem finding file");
		} // woher kommst Du?

	}

	/**
	 * Computes the gauss array with equally spaced (non-overlapping) gausses
	 * (gaussnumber) with standard deviation sigma and center (centerX,centerY).
	 * Each gaussian is covering an arrea of the array where its value is above
	 * threshold. 
	 * 
	 * The computation of the threshold is affected by "placefieldadjustement",
	 * a field of LookupGaussian. The space between gaussians can be enlarged (or decreased : an
	 * overlap between gaussians will be the result! But unfortunatly with
	 * distorted borders!) by increasing (or decreasing) the value of
	 * placefieldadjustment. 
	 * 
	 * @param centerX
	 * @param centerY
	 * @param gaussnummer
	 */

	private void gaussX(int centerX, int centerY, int gaussnumber) {

		// setting of a 4 sigma x 4 sigma portion of the array to work on
		double startX = centerX - 4 * this.sigma;
		if ((int) startX < 0)
			startX = 0;
		double startY = centerY - 4 * this.sigma;
		if ((int) startY < 0)
			startY = 0;
		double endX = centerX + 4 * this.sigma;
		if ((int) endX >= this.array.length)
			endX = this.array.length - 1;
		double endY = centerY + 4 * this.sigma;
		if ((int) endY >= this.array[1].length)
			endY = this.array[1].length - 1;

		for (int i = (int) startX; i <= endX; i++) {
			for (int j = (int) startY; j <= endY; j++) {
				double g1 = (1 / (2 * Math.PI * Math.pow(this.sigma, 2)))
						* Math.exp(-(Math.pow(i - centerX, 2) + Math.pow(j
								- centerY, 2))
								/ (2 * Math.pow(this.sigma, 2)));
				if (g1 >= this.threshold)
					this.array[i][j] =  (gaussnumber + (int) g1);
			}
		}
	}
}
