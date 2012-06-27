package am;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.IntDoublePair;
import am.userInterface.table.MatchersControlPanelTableModel;



public class Utility {
	public final static String UNEXPECTED_ERROR = "Unexpected System Error.\nTry to reset the system and repeat the operation.\nContact developers if the error persists.";
	public final static String OUT_OF_MEMORY = "Operation aborted\n\n" +
			"The system has run out of memory.\n" +
			"Try to run the system with more heap space\n" +
			"(e.g., java -Xms64m -Xmx2048m -jar AgreementMaker.jar).";

	//USED by the tuning alg.
	//we can't generate this array with a for because the 0.35 + 0.05 = 0.39 in java because of double representations, exclude <10 and >90
	public final static double[] STEPFIVE = {0.0, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.0};
	public final static String[] STEPFIVE_INT = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90" };

	private static DecimalFormat df5;


	//**************************************************USER INTERFACE UTILITIES***********************************************************


	//methods to convert a double into a percent string with only 1 decimanl value
	public static String[] getPercentStringList() {
		int min = 0;
		int max = 100;
		int spin = 1;
		String[] s = new String[(max/spin) +1];
		String current;
		for(int i =min, j =0; i <= max && j<s.length; i+=spin, j++) {
			current = i+"%";
			s[j] = current;
		}
		return s;
	}


	public static double getDoubleWithTwoDecimalDigits(double value) {
		DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
		return Double.valueOf(df.format(value));
	}

