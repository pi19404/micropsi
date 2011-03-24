/*
 * Created on 21.04.2005
 *
 */
package org.micropsi.comp.robot.khepera5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class Kalibrate {

	private static KheperaManager comPort;
	
	private static final int QUANTITY = 10;
	private static int[][] data_array = new int[16][QUANTITY]; //p1-p8 l1-l8
	private static int x, sum = 0;
	private static int[][] statistics = new int[16][2]; //p_max p_min l_max l_min 
	private static BufferedReader tastatur = new BufferedReader(new InputStreamReader(System.in));
	
	
	public static void main(String[] args) {
		comPort = KheperaManager.getInstance(); //logger als teil von this übergeben! comport im neuen konstruktor!
		//initialise values in khepera:
		comPort.khepera.getAmbientLightSensors();
		//comPort.khepera.setLED1("2");
		System.out.println(comPort.khepera.getLs1());
		
		System.out.print("Ready to get proximity sensors input for kalibration. " +
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
		
		System.out.print("Ready to get light sensors input for kalibration. " +
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
			comPort.khepera.getAmbientLightSensors();
			System.out.println(i);
			data_array[8][i] =Integer.parseInt(comPort.khepera.getLs1());
			data_array[9][i] =Integer.parseInt(comPort.khepera.getLs2());
			data_array[10][i]=Integer.parseInt(comPort.khepera.getLs3());
			data_array[11][i]=Integer.parseInt(comPort.khepera.getLs4());
			data_array[12][i]=Integer.parseInt(comPort.khepera.getLs5());
			data_array[13][i]=Integer.parseInt(comPort.khepera.getLs6());
			data_array[14][i]=Integer.parseInt(comPort.khepera.getLs7());
			data_array[15][i]=Integer.parseInt(comPort.khepera.getLs8());
		}
		

		System.out.println("Finished! Calculating statistics...");
		
		//initialize statistics[][]
		for(int i=0;i<16;i++){
			statistics[i][0]=Integer.MIN_VALUE;
			statistics[i][1]=Integer.MAX_VALUE;
		}
		
		//calculate statistics
		System.out.println("Results: max | min");
		for (int j=0;j<16;j++){
			for (int i=0;i<QUANTITY;i++){
				statistics[j][0] = Math.max(statistics[j][0],data_array[j][i]);
				statistics[j][1] = Math.min(statistics[j][0],data_array[j][i]);
			}
			System.out.println(statistics[j][0]+" | "+statistics[j][1]);
		}
		
		
	}
}
