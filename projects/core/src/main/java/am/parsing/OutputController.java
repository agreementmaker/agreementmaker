package am.parsing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimpleSimilarityMatrix;
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
	 * 
	 * TODO: Add support for multiple matching tasks? - Cosmin, Oct. 20, 2013.
	 */
	public static void printDocumentOAEI(String completeFileName, MatchingTask task) throws Exception{
		AlignmentOutput output = new AlignmentOutput(task.selectionResult.getAlignment(), completeFileName);
		String sourceUri = Core.getInstance().getSourceOntology().getURI();
		String targetUri = Core.getInstance().getTargetOntology().getURI();
		output.write(sourceUri, targetUri, sourceUri, targetUri, task.matchingAlgorithm.getName());
	}
	
	/**
	 * Save an alignment to the given file.
	 * @param completeFileName
	 * @param alignment The alignment to be saved.
	 * @param matcherName The name of the matcher that created this alignment. (This is used to name the alignment when we import)
	 * @throws Exception
	 * 
	 * @deprecated Use {@link AlignmentOutput} directly.
	 */
	@Deprecated
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
	public static void printDocument(String name, MatchingTask[] task) throws Exception{
		
		Date d = new Date();
		String toBePrinted = "AGREEMENT DOCUMENT\n\n";
		toBePrinted += "Date: "+d+"\n";
		toBePrinted += "Source Ontology: "+Core.getInstance().getSourceOntology().getFilename()+"\n";
		toBePrinted += "Target Ontology: "+Core.getInstance().getTargetOntology().getFilename()+"\n\n";
		toBePrinted += getAllSelectedAlignmentsStrings(task);
		
		FileOutputStream out = new FileOutputStream(name);
	    PrintStream p = new PrintStream( out );
	    String[] lines = toBePrinted.split("\n");
	    for(int i = 0; i < lines.length; i++)
	    	p.println(lines[i]);
	    p.close();
	    out.close();
	}
	
	public static String getAllSelectedAlignmentsStrings(MatchingTask[] task) {
		String result = "";
		for(int i = 0; i < task.length; i++) {
			AbstractMatcher matcher = task[i].matchingAlgorithm;
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
			BufferedWriter bfr = new BufferedWriter(new FileWriter(new File(fullFileName)));
			
			bfr.write(matrix.getRows() + "\n");
			bfr.write(matrix.getColumns() + "\n");
			
			for( int row = 0; row < matrix.getRows(); row++ ) {
				for( int col = 0; col < matrix.getColumns(); col++ ) {
					//if( skipZeros && matrix.getSimilarity( row , col) == 0.0d ) continue;
					bfr.write(Utility.roundDouble( matrix.getSimilarity( row , col), 4) + "\n");
				}
				if( doIsolines ) bfr.write("\n"); // blank lines between scans
			}
			
			bfr.close();
		}
		
		/*
		AlignmentOutput output = new AlignmentOutput(matcher.getAlignmentSet(), fullFileName);
		String sourceUri = Core.getInstance().getSourceOntology().getURI();
		String targetUri = Core.getInstance().getTargetOntology().getURI();
		output.write(sourceUri, targetUri, sourceUri, targetUri);
		*/
	}

	public static SimilarityMatrix readMatrixFromCSV (String filename, boolean isCompressed){
		InputStream is = null; 
		SimpleSimilarityMatrix matrix = null;
		if (isCompressed == true){
			
			try {
				FileInputStream file = new FileInputStream(filename);
				BufferedInputStream myBR = new BufferedInputStream(file);
				is = new BZip2CompressorInputStream(myBR);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			try {
				FileInputStream file = new FileInputStream(filename);
				is = new BufferedInputStream(file);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		try {
			InputStreamReader iSR = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(iSR);
			int rows = Integer.parseInt(br.readLine());
			int cols = Integer.parseInt(br.readLine());
			
			matrix = new SimpleSimilarityMatrix(rows, cols, alignType.aligningClasses);
			
			for(int i = 0; i<rows ; i++)
			{
				for(int j=0; j<cols; j++){
					matrix.setSimilarity(i, j, Double.parseDouble(br.readLine()));
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return matrix;
		
	}
}
