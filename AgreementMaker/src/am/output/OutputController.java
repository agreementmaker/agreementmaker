package am.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.utility.Capsule;
/**
 * Q: What do the methods of this class do? - Cosmin. (Oct 13th, 2010)
 * A: They are responsible for saving the AlignmentSets in different formats. - Cosmin
 */
public class OutputController {

	
	public static enum AlignmentFormats {
		RDF("OAEI ( .rdf )", "rdf"),
		TXT("Text ( .txt )", "txt");
		
		String description, file_extension;
		AlignmentFormats(String desc, String ext) { description = desc; file_extension = ext; }
		String getDescription() { return description; }
		String getFileExtension() { return file_extension; }
	}
	
	public static enum ImportAlignmentFormats {
		RDF("OAEI ( .rdf )", "rdf"), 
		TABBEDTEXT("source(tab)target (.txt)", "txt");
		
		String description, file_extension;
		ImportAlignmentFormats(String desc, String ext) { description = desc; file_extension = ext; }
		String getDescription() { return description; }
		String getFileExtension() { return file_extension; }
	}
	
	public static String[] getAlignmentFormatDescriptionList() {
		AlignmentFormats[] array = AlignmentFormats.values();
		String[] formatList = new String[ array.length ];
		for( int i = 0; i < formatList.length; i++ ) {
			formatList[i] = array[i].getDescription();
		}
		return formatList;
	}
	
	public static String[] getImportAlignmentFormatDescriptionList() {
		ImportAlignmentFormats[] array = ImportAlignmentFormats.values();
		String[] formatList = new String[ array.length ];
		for( int i = 0; i < formatList.length; i++ ) {
			formatList[i] = array[i].getDescription();
		}
		return formatList;
	}
	
	public static String getAlignmentFormatExtension( int index ) {
		AlignmentFormats[] array = AlignmentFormats.values();
		return array[index].getFileExtension();
	}

	public final static String arrow = "--->";
	
	/**
	 * Save the alignment of a matcher to an alignment file in the OAEI format. 
	 * @param matcher The matcher storing the alignment.
	 * @param completeFileName Complete path of the filename to save the alignment to.
	 * @throws Exception
	 */
	public static void saveOAEIAlignment( AbstractMatcher matcher, String completeFileName) throws Exception {
		AlignmentOutput output = new AlignmentOutput(matcher.getAlignment(), completeFileName);
		String sourceUri = Core.getInstance().getSourceOntology().getURI();
		String targetUri = Core.getInstance().getTargetOntology().getURI();
		output.write(sourceUri, targetUri, sourceUri, targetUri, matcher.getName());
	}
	
	/**
	 * Save an alignment to the given file.  The alignment to be saved is taken from the first selected matcher in the Matchers Control Panel.
	 * @param completeFileName
	 * @throws Exception
	 */
	public static void printDocumentOAEI(String completeFileName) throws Exception{
		List<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
		AbstractMatcher matcher;

		//TO DO:
		//May be multiple matchers.
		int [] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
		matcher = list.get(rowsIndex[0]);
		AlignmentOutput output = new AlignmentOutput(matcher.getAlignment(), completeFileName);
		String sourceUri = Core.getInstance().getSourceOntology().getURI();
		String targetUri = Core.getInstance().getTargetOntology().getURI();
		output.write(sourceUri, targetUri, sourceUri, targetUri, matcher.getName());
	}
	
	/**
	 * Save an alignment to the given file.
	 * @param completeFileName
	 * @param alignment The alignment to be saved.
	 * @param matcherName The name of the matcher that created this alignment. (This is used to name the alignment when we import)
	 * @throws Exception
	 */
	public static void printDocumentOAEI(File completeFileName, Alignment<Mapping> alignment, String matcherName) throws Exception{

		AlignmentOutput output = new AlignmentOutput(alignment, completeFileName);
		String sourceUri = Core.getInstance().getSourceOntology().getURI();
		String targetUri = Core.getInstance().getTargetOntology().getURI();
		output.write(sourceUri, targetUri, sourceUri, targetUri, matcherName);
	}
	
