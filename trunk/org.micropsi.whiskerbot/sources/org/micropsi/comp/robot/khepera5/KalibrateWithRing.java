/*
 * Created on 21.04.2005
 *
 */
package org.micropsi.comp.robot.khepera5;

import java.io.BufferedReader;
//import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.nio.channels.FileChannel;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class KalibrateWithRing {

	private static KheperaManager comPort;
	
	private static String filename = "Calibration_statistics_data.txt";
	private static String filename2 = "Calibration_raw_data.txt";
	
	private static final int QUANTITY = 50;
	private static final int RINGS = 14; //cm: 4-16,inf (also 1-13 abstand vom sensor)
	
	private static int[][] data_array = new int[8][QUANTITY*RINGS]; //p1-p8 mit 4-16cm
	private static int x, sum, mean, variance = 0;
	private static double sum_double = 0.0;
	
	private static int[][] statistics = new int[8*RINGS][4]; //p1_4cm: p_max p_min p_mean p_var
													   //p1_5cm:
													   //...
													   //p1_inf:
													   //p2_4cm:
													   //...
													   //p8_inf
	
	private static BufferedReader tastatur = new BufferedReader(new InputStreamReader(System.in));
	
	
	
	
	public static void main(String[] args) {
		comPort = KheperaManager.getInstance(); //logger als teil von this übergeben! comport im neuen konstruktor!
		//initialise values in khepera:
		
		
				
		System.out.print("Ready to get proximity sensors input for kalibration with RING=4cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");
		
		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);
		
		for (int i=0;i<QUANTITY;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=5cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");
	
		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY;i<QUANTITY*2;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=6cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*2;i<QUANTITY*3;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=7cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");
	

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*3;i<QUANTITY*4;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=8cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*4;i<QUANTITY*5;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=9cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*5;i<QUANTITY*6;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=10cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*6;i<QUANTITY*7;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=11cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*7;i<QUANTITY*8;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		System.out.print("Ready to get proximity sensors input for kalibration with RING=12cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*8;i<QUANTITY*9;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		System.out.print("Ready to get proximity sensors input for kalibration with RING=13cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*9;i<QUANTITY*10;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		System.out.print("Ready to get proximity sensors input for kalibration with RING=14cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*10;i<QUANTITY*11;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		System.out.print("Ready to get proximity sensors input for kalibration with RING=15cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*11;i<QUANTITY*12;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}

		System.out.print("Ready to get proximity sensors input for kalibration with RING=16cm. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*12;i<QUANTITY*13;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		System.out.print("Ready to get proximity sensors input for kalibration with RING=inf. " +
				"Please press key #1 to start collecting "+QUANTITY+" values: ");

		do {
			try {
				x = Integer.parseInt(tastatur.readLine());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(x!=1);


		for (int i=QUANTITY*13;i<QUANTITY*14;i++){
			comPort.khepera.getProximitySensors();
			System.out.print(i+"\r");
			data_array[0][i]=Integer.parseInt(comPort.khepera.getPs1());
			data_array[1][i]=Integer.parseInt(comPort.khepera.getPs2());
			data_array[2][i]=Integer.parseInt(comPort.khepera.getPs3());
			data_array[3][i]=Integer.parseInt(comPort.khepera.getPs4());
			data_array[4][i]=Integer.parseInt(comPort.khepera.getPs5());
			data_array[5][i]=Integer.parseInt(comPort.khepera.getPs6());
			data_array[6][i]=Integer.parseInt(comPort.khepera.getPs7());
			data_array[7][i]=Integer.parseInt(comPort.khepera.getPs8());
		}
		
		
		System.out.println("Finished! Calculating statistics...");
		
		//initialize statistics[][]
		for(int i=0;i<8*RINGS;i++){
			statistics[i][0]=Integer.MIN_VALUE;
			statistics[i][1]=Integer.MAX_VALUE;
			statistics[i][2]=Integer.MIN_VALUE;
			statistics[i][3]=Integer.MIN_VALUE;
		}
		
		//calculate statistics
		System.out.println("Results: max | min | mean | var");
		for (int j=0;j<8;j++){ //number of prox_sensors
			for(int k=0;k<RINGS;k++){ //number of rings
				for (int i=k*QUANTITY;i<(k+1)*QUANTITY;i++){
					sum += data_array[j][i];
					System.out.println("sum="+sum);
					statistics[j*RINGS+k][0] = Math.max(statistics[j*RINGS+k][0],data_array[j][i]);
					statistics[j*RINGS+k][1] = Math.min(statistics[j*RINGS+k][1],data_array[j][i]); 
				}
				mean = (int)Math.round((double)sum / (double)QUANTITY);
				statistics[j*RINGS+k][2] = mean;
				sum = 0;
				
				for (int i=k*QUANTITY;i<(k+1)*QUANTITY;i++){
					sum_double += Math.pow((double)(data_array[j][i] - mean), 2.0);
					System.out.println("sum_double="+sum_double+" | mean="+mean+" | data_array="+data_array[j][i]);//+" | data_array - mean="+(data_array[j*RINGS+k][i]) - mean);
				}
				variance = (int)(Math.sqrt((1.0/(QUANTITY-1.0))*sum_double));
				statistics[j*RINGS+k][3] = variance;				
				sum_double = 0.0;
			
				System.out.println("\t"+statistics[j*RINGS+k][0]+" \t| "+statistics[j*RINGS+k][1]+" \t| "
						+statistics[j*RINGS+k][2]+" \t| "+statistics[j*RINGS+k][3]);
			
			}
		}
		
		System.out.println("\n\n\ndata_array:");
		for(int j=0;j<8;j++){
			for(int i=0;i<QUANTITY*RINGS;i++){
				System.out.print("\t"+data_array[j][i]);
			}
			System.out.println("");
		}
	
		try{
			PrintWriter f = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			for(int j=0;j<8*RINGS;j++){
				for(int i=0;i<4;i++){
					System.out.print("\t"+statistics[j][i]);
					f.print("\t"+statistics[j][i]);
				}
				System.out.println();
				f.println();
			}
			f.close();
		}catch (IOException e) {System.out.println(filename2+": Dateifehler!");}
		
		try{
		PrintWriter f2 = new PrintWriter(new BufferedWriter(new FileWriter(filename2)));
		for(int j=0;j<8;j++){
			for(int i=0;i<QUANTITY*RINGS;i++){
				f2.print("\t"+data_array[j][i]);
			}
			f2.println();
		}
		f2.close();
	}catch (IOException e) {System.out.println(filename2+": Dateifehler!");}
	}
}