	public static double getDoubleWithFiveDecimalDigits(double value) {
		if(df5 == null) df5 = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.US));
		return Double.valueOf(df5.format(value));
	}

	public static String[] getPercentDecimalsList() {
		int min = 0;
		int max = 100;
		int spin = 1;
		String[] s = new String[(max/spin) +1];
		String current;
		for(int i =min, j =0; i <= max && j<s.length; i+=spin, j++) {
			Double k = new Double( Double.parseDouble(Integer.toString(i)) / 100 );
			current =  k.toString() ;
			s[j] = current;
		}
		return s;
	}

	// return a string array
	public static String[] getPercentDecimal( double start, double eachStep, int numSteps ) {

		String[] s = new String[numSteps];
		for(int i = 0; i < s.length; i++) {
			s[i] = Double.toString(start + i*eachStep);
		}
		return s;
	}

	// return a double array with regular entries
	public static double[] getDoubleArray( double start, double eachStep, int numSteps ) {

		double[] s = new double[numSteps];
		for(int i = 0; i < s.length; i++) {
			s[i] = start + i*eachStep;
		}
		return s;
	}

	public static double getDoubleFromPercent(String s) {
		String s2 = s.substring(0,s.length()-1);//remove last char %
		double d = Double.parseDouble(s2);
		return d/100;
	}

	/**
	 * Return a percent value with 0 decimal value and the % at the end. used in display alignments function in the canvas
	 * Used also to manage the threshold value
	 * @param inValue a double value between 0 & 1
	 * @return
	 */
	public static String getNoDecimalPercentFromDouble(double d) {
		int i = (int)(d*100);
		return i+"%";
	}

	/**
	 * Return a percent value with 1 decimal value and the % at the end: 98.7% 
	 * used everywhere for example to show quality and reference evaluation values in the table
	 * @param inValue a double value between 0 & 1
	 * @return
	 */
	public static String getOneDecimalPercentFromDouble(double inValue){
		String shortString = "";
		if(!(new Double(inValue)).isNaN()) {
			double d = inValue * 100;
			DecimalFormat oneDec = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
			shortString = (oneDec.format(d));
			shortString+="%";
		}
		else shortString = "0.0%";
		return shortString;

	}

	public static String[] getNumRelList() {
		int min = 1;
		int max = 100;
		int spin = 1;
		String[] list  = new String[(max/spin)+1];
		String any = MatchersControlPanelTableModel.ANY;
		for(int i =min, j =0; i <= max && j<list.length-1; i+=spin, j++) {
			list[j] = i+"";
		}
		list[list.length-1] = any;
		return list;
	}

	public static int getIntFromNumRelString(String n) {
		int i;
		try {
			i = Integer.parseInt(n);
		}
		catch(Exception e) {
			//the value is the string any
			i = AbstractMatcher.ANY_INT;
		}
		return i;
	}

	public static String getStringFromNumRelInt(int n) {
		String s;
		if(n ==AbstractMatcher.ANY_INT)
			s = MatchersControlPanelTableModel.ANY;
		else s = n+"";
		return s;
	}

	public static String getYesNo(boolean b) {
		if(b)
			return "yes";
		else return "no";
	}



	/**
	 * This function displays the JOptionPane with title and descritpion
	 *
	 * @param desc 		thedescription you want to display on option pane
	 * @param title 	the tile you want to display on option pane
	 */
	public  static void displayMessagePane(String desc, String title) {
		if(title == null)
			title = "Message Dialog";
		JOptionPane.showMessageDialog(null, desc, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static void displayTextAreaPane(String desc, String title) {
		if(title == null)
			title = "Message Dialog";
		String[] split = desc.split("\n");
		int rows = Math.min(30, split.length+5);
		int columns = 0;
		for(int i = 0; i<split.length; i++) {
			if(columns < split[i].length())
				columns = split[i].length();
		}
		columns = Math.min(80, columns/2 +5); //columns/2 because each character is longer then a column so at the end it fits doing this

		JTextArea ta = new JTextArea(desc,rows,columns);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		//ta.setEditable(false);
		JScrollPane sp = new JScrollPane(ta);
		JOptionPane.showMessageDialog(null, sp, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static void displayTextAreaWithDim(String desc, String title, int rows, int columns ) {
		if(title == null)
			title = "Message Dialog";	
		JTextArea ta = new JTextArea(desc,rows,columns);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		//ta.setEditable(false);
		JScrollPane sp = new JScrollPane(ta);
		JOptionPane.showMessageDialog(null, sp, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static void displayErrorPane(String desc, String title) {
		if(title == null)
			title = "Error";
		try {
			JOptionPane.showMessageDialog(null, desc,title, JOptionPane.ERROR_MESSAGE);
		} catch( HeadlessException ex ) {
			Logger log = Logger.getLogger(Utility.class);
			log.error(desc);
		}
	}


	public static boolean displayConfirmPane(String desc, String title) {
		if(title == null)
			title = "Confirmation required";
		int res =  JOptionPane.showConfirmDialog(Core.getUI().getUIFrame(),
				desc,
				title,
				JOptionPane.YES_NO_OPTION);	
		if(res == JOptionPane.YES_OPTION)
			return true;
		else return false;
	}


	//********************************************MATH UTILITIES**************************************************************

	//return f(x) = 1 / ( 1 + exp( -5 (x - k) ) )
	//sigmoid function used by rimom in the weighted average of similarities
	public static double getSigmoidFunction(double d) {
		double k = 0.5;
		double numerator = 1;
		double exponent = (-5) * (d - k);
		double exp = Math.exp(exponent);
		double denominator = 1 + exp;
		return numerator / denominator;
	}

	//return f(x) = 1 / ( 1 + exp( -5 (x - k) ) )
	//sigmoid function used by rimom in the weighted average of similarities
	public static double getModifiedSigmoidFunction(double d) {
		//if(d <= 0) return 0;
		double sigmoid = getSigmoidFunction(Math.abs(d));
		return sigmoid;
		//return (sigmoid - 0.5) * 2;
	}	

	public static double getSumOfArray(double[] array) {
		double sum = 0;
		for(int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/**
	 * @param array An array of doubles.
	 * @return Average = sum of all array entries / length.
	 */
	public static double getAverageOfArray(double[] array) {
		double sum = getSumOfArray(array);
		sum = sum / (double) array.length;
		return sum;
	}

	/**
	 * 
	 * @param array
	 * @return
	 */
	public static double getAverageOfArrayNonZeroValues(double[] array) {
		double sum=0;
		double tot = 0;
		for(int i = 0; i< array.length; i++) {
			if(array[i] != 0) {
				tot++;
				sum+=array[i];
			}
		}
		sum = sum / tot;
		return sum;
	}

	public static IntDoublePair getMaxOfRow(double[][] matrix, int row) {
		IntDoublePair max = IntDoublePair.createFakePair();
		for(int i = 0; i < matrix[row].length; i++) {
			if(matrix[row][i] > max.value) {
				max.value = matrix[row][i];
				max.index = i;
			}
		}
		return max;
	}

	//order the array of IntDoublePair so that the minimum is at the beginning
	//the only element in the wrong position is the key element that has to be moved before or later or staeid there
	public static void adjustOrderPairArray(IntDoublePair[] intDoublePairs, int k) {
		IntDoublePair currentPair;
		IntDoublePair nextPair;
		//THE TWO CASES ARE EXCLUSIVE 
		//if is higher the the next val i need to move the element to the right
		for(int i = k; i < intDoublePairs.length -1 && intDoublePairs[i].value > intDoublePairs[i+1].value; i++) {
			currentPair = intDoublePairs[i];
			nextPair = intDoublePairs[i+1];
			intDoublePairs[i] = nextPair;
			intDoublePairs[i+1] = currentPair;
		}

		//if is lower the the prev val i need to move the element to the left
		for(int i = k; i > 0 && intDoublePairs[i].value < intDoublePairs[i-1].value; i--) {
			currentPair = intDoublePairs[i];
			nextPair = intDoublePairs[i-1];
			intDoublePairs[i] = nextPair;
			intDoublePairs[i-1] = currentPair;
		}

	}

	public static double getMaxOfMatrix(double[][] matrix) {
		double max = Double.MIN_VALUE;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				double current = matrix[i][j];
				if(current > max)
					max = current;
			}
		}
		return max;
	}

	public static double getSumOfMatrix(double[][] matrix) {
		double sum = 0;
		for(int i = 0; i < matrix.length; i++) {
			sum += getSumOfArray(matrix[i]);
		}
		return sum;
	}

	public static int getSumOfIntMatrix(int[][] matrix) {
		int sum = 0;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				sum += matrix[i][j];
			}
		}
		return sum;
	}



	//these two methods have to be used only on square matrix
	//they return the sum of values in the first half of the array
	public static double getSumOfHalfMatrix(double[][] matrix) {
		double sum = 0;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < i; j++) {
				sum += matrix[i][j];
			}
		}
		return sum;
	}

	public static int getSumOfHalfIntMatrix(int[][] matrix) {
		int sum = 0;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < i; j++) {
				sum += matrix[i][j];
			}
		}
		return sum;
	}

	//an array avg of the two, they must have same size
	public static double[] avgArrays(double[] array1, double[] array2) {
		double[] result = new double[array1.length];
		for(int i = 0; i < array1.length; i++) {
			result[i] = (array1[i] + array2[i] ) / 2;
		}
		return result;
	}

	//divide the array with the divisor
	public static double[] avgArrayAndDouble(double[] array,
			double doublevalue) {

		double[] result = new double[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] =  (array[i] + doublevalue ) / 2;
		}

		return result;
	}

	//return a new matrix with only values higher than the threshold
	public static double[][] cutMatrix(double[][] similarityMatrix,
			double threshold) {
		double[][] result = new double[similarityMatrix.length][similarityMatrix[0].length];
		for(int i= 0; i < similarityMatrix.length; i++) {
			for(int j = 0; j < similarityMatrix[0].length; j++) {
				result[i][j] = 0;
				if(similarityMatrix[i][j] >= threshold) {
					result[i][j] = similarityMatrix[i][j] ;
				}
			}
		}
		return result;
	}

	/* from: http://www.rgagnon.com/javadetails/java-0016.html */
	public static double roundDouble(double d, int decimalPlace){
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	/* from: http://www.rgagnon.com/javadetails/java-0016.html */
	public static float roundFloat(float d, int decimalPlace){
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	//*******************************************STRING UTILITIES********************************************************
	public static boolean isIrrelevant(String s) {
		return s == null || s.equals("") || s.equals(" ");
	}

	public static boolean startsWithSpace(String s) {
		if(s!=null && s.length() >0) {
			char first = s.charAt(0);
			if(first == ' ')
				return true;
		}
		return false;
	}

	public static boolean endsWithSpace(String s) {
		if(s!=null && s.length() >0) {
			char last = s.charAt(s.length()-1);
			if(last == ' ')
				return true;
		}
		return false;
	}

	//I need to add the string only if it's relevant
	//and i need to put a space only if there is not already one
	public static String smartConcat(String first, String second) {
		//Attention: first can be irrelevant in fact the first time i start concatenating is often empty the first string.
		//it may happen that there will be 2 space between the two words, but is not important
		String result = first;
		if(!isIrrelevant(second)) {
			if(!(endsWithSpace(first) || startsWithSpace(second) || first.equals(""))) {
				result += " ";
			}
			result += second;
		}
		return result;
	}
	//TIME OPERATIONS**********************************************************************
	public static String getFormattedTime(long totMS){

		//return the time in hh:mm:ss:msmsms starting from the total time in ms

		//msmsms
		long msmsms = totMS % 1000;
		//ss
		long totS = totMS/1000;
		long ss = totS % 60;
		//mm
		long totM = totS/60;
		long mm = totM % 60;
		//hh
		long totH = totM/60;
		long hh = totH % 60;


		return hh+"h "+mm+"m "+ss+"s "+msmsms+"ms";
	}


	/**
	 * createIntListToN: creates an ArrayList of n integers from 0 to n-1
	 * useful to create a list for considering all the values of the rows or columns of the alignment matrix
	 * Takes O(n)
	 * @param n size of the ArrayList (n-1 is the last value)
	 * @return arrayList of integer values from 0 to n-1 
	 * @author michele 
	 */
	public static ArrayList<Integer> createIntListToN(int n){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < n; i++){
			list.add(i);
		}
		return list;		
	}

	public static String treatString(String label){
		if(label==null) return null;
		//Remove anything from a string that isn't a Character or a space
		//e.g. numbers, punctuation etc.
		String result = "";
		for(int i=0; i<label.length(); i++){
			if( Character.isLetter(label.charAt(i)) || Character.isWhitespace( label.charAt(i) ) ){
				result += label.charAt(i);
			}
		}
		label = result;

		String lower = label.toLowerCase();

		//Remove non-content words
		if(lower.startsWith("has"))
			label = label.substring(3);
		else if(lower.startsWith("is"))
			label = label.substring(2);
		else if(lower.startsWith("are"))
			label = label.substring(3);
		else if(lower.startsWith("be"))
			label = label.substring(2);
		else if(lower.endsWith(" by"))
			label = label.substring(0, label.length()-3);
		else if(lower.endsWith(" in"))
			label = label.substring(0, label.length()-3);
		else if(lower.endsWith(" at"))
			label = label.substring(0, label.length()-3);
		else if(lower.endsWith(" to"))
			label = label.substring(0, label.length()-3);
		else if(lower.endsWith(" on"))
			label = label.substring(0, label.length()-3);
		else if(lower.endsWith(" for"))
			label = label.substring(0, label.length()-4);

		int len = label.length();
		//Separate words with spaces
		for(int i=0;i<len-1; i++){
			if( Character.isLowerCase(label.charAt(i)) &&  Character.isUpperCase(label.charAt(i+1)) ){

				label = label.substring(0,i+1) + " " + label.substring(i+1); len++;}
		}
		label.toLowerCase();

		return label.trim();
	}

	/**
	 * Reads a File into a String.
	 * @param file
	 * @return Contents of the file in a String.
	 * @throws java.io.IOException
	 */
	public static String readFileAsString(File file) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(file));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}
}