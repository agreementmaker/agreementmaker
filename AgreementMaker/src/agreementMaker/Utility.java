package agreementMaker;

import agreementMaker.userInterface.table.MyTableModel;

public class Utility {
	
	public static String[] getPercentStringList() {
		int min = 0;
		int max = 100;
		int spin = 5;
		String[] s = new String[(max/spin) +1];
		String current;
		for(int i =min, j =0; i <= max && j<s.length; i+=spin, j++) {
			current = i+"%";
			s[j] = current;
		}
		return s;
	}
	
	public static double getDoubleFromPercent(String s) {
		String s2 = s.substring(0,s.length()-1);//remove last char %
		double d = Double.parseDouble(s2);
		return d/100;
	}
	
	public static String getPercentFromDouble(double d) {
		double p = d * 100;
		return p+"%";
	}
	
	public static Object[] getNumRelList() {
		int min = 1;
		int max = 100;
		int spin = 1;
		Object[] list  = new Object[(max/spin)+1];
		String any = MyTableModel.ANY;
		for(int i =min, j =0; i <= max && j<list.length-1; i+=spin, j++) {
			list[j] = i;
		}
		list[list.length-1] = any;
		return list;
	}

}
