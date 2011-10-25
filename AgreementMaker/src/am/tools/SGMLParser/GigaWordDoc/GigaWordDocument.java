/**
 * 
 */
package am.tools.SGMLParser.GigaWordDoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;

import am.tools.SGMLParser.GigaWordParser.GigaWordParser;
import am.tools.SGMLParser.GigaWordParser.GigaWordParserConstants;
import am.tools.SGMLParser.GigaWordParser.ParseException;

/**
 * @author Michele Caci
 * This class will host a document from the GigaWord news wire
 */
public class GigaWordDocument {
	
	private ArrayList<String> paragraphList;
	private File document;

	public GigaWordDocument(){
		
		paragraphList = new ArrayList<String>();
		
		this.openFile();
		this.parse();
//		System.out.println(createFullText());
	}
	
	public String createFullText(){
		String text = "";
		Iterator<String> iParagraph = paragraphList.iterator();
		while(iParagraph.hasNext()){
			text += (iParagraph.next() + " ");
		}
		return text;
	}
	
	/** 
	 * This function allows to open the document to parse
	 */
	public void openFile() {
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		this.setDocument(fc.getSelectedFile());
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	public void parse() {
		try {
			GigaWordParser parser = new GigaWordParser(new FileInputStream(this.getDocument()));
			parser.start();
			setParagraphList(parser.getText());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the paragraphList
	 */
	public ArrayList<String> getParagraphList() {
		return paragraphList;
	}

	/**
	 * @param paragraphList the paragraphList to set
	 */
	public void setParagraphList(ArrayList<String> paragraphList) {
		this.paragraphList = paragraphList;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(File document) {
		this.document = document;
	}

	/**
	 * @return the document
	 */
	public File getDocument() {
		return document;
	} 
	
}

class GigaWordTest{
	
    public static void main(String args[]) throws ParseException {
    		GigaWordDocument gwd = new GigaWordDocument();
            
    }
}
