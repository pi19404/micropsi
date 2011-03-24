package org.micropsi.nodenet.modules.khepera;
//import edu.toronto.psi.vincent.util.*;
import java.io.*;


/**
 * Sample code for using the ArrayReaderWriter class to read arrays from and write arrays to a file.
 *
 * <pre>
 * Copyright (C) 2005  Vincent Cheung (vincent@psi.toronto.edu, http://www.psi.toronto.edu/~vincent/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.</pre>
 * 
 * @author <a href="mailto:vincent@psi.toronto.edu">Vincent Cheung</a>
 * @version 1.0 11/23/05
 */
public class ArrayReaderWriterExample {
	public static void main(String[] args) {
		
		// arrays must be rectangular, i.e. same number of columns for each row and vice versa
		int[][] x = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
		
		double[] y = {Math.PI, Math.E, Double.MAX_VALUE, Double.MIN_VALUE, -1.23};
		
		// the names of the files to create.
		// the necessary directories are created.
		String filename1 = "intArray.dat";
		String filename2 = "Results/doubleArray.dat";
		
		try {
			// write the arrays to files
			ArrayReaderWriter.write(x, filename1);
			ArrayReaderWriter.write(y, filename2);
		
			// read the arrays from the files
			x = ArrayReaderWriter.read2DIntArray(filename1);
			y = ArrayReaderWriter.read1DArray(filename2);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		// display the arrays
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++)
				System.out.print(x[i][j] + " ");
			
			System.out.println();
		}
		System.out.println();		
		
		for (int i = 0; i < y.length; i++)
			System.out.print(y[i] + " ");
		
		System.out.println();
	}
} // end ArrayReaderWriterExample class
