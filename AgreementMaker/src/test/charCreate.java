package test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

public class charCreate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		char c = '\u2283';
		Character ch = new Character(c);
		OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
		System.out.println(out.getEncoding());

		System.out.println(c+" "+ch);
	}

}
