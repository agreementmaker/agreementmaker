package am.extension.userfeedback.preset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.log4j.Logger;

import am.app.Core;
import am.utility.Pair;

import com.thoughtworks.xstream.XStream;

/**
 * Used to save and retrieve UFL experiment presets to the disk.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class PresetStorage {

	private static final Logger LOG = Logger.getLogger(PresetStorage.class);
	
	private static final String MATCHING_TASKS_FILE = "MatchingTaskPresets.ser.bz2";
	private static final String EXPERIMENTS_FILE = "ExperimentPresets.ser.bz2";
	
	public static MatchingTaskPreset[] getMatchingTaskPresets() {
		List<MatchingTaskPreset> loadedTaskList = loadMatchingTaskPresets();
		
		if(loadedTaskList == null) {
			return new MatchingTaskPreset[0];
		}
		
		// make sure the files exist
		List<MatchingTaskPreset> filteredTaskList = new LinkedList<>();
		for( MatchingTaskPreset p : loadedTaskList) {
			File sourceOntFile = new File(p.getSourceOntology());
			if( !sourceOntFile.exists() ) {
				LOG.debug("Source ontology file not found. Ignoring preset:" + p);
				continue;
			}
			
			File targetOntFile = new File(p.getTargetOntology());
			if( !targetOntFile.exists() ) {
				LOG.debug("Target ontology file not found. Ignoring preset:" + p);
				continue;
			}
			
			filteredTaskList.add(p);
		}
		
		return filteredTaskList.toArray(new MatchingTaskPreset[0]);
	}
	
	public static void addMatchingTaskPresets(Collection<MatchingTaskPreset> presets) {
		for( MatchingTaskPreset p : presets ) {
			addMatchingTaskPreset(p);
		}
	}
	
	/**
	 * Add a matching task preset to the presets list, and save it to the disk.
	 */
	public static void addMatchingTaskPreset(MatchingTaskPreset p) {
		
		if( !isValid(p) ) {
			LOG.error("The matching task '" + p + "' is incorrectly specified.");
			return;
		}
		List<MatchingTaskPreset> loadedTaskList = loadMatchingTaskPresets();
		
		if( loadedTaskList == null ) loadedTaskList = new LinkedList<>();
		loadedTaskList.add(p);
		
		saveMatchingTaskPresets(loadedTaskList);
	}
	
	public static void removeMatchingTaskPreset(MatchingTaskPreset p) {
		List<MatchingTaskPreset> loadedTaskList = loadMatchingTaskPresets();
		
		loadedTaskList.remove(p);
		
		saveMatchingTaskPresets(loadedTaskList);
	}
	
	/**
	 * @param name
	 * @return The MatchingTaskPreset with the given name. null if there is no
	 *         MatchingTaskPreset with that name.
	 */
	public static MatchingTaskPreset getMatchingTaskPreset(String name) {
		List<MatchingTaskPreset> loadedTaskList = loadMatchingTaskPresets();
		for( MatchingTaskPreset p : loadedTaskList ) {
			if( p.getName().equals(name) ) return p;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static List<MatchingTaskPreset> loadMatchingTaskPresets() {
		final String rootDir = Core.getInstance().getRoot();
		File inputFile = new File(rootDir + File.separator + Core.SETTINGS_DIR + File.separator + MATCHING_TASKS_FILE);
		if( !inputFile.exists() ) {
			LOG.error("Matching Task Presets will not be read.  File not found: " + inputFile);
			return null;
		}
		
		List<MatchingTaskPreset> presetList = null;
		try {
			FileInputStream fileIn = new FileInputStream(inputFile);
			BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(fileIn);
			ObjectInputStream in = new ObjectInputStream(bzIn);
			
			presetList = (List<MatchingTaskPreset>) in.readObject();
			
			in.close();
			bzIn.close();
			fileIn.close();
			
			LOG.info("Deserialized matching task presets from: " + inputFile);
		} catch (IOException | ClassNotFoundException e) {
			LOG.error(e);
			return null;
		}
		
		return presetList;
	}
	
	private static void saveMatchingTaskPresets(List<MatchingTaskPreset> presets) {
		final String rootDir = Core.getInstance().getRoot();
		File outputDir = new File(rootDir + File.separator + Core.SETTINGS_DIR);
		if( !outputDir.exists() && !outputDir.mkdirs() ) {
			LOG.error("Presets will not be stored.  Could not create directory: " + outputDir);
			return;
		}
		
		// serialize the object
		try {
			final String outputFile = outputDir.getAbsolutePath() + File.separator + MATCHING_TASKS_FILE;
			
			FileOutputStream fileOut = new FileOutputStream(outputFile);
			BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(fileOut); // bzip the serialized object to save space
			ObjectOutputStream out = new ObjectOutputStream(bzOut);
			
			Collections.sort(presets);
			out.writeObject(presets);
			
			out.close();
			bzOut.close();
			fileOut.close();
			
			LOG.debug("Serialized matching task presets to: " + outputFile);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	public static void resetExperimentPresets() {
		List<ExperimentPreset> loadedExperimentList = new LinkedList<>();
		saveExperimentPresets(loadedExperimentList);
	}
	
	public static void resetMatchingTaskPresets() {
		List<MatchingTaskPreset> loadedTaskList = new LinkedList<>();
		saveMatchingTaskPresets(loadedTaskList);
	}
	
	/**
	 * Add a matching task preset to the presets list, and save it to the disk.
	 */
	public static void addExperimentPreset(ExperimentPreset p) {
		List<ExperimentPreset> loadedExperimentList = loadExperimentPresets();
		
		if( loadedExperimentList == null ) loadedExperimentList = new LinkedList<>();
		loadedExperimentList.add(p);
		
		saveExperimentPresets(loadedExperimentList);
	}
	
	public static void removeExperimentPreset(ExperimentPreset p) {
		List<ExperimentPreset> loadedTaskList = loadExperimentPresets();
		
		loadedTaskList.remove(p);
		
		saveExperimentPresets(loadedTaskList);
	}
	
	public static ExperimentPreset[] getExperimentPresets() {
		List<ExperimentPreset> loadedTaskList = loadExperimentPresets();
		
		if(loadedTaskList == null) {
			return new ExperimentPreset[0];
		}
		
		return loadedTaskList.toArray(new ExperimentPreset[0]);
	}
	
	/**
	 * @param name
	 * @return The ExperimentPreset with the given name. null if there is no
	 *         ExperimentPreset with that name.
	 */
	public static ExperimentPreset getExperimentPreset(String name) {
		List<ExperimentPreset> loadedExperimentList = loadExperimentPresets();
		for( ExperimentPreset e : loadedExperimentList ) {
			if( e.getName().equals(name) ) return e;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static List<ExperimentPreset> loadExperimentPresets() {
		final String rootDir = Core.getInstance().getRoot();
		File inputFile = new File(rootDir + File.separator + Core.SETTINGS_DIR + File.separator + EXPERIMENTS_FILE);
		if( !inputFile.exists() ) {
			LOG.error("Experiment Presets will not be read.  File not found: " + inputFile);
			return null;
		}
		
		List<ExperimentPreset> presetList = null;
		try {
			FileInputStream fileIn = new FileInputStream(inputFile);
			BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(fileIn);
			ObjectInputStream in = new ObjectInputStream(bzIn);
			
			presetList = (List<ExperimentPreset>) in.readObject();
			
			in.close();
			bzIn.close();
			fileIn.close();
			
			LOG.info("Deserialized experiment presets from: " + inputFile);
		} catch (IOException | ClassNotFoundException e) {
			LOG.error(e);
			return null;
		}
		
		return presetList;
	}
	
	private static void saveExperimentPresets(List<ExperimentPreset> presets) {
		final String rootDir = Core.getInstance().getRoot();
		File outputDir = new File(rootDir + File.separator + Core.SETTINGS_DIR);
		if( !outputDir.exists() && !outputDir.mkdirs() ) {
			LOG.error("Expriment Presets will not be stored.  Could not create directory: " + outputDir);
			return;
		}
		
		// serialize the object
		try {
			final String outputFile = outputDir.getAbsolutePath() + File.separator + EXPERIMENTS_FILE;
			
			FileOutputStream fileOut = new FileOutputStream(outputFile);
			BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(fileOut); // bzip the serialized object to save space
			ObjectOutputStream out = new ObjectOutputStream(bzOut);
			
			out.writeObject(presets);
			
			out.close();
			bzOut.close();
			fileOut.close();
			
			LOG.debug("Serialized experiment presets to: " + outputFile);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	/**
	 * @param runs The list of runs we are saving.
	 * @param fileName If this filename is relative (doesn't start with /) it will be made relative to the AM_ROOT.
	 */
	public static void saveBatchModeRunsToXML(List<Pair<MatchingTaskPreset,ExperimentPreset>> runs, String fileName) {
		if( !fileName.startsWith(File.separator) ) {
			fileName = Core.getInstance().getRoot() + fileName;
		}
		
		List<Pair<MatchingTaskPreset,ExperimentPreset>> runsCopy = new LinkedList<>();
		runsCopy.addAll(runs);
		
		Comparator<Pair<MatchingTaskPreset,ExperimentPreset>> c = 
				new Comparator<Pair<MatchingTaskPreset,ExperimentPreset>>() {
			@Override
			public int compare(Pair<MatchingTaskPreset, ExperimentPreset> o1,
					Pair<MatchingTaskPreset, ExperimentPreset> o2) {
				int mt = o1.getLeft().getName().compareTo(o2.getRight().getName());
				if( mt == 0 ) {
					return o1.getRight().getName().compareTo(o2.getRight().getName());
				}
				return mt;
			}
			
		};
		
		// serialize in order
		Collections.sort(runsCopy, c);
		
		XStream xs = getXStream();
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			xs.toXML(runs,fos);
			fos.close();
			LOG.info("Saved UFL batch mode experiments: " + fileName);
		}
		catch (IOException ioex) {
			LOG.error(ioex);
		}
	}
	
	private static XStream getXStream() {
		XStream xs = new XStream();
		xs.registerConverter(new UFLExperimentParametersConverter());
		xs.alias("pair", Pair.class);
		return xs;
	}
	
	public static List<Pair<MatchingTaskPreset, ExperimentPreset>> loadBatchModeRunsFromXML(String filePath) {
		if( !filePath.startsWith(File.separator) ) {
			filePath = Core.getInstance().getRoot() + filePath;
		}
		
		File inputFile = new File(filePath);
		if( !inputFile.exists() )
			throw new AssertionError("UFL Batch Mode runs file not found.", new FileNotFoundException(filePath));
		
		XStream xs = getXStream();
		@SuppressWarnings("unchecked")
		List<Pair<MatchingTaskPreset,ExperimentPreset>> presetList =
				(List<Pair<MatchingTaskPreset, ExperimentPreset>>) xs.fromXML(inputFile);
		LOG.info("Deserialized experiment runs from: " + inputFile);

		return presetList;
	}
	
	/**
	 * Verify that a MatchingTaskPreset is correctly specified.
	 * @return true if the MatchingTaskPreset is valid, false otherwise.
	 */
	public static boolean isValid(MatchingTaskPreset p) {
		if( p == null ) {
			LOG.trace("MatchingTaskPreset is null.");
			return false;
		}
		
		if( p.getName() == null || p.getName().isEmpty() ) {
			LOG.trace("MatchingTaskPreset name is missing.");
			return false;
		}

		if( p.getSourceOntology() == null ) {
			LOG.trace("MatchingTaskPreset sourceOntology cannot be null");
			return false;
		}
		File sourceFile = new File(p.getSourceOntology());
		if( !sourceFile.exists() || sourceFile.isDirectory() ) {
			LOG.trace("MatchingTaskPreset sourceOntology file not found.");
			return false;
		}
		
		if( p.getTargetOntology() == null ) {
			LOG.trace("MatchingTaskPreset targetOntology cannot be null");
			return false;
		}
		File targetFile = new File(p.getTargetOntology());
		if( !targetFile.exists() || targetFile.isDirectory() ) {
			LOG.trace("MatchingTaskPreset targetOntology file not found.");
			return false;
		}
		
		if( p.hasReference() ) {
			if( p.getReference() == null ) {
				LOG.trace("MatchingTaskPreset reference cannot be null when hasReference() is true.");
				return false;
			}
			File refFile = new File(p.getReference());
			if( !refFile.exists() || refFile.isDirectory() ) {
				LOG.trace("MatchingTaskPreset reference file not found.");
				return false;
			}
		}
		
		return true;
	}
}