	/**
	 * Save an alignment to the given file. (without calls to Core)
	 * @param completeFileName
	 * @param alignment The alignment to be saved.
	 * @param matcherName The name of the matcher that created this alignment. (This is used to name the alignment when we import)
	 * @throws Exception
	 */
	public static void printDocumentOAEI(File completeFileName, Alignment<Mapping> alignment, String matcherName, String sourceURI, String targetURI) throws Exception{

		AlignmentOutput output = new AlignmentOutput(alignment, completeFileName);
		output.write(sourceURI, targetURI, sourceURI, targetURI, matcherName);
	}
	
	// TODO: This function should differentiate between TXT, DOC, and XLS formats. (Or create separate print functions for each format)
	public static void printDocument(String name) throws Exception{
		
		int [] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
		
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
		List<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
		AbstractMatcher matcher;
		for(int i = 0; i < rowsIndex.length; i++) {
			matcher = list.get(rowsIndex[i]);
			result += "Matcher "+(i+1)+": "+matcher.getRegistryEntry().getMatcherName()+"\n\n";
			result += matcher.getAlignmentsStrings();
		}
		return result;
	}
	
	/**
	 * Save an AlignmentMatrix to a file, as Comma Separated Values of the form: sourceIndex, targetIndex, similarity. 
	 * @param matrix The matrix to be exported.
	 * @param fullFileName The file to which the matrix will be saved.
	 * @param doSort If true, the rows and columns will be sorted in order to put the highest similarities in one corner of the matrix.
	 * @throws Exception If anything goes wrong, an exception is thrown.
	 */
	public static void saveMatrixAsCSV(SimilarityMatrix matrix, String fullFileName, boolean doSort, boolean doIsolines, boolean skipZeros) throws Exception {
		
		if( matrix == null ) throw new Exception("Cannot save a null AlignmentMatrix.");  

		if( doSort ) {
			ArrayList<Capsule<Integer>> rowList = new ArrayList<Capsule<Integer>>(); 
			
			for( int row = 0; row < matrix.getRows(); row++ ) {
				Mapping[] rowMax = matrix.getRowMaxValues(row, 1);
				rowList.add( new Capsule<Integer>( rowMax[0].getSimilarity() , new Integer(row) ));
			}
			
			ArrayList<Capsule<Integer>> colList = new ArrayList<Capsule<Integer>>(); 
			
			for( int col = 0; col < matrix.getColumns(); col++ ) {
				Mapping[] colMax = matrix.getColMaxValues(col, 1);
				colList.add( new Capsule<Integer>( colMax[0].getSimilarity() , new Integer(col) ));
			}
			
			Collections.sort(rowList);
			Collections.sort(colList);
			
			
			
			FileOutputStream out = new FileOutputStream(fullFileName);
			PrintStream p = new PrintStream( out );
			
			for( int row = 0; row < matrix.getRows(); row++ ) {
				for( int col = 0; col < matrix.getColumns(); col++ ) {
					if( skipZeros && colList.get(col).getPayload().intValue() == 0 ) continue;
					p.println( row + "," + col + "," + 
							Utility.roundDouble( matrix.getSimilarity( rowList.get(row).getPayload().intValue() , colList.get(col).getPayload().intValue()), 4 ) );
				}
				if( doIsolines ) p.println(""); // blank lines between scans
			}
			
			p.close();
			out.close();
		} else {
			FileOutputStream out = new FileOutputStream(fullFileName);
			PrintStream p = new PrintStream( out );
			
			for( int row = 0; row < matrix.getRows(); row++ ) {
				for( int col = 0; col < matrix.getColumns(); col++ ) {
					if( skipZeros && matrix.getSimilarity( row , col) == 0.0d ) continue;
					p.println( row + "," + col + "," + Utility.roundDouble( matrix.getSimilarity( row , col), 4) );
				}
				if( doIsolines ) p.println(""); // blank lines between scans
			}
			
			p.close();
			out.close();
		}
		
		/*
		AlignmentOutput output = new AlignmentOutput(matcher.getAlignmentSet(), fullFileName);
		String sourceUri = Core.getInstance().getSourceOntology().getURI();
		String targetUri = Core.getInstance().getTargetOntology().getURI();
		output.write(sourceUri, targetUri, sourceUri, targetUri);
		*/
	}

}
