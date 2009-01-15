package agreementMaker.output;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;

import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;

public class OutputController {

	public final static String TXT = "txt";
	public final static String DOC = "doc";
	public final static String XLS = "xls";
	

	public final static String arrow = "--->";
	
	public static void printDocument(String name) throws Exception{
		
		int [] rowsIndex = Core.getInstance().getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
		
		Date d = new Date();
		String toBePrinted = "AGREEMENT DOCUMENT\n\n";
		toBePrinted += "Date: "+d+"\n";
		toBePrinted += "Source Ontology: "+Core.getInstance().getSourceOntology().getFilename()+"\n";
		toBePrinted += "Target Ontology: "+Core.getInstance().getTargetOntology().getFilename()+"\n\n";
		toBePrinted += getAllSelectedAlignmentsStrings(rowsIndex);
		
		FileOutputStream out = new FileOutputStream(name);
	    PrintStream p = new PrintStream( out );
	    String[] lines = toBePrinted.split("\n");
	    for(int i = 0; i < lines.length; i++)
	    	p.println(lines[i]);
	    p.close();
	    out.close();
	}
	
	public static String getAllSelectedAlignmentsStrings(int[] rowsIndex) {
		String result = "";
		ArrayList<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
		AbstractMatcher matcher;
		for(int i = 0; i < rowsIndex.length; i++) {
			matcher = list.get(rowsIndex[i]);
			result += "Matcher "+(i+1)+": "+matcher.getName().getMatcherName()+"\n\n";
			result += matcher.getAlignmentsStrings();
		}
		return result;
	}

}
