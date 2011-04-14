package am.userInterface;

import java.io.File;
import java.util.prefs.Preferences;

import am.GlobalStaticVariables;
import am.app.mappingEngine.MatcherSetting;

/** 
 * @author cosmin
 * 
 * This class is responsible for storing and restoring Application specific preferences.
 * 
 * Preferences that are saved:
 * 		1. The open file dialog selections and the last directory browsed to.
 * 		2. The last 10 files used as Sources and Targets (used for the File -> Recent .. submenus)
 * 
 * 
 */
public class AppPreferences {

	
	/** The app prefs. */
	private Preferences appPrefs;
	
	// TODO: Convert all these static Strings to an enum.
	
	/** preferences key for storing the last directory used by the dialog */
	private static final String PREF_LASTDIR = "pref_lastdirectoryused";
	
	/** what syntax and language were last used for the files. */
	private static final String PREF_LASTSYNT = "pref_lastsyntaxused";
	private static final String PREF_LASTLANG = "pref_lastlanguageused";
	private static final String PREF_LAST_SKIP_NAMESPACE = "pref_skipnamespace";
	private static final String PREF_LAST_NO_REASONER = "pref_lastnoreasoner";
	private static final String PREF_LAST_ONDISK = "pref_lastonDISK";
	
	/** the prefix for the 10 entries for the recent source file */
	private static final String PREF_RECENTSOURCE = "recentsource_";
	private static final String PREF_RECENTTARGET = "recenttarget_";
	
	private static final String PREF_RECENT_FILENAME = "_filename_";
	private static final String PREF_RECENT_SYNTAX = "_syntax_";
	private static final String PREF_RECENT_LANGUAGE = "_language_";
	private static final String PREF_RECENT_SKIP = "_skip_";
	private static final String PREF_RECENT_REASONER = "_reasoner_";
	private static final String PREF_RECENT_ONDISK = "_ondisk_";
	private static final String PREF_RECENT_ONDISKDIRECTORY = "_ondiskdirectory_";
	private static final String PREF_RECENT_ONDISKPERSISTENT = "_ondiskpersistent_";
	
	/** key for storing the last directory used to open the reference file in the Evaluate Reference function */
	private static final String 	PREF_LASTDIRREFERENCE = "pref_lastdirreference";													 
	/** format of the last reference file opened in the Evaluate Reference function */
	private static final String PREF_LASTFORMATREFERENCE = "pref_lastformatreference";
	/** key for storing the last directory used to save the output of the evaluation with the reference file in the Evaluate Reference function */
	private static final String 	PREF_LASTDIROUTPUT = "pref_lastdiroutput";		
	/** key for storing the last name of the file used to save the output of the evaluation with the reference file in the Evaluate Reference function */
	private static final String 	PREF_LASTNAMEOUTPUT = "pref_lastnameoutput";		
	
	/** key for storying the VisualizationDisabledMode = When true hierarchies and mappings are not displayed in the canvas */
	private static final String		PREF_DISABLEVISUALIZATION = "PREF_DISABLEVISUALIZATION";
	private static final String		PREF_SELECTEDMATCHINGSONLY = "pref_selectedmatchingsonly";
	private static final String		PREF_SHOWLOCALNAME = "pref_SHOWLOCALNAME";
	private static final String		PREF_SHOWLABEL = "pref_SHOWLABEL";
	private static final String		PREF_SHOWMAPPINGSWITHNAME = "PREF_SHOWMAPPINGSWITHNAME";
	
	/** key for storing the status of the beep on finish checkbox of the matcher progress panel */
	private static final String 	PREF_BEEPONFINISH = "pref_BEEPONFINISH";

	/** keys for storing preferences for the export dialog */
	private static final String		PREF_EXPORT_TYPE = "pref_export_type";
	private static final String		PREF_EXPORT_ALIGNMENT_FORMAT = "pref_export_alignment_format";
	private static final String 	PREF_EXPORTLASTDIR = "pref_export_last_dir_output";
	private static final String 	PREF_EXPORTLASTFILENAME = "pref_export_last_filename";
	private static final String 	PREF_EXPORT_CLASSESMATRIX = "pref_export_classes_matrix";
	private static final String		PREF_EXPORT_SORT = "pref_export_sort";
	private static final String		PREF_EXPORT_ISOLINES = "pref_export_isolines";
	private static final String		PREF_EXPORT_SKIPZERO = "pref_export_skipzero";
	
	private static final String		PREF_IMPORTLASTFILENAME = "pref_import_lastfilename";
	private static final String		PREF_IMPORTLASTDIRECTORY = "pref_import_lastdirectory";
	private static final String		PREF_IMPORT_TYPE = "pref_import_type";
	private static final String		PREF_IMPORT_ALIGNMENT_FORMAT = "pref_export_alignment_format";
	
	public static enum FileType { 
		ALIGNMENT_ONLY("1"), MATRIX_AS_CSV("2"), COMPLETE_MATCHER("3");
		//----------------------- Implementation Details ------------------
		String key;
		FileType(String k) { key = k; }
		String getKey() { return key; }
	}
	
	/**
	 * Constructor
	 */
	public AppPreferences() {
		appPrefs = Preferences.userRoot().node("/com/advis/agreementMaker");
		cleanUpFileEntries();
	}
	
	
	public void saveBeepOnFinish( boolean beep ) {
		if( beep ) appPrefs.put(PREF_BEEPONFINISH, "y");
		else appPrefs.put(PREF_BEEPONFINISH, "n");
	}
	
	public boolean getBeepOnFinish() {
		String beep = appPrefs.get(PREF_BEEPONFINISH, "n");
		if( beep.equals("y") ) { return true; }
		return false; 
	}
	
	
	/** 
	 *
	 * @return the last directory selected by the user in the file chooser
	 */
	public File getLastFile() {
		
		File lastdir = new File( appPrefs.get(PREF_LASTDIR, "."));
		return lastdir;
	}
	
	/**
	 * 
	 * @return How many entries of recent source files we have.
	 */
	public int countRecentSources() {
		
		// we start from the bottom of the preferences, and go until we find a nonempty entry
		// that tells us how many entries hold information
		for( int i = 9; i >= 0; i--) {
			if( !appPrefs.get( PREF_RECENTSOURCE + "name" + i, "").equals("") ) { // not equals
				// found a non empty entry
				return i + 1; // return the count, which is the current index + 1 
			}
		}
		
		// all the entries are empty
		return 0;	
		
	}
	
	/** 
	 * Note: this function is identical to countRecentSources(), except that it uses a PREF_RECENTTARGET
	 * The two functions could be merged, but it's not a big deal.
	 * @return How many entries of recent target files we have.
	 */
	public int countRecentTargets() {
		
		// we start from the bottom of the preferences, and go until we find a nonempty entry
		// that tells us how many entries hold information
		for( int i = 9; i >= 0; i--) {
			if( !appPrefs.get( PREF_RECENTTARGET + "name" + i, "").equals("") ) { // not equals
				// found a non empty entry
				return i + 1; // return the count, which is the current index + 1 
			}
		}
		
		// all the entries are empty
		return 0;	
		
	}
	
	/**
	 * 
	 * @return The last thing selected in the syntax list.
	 */
	public int getSyntaxListSelection() {
		int lastsynt = appPrefs.getInt(PREF_LASTSYNT, 0);
		if( lastsynt < 0 ) return 0;
		
		return lastsynt;
	}
	
	/**
	 * 
	 * @return The last thing selected in the language list.
	 */
	public int getLanguageListSelection() {
		int lastlang = appPrefs.getInt(PREF_LASTLANG, 1);
		if( lastlang < 0 ) return 0;
		
		return lastlang;
	}
	
	/**
	 * This will save the location of the directory that was used by the user
	 * for use next time the user opens the dialog
	 * @param selectedfile - the file that was selected by the user
	 */
	public void saveLastDir(File selectedfile) {
		appPrefs.put(PREF_LASTDIR, selectedfile.getPath());
	}
	
	/**
	 * Save the selections from the two listboxes on the Open dialog
	 * 
	 * TODO: FIND A BETTER WAY TO PASS ARGUMENTS
	 * 
	 * @param syntaxlist index of selection from the syntax listbox
	 * @param languagelist index of selection from the language listbox
	 */
	public void saveOpenDialogListSelection( int syntaxlist, 
											int languagelist, 
											boolean skip, 
											boolean noReasoner, 
											boolean onDisk, 
											String onDiskDirectory,
											boolean onDiskPersistent) {
		appPrefs.putInt(PREF_LASTSYNT, syntaxlist);
		appPrefs.putInt(PREF_LASTLANG, languagelist);
		appPrefs.putBoolean(PREF_LAST_SKIP_NAMESPACE, skip);
		appPrefs.putBoolean(PREF_LAST_NO_REASONER, noReasoner);
		appPrefs.putBoolean(PREF_LAST_ONDISK, onDisk);
		// don't need to save onDiskDirectory or onDiskPersistent (those are saved in the OnDiskLocationDialog)
	}
	
	/**
	 * Save the recent files used (for use in the File menu)
	 * @param selectedfile the file selected by the user
	 * @param onthologyType what type of ontology (source or target ontology)
	 */
	public void saveRecentFile( String filename, 
								int ontoType, 
								int syntax, 
								int language, 
								boolean skip, 
								boolean noReasoner, 
								boolean onDisk, 
								String tdbDir, 
								boolean persistent ) {		
		if(ontoType == GlobalStaticVariables.SOURCENODE) 
			saveRecentFile(	PREF_RECENTSOURCE, 
							filename, 
							syntax, 
							language,
							skip, 
							noReasoner, 
							onDisk, 
							tdbDir, 
							persistent);
		else if(ontoType == GlobalStaticVariables.TARGETNODE) 
			saveRecentFile( PREF_RECENTTARGET, 
							filename, 
							syntax, 
							language, 
							skip, 
							noReasoner, 
							onDisk, 
							tdbDir, 
							persistent);
	}
	
	
	/**
	 * Save the recent file names to the Preferences API using keyNames for the name of the entries
	 * @param keyValues These are the names of the keys under which the file names are saved
	 * @param selectedfile
	 */
	private void saveRecentFile( String keyPrefix,  String filename, int syntax, int language, boolean skip, boolean noReasoner, boolean onDisk, String tdbDir, boolean persistent ) {
		
		// we have 10 entries available for saving recent files.
		// the file that is passed to us, automatically put it at the top of the list.
		// then, iterate down the list pushing all the other lists one step down the list
		// while iterating, if we find that the current file is equal to that entry on the list, we are done
		// iterate until the end of the the entries.  the last item gets pushed off. we are done

		
		// each file has a name but also, the syntax and language associated with its format
		// we need to keep track of these parameters, so the user doesn't have to specify them again
		String previousName = "";
		int previousSyntax = 0;
		int previousLanguage = 0;
		boolean previousSkip = false;
		boolean previousNoReasoner = false;
		boolean previousOnDisk = false;
		String previousDirectory = "";
		boolean previousPersistent = false;
		
		String currentName;
		int currentSyntax;
		int currentLanguage;
		boolean currentSkip;
		boolean currentNoReasoner;
		boolean currentOnDisk;
		String currentOnDiskDirectory;
		boolean currentOnDiskPersistent;
		
		for( int i = 0; i <= 9; i++) {
						
			if( i == 0 ) {
				// we are at the top of the list
				// we need to put the current entry on the top of the list.
				
				previousName = getRecentFileName( keyPrefix, i ); // save current top entry
				previousSyntax = getRecentFileSyntax( keyPrefix, i);
				previousLanguage = getRecentFileLanguage( keyPrefix, i);
				previousSkip = getRecentSkipNamespace(keyPrefix, i);
				previousNoReasoner = getRecentNoReasoner( keyPrefix, i);
				previousOnDisk = getRecentOnDisk( keyPrefix, i);
				previousDirectory = getRecentOnDiskDirectory( keyPrefix, i);
				previousPersistent = getRecentOnDiskPersistent( keyPrefix, i);
				
				if( previousName.equals(filename) &&
					previousSyntax == syntax &&
					previousLanguage == language &&
					previousSkip == skip &&
					previousNoReasoner == noReasoner &&
					previousOnDisk == onDisk && 
					previousPersistent == persistent &&
					previousDirectory.equals(tdbDir) ) { // when comparing strings, use the equals() function
					// the current file is already at the top of the list
					// no need to do anything
					saveFileEntry( keyPrefix, i, filename, syntax, language, skip, noReasoner, onDisk, tdbDir, persistent);
					break;
				}
				
				saveFileEntry( keyPrefix, i, filename, syntax, language, skip, noReasoner, onDisk, tdbDir, persistent);
				
				continue; // we are done changing the top entry, go on to the next
				
			}
			
			// we have placed the current entry on the top, now we are moving entries down.
			currentName = getRecentFileName( keyPrefix, i ); // save current top entry
			currentSyntax = getRecentFileSyntax( keyPrefix, i);
			currentLanguage = getRecentFileLanguage( keyPrefix, i);
			currentSkip = getRecentSkipNamespace(keyPrefix, i);
			currentNoReasoner = getRecentNoReasoner(keyPrefix, i);
			currentOnDisk = getRecentOnDisk(keyPrefix, i);
			currentOnDiskDirectory = getRecentOnDiskDirectory(keyPrefix, i);
			currentOnDiskPersistent = getRecentOnDiskPersistent(keyPrefix, i);
			
			if ( currentName.equals("") ) {
				// we have reached an empty entry, so no need to go any further
				// save the entry we are bumping down to this empty slot
				saveFileEntry( keyPrefix, i, previousName, previousSyntax, previousLanguage, previousSkip, previousNoReasoner, previousOnDisk, previousDirectory, previousPersistent);
				break;
			}
			
			if( currentName.equals(filename) &&
				currentSyntax == syntax &&
				currentLanguage == language &&
				currentSkip == skip &&
				currentNoReasoner == noReasoner &&
				currentOnDisk == onDisk && 
				currentOnDiskPersistent == persistent &&
				currentOnDiskDirectory.equals(tdbDir) ) {
				// the file that we put on the top is found at this location
				// that means we bumped it to the top from this location
				// just replace this entry with the file we are bumping down, and that's it
				saveFileEntry( keyPrefix, i, previousName, previousSyntax, previousLanguage, previousSkip, previousNoReasoner, previousOnDisk, previousDirectory, previousPersistent);
				break;	
			}
			
			// if we get here, we are still bumping down entries
			// so let's bump
			
			saveFileEntry( keyPrefix, i, previousName, previousSyntax, previousLanguage, previousSkip, previousNoReasoner, previousOnDisk, previousDirectory, previousPersistent);
			
			previousName = currentName;
			previousSyntax = currentSyntax;
			previousLanguage = currentLanguage;
			previousSkip = currentSkip;
			previousNoReasoner = currentNoReasoner;
			previousOnDisk = currentOnDisk;
			previousDirectory = currentOnDiskDirectory;
			previousPersistent = currentOnDiskPersistent;
					
			
		} // for()
		
		// if we get here, we have iterated through the whole list, and ended up
		// bumping off an entry.  done.
		
	}
	
	/**
	 * This function will save the file name, syntax of the file, and language of the file
	 * into a recent spot
	 * @param key
	 * @param position
	 * @param filename
	 * @param syntax
	 * @param language
	 * @param noReasoner
	 */
	private void saveFileEntry( String prefix, int position, String filename, int syntax, int language, boolean skip, boolean noReasoner, boolean onDisk, String tdbDir, boolean persistent) {
		appPrefs.put( prefix + PREF_RECENT_FILENAME + position, filename); 
		appPrefs.putInt( prefix + PREF_RECENT_SYNTAX + position , syntax); 
		appPrefs.putInt( prefix + PREF_RECENT_LANGUAGE + position, language); 
		appPrefs.putBoolean( prefix + PREF_RECENT_SKIP + position, skip);
		appPrefs.putBoolean( prefix + PREF_RECENT_REASONER + position, noReasoner);
		appPrefs.putBoolean(prefix + PREF_RECENT_ONDISK + position, onDisk);
		appPrefs.put(prefix + PREF_RECENT_ONDISKDIRECTORY + position, tdbDir);
		appPrefs.putBoolean(prefix + PREF_RECENT_ONDISKPERSISTENT + position, persistent);
	}


	/**
	 * This function will clean up the recent file entries
	 * If the files on the list no longer exist, it will remove them from the list,
	 * and rearrange the list to the entries stay at the top
	 */
	private void cleanUpFileEntries() {
		cleanUpFileEntries( PREF_RECENTSOURCE );  // clean up the recent source files
		cleanUpFileEntries( PREF_RECENTTARGET ); // clean up the recent target files
	}
	
	private void cleanUpFileEntries( String prefix) {
		
		
		// start at the top entry and go to the bottom
		// if any entry is removed, move entries up
		
		int j = 0; // this will keep track of where to put entries when moving up
		
		
		for( int i = 0; i <= 9; i++) {
			
			String filename = getRecentFileName(prefix, i); // get the filename of the current location
			int syntax = getRecentFileSyntax(prefix, i); // get the syntax of the current file
			int language = getRecentFileLanguage(prefix, i); // get the language of the current file
			boolean skip = getRecentSkipNamespace(prefix, i); // get the language of the current file
			boolean noReasoner = getRecentNoReasoner(prefix, i); // get whether we want to use a reasoner or not
			boolean onDisk = getRecentOnDisk(prefix, i); // get whether we are loading to the disk instead of memory
			String diskDirectory = getRecentOnDiskDirectory(prefix, i); // get the directory where we're saving the ontology
			boolean diskPersistent = getRecentOnDiskPersistent(prefix, i); // get whether we are loading to the disk instead of memory
			
			File testfile = new File(filename);
			
			if( !testfile.exists() ) {
				// the file does not exist, so clear this entry
				saveFileEntry(prefix, i, "", 0, 0, false, false, false, "", false);
			} 
			else if( j < i ) {
				// the file exists
				
				// j is less than i, which means we have removed a file previously
				// move the current entry up to where j is pointing
				saveFileEntry(prefix, j, filename, syntax, language, skip, noReasoner, onDisk, diskDirectory, diskPersistent);
				saveFileEntry(prefix, i, "", 0, 0, false, false, false, "", false); // clear the entry we moved up
				
				j++;
				
			}
			else {
				
				j++;
			}
		}
		
		// clear any entries that are beyond j
		for( ; j <= 9; j++) {
			saveFileEntry(prefix, j, "", 0, 0, false, false, false, "", false);
		}
		
	}
	
	/*
	 * Functions used by the ReferenceFileDialog
	 * 
	 */
	/** 
	 *
	 * @return the last directory selected by the user in the file chooser in ReferenceFileDialog task
	 */
	public File getLastDirReference() {
		
		File lastdirref = new File( appPrefs.get(PREF_LASTDIRREFERENCE, ""));
		return lastdirref;
	}
	
	/**
	 * This will save the location of the directory that was used by the user to open a reference file in Evaluate Reference function
	 * for use next time the user opens the dialog
	 * @param selectedfile - the file that was selected by the user
	 */
	public void saveLastDirReference(File selectedfile) {
		appPrefs.put(PREF_LASTDIRREFERENCE, selectedfile.getPath());
	}
	
	/** 
	 *
	 * @return the format of the last reference file opened by the user in the file chooser in ReferenceFileDialog task
	 */
	public int getFileFormatReference() {
		return appPrefs.getInt(PREF_LASTFORMATREFERENCE, 0);
	}
	
	/**
	 * 
	 * This will save the index of the format of the file that was used by the user to open a reference file in Evaluate Reference function
	 * for use next time the user opens the dialog
	 * @param selectedfile - the file that was selected by the user
	 */
	public void saveLastFormatReference(int indexformat) {
		appPrefs.put(PREF_LASTFORMATREFERENCE, indexformat+"");
	}
	
	/** 
	 *
	 * @return the last directory selected by the user in the file chooser in ReferenceFileDialog task
	 */
	public File getLastDirOutput() {
		File lastdirrefoutput = new File( appPrefs.get(PREF_LASTDIROUTPUT, ""));
		return lastdirrefoutput;
		
	}
	
	
	/** Export Dialog **/
	public boolean getExportSort() {	return appPrefs.getBoolean(PREF_EXPORT_SORT, true); }
	public void saveExportSort( boolean sort ) {	appPrefs.putBoolean(PREF_EXPORT_SORT, sort); }
	public boolean getExportIsolines() {	return appPrefs.getBoolean(PREF_EXPORT_ISOLINES, true); }
	public void saveExportIsolines( boolean sort ) {	appPrefs.putBoolean(PREF_EXPORT_ISOLINES, sort); }
	public boolean getExportSkipZeros() {	return appPrefs.getBoolean(PREF_EXPORT_SKIPZERO, true); }
	public void saveExportSkipZeros( boolean sort ) {	appPrefs.putBoolean(PREF_EXPORT_SKIPZERO, sort); }
	public void saveExportClassesMatrix( boolean c ) { appPrefs.putBoolean(PREF_EXPORT_CLASSESMATRIX, c); }
	public boolean isExportClassesMatrix() { return appPrefs.getBoolean(PREF_EXPORT_CLASSESMATRIX, true); }
	public void saveExportType( FileType t ) { appPrefs.put(PREF_EXPORT_TYPE, t.getKey() ); }
	public void saveImportType( FileType t ) { appPrefs.put(PREF_IMPORT_TYPE, t.getKey() ); }
	
	public boolean isExportTypeSelected( FileType t ) {
		String type = appPrefs.get(PREF_EXPORT_TYPE, "1");
		if( type.equals(t.getKey()) ) return true;
		return false;
	}
	
	public void saveExportAlignmentFormatIndex( int index ) { appPrefs.putInt(PREF_EXPORT_ALIGNMENT_FORMAT, index ); }
	public int getExportAlignmentFormatIndex() { return appPrefs.getInt(PREF_EXPORT_ALIGNMENT_FORMAT, 0); }
	
	public void saveImportAlignmentFormatIndex( int index ) { appPrefs.putInt(PREF_IMPORT_ALIGNMENT_FORMAT, index ); }
	public int getImportAlignmentFormatIndex() { return appPrefs.getInt(PREF_IMPORT_ALIGNMENT_FORMAT, 0); }
	
	/**
	 * @return The last directory selected by the user in the Export Dialog.
	 */
	public String getImportLastFilename() { return appPrefs.get(PREF_IMPORTLASTFILENAME, ""); }
	public String getImportLastDirectory() { return appPrefs.get(PREF_IMPORTLASTDIRECTORY, ""); }
	public void saveImportLastFilename( String name ) { appPrefs.put( PREF_IMPORTLASTFILENAME, name); }
	public void saveImportLastDirectory( String name ) { appPrefs.put( PREF_IMPORTLASTDIRECTORY, name); }
	public String getExportLastFilename() { return appPrefs.get(PREF_EXPORTLASTFILENAME,""); }
	public File getExportLastDir() { return new File( appPrefs.get(PREF_EXPORTLASTDIR, "") ); }
	public void saveExportLastFilename( String name ) { appPrefs.put( PREF_EXPORTLASTFILENAME, name); }
	public void saveExportLastDir(File selecteddir) { appPrefs.put(PREF_EXPORTLASTDIR, selecteddir.getPath()); }
	public void saveExportLastDir(String selecteddir) { appPrefs.put(PREF_EXPORTLASTDIR, selecteddir); }
	
	/**
	 * This will save the location of the directory that was used by the user to save the output of the Evaluate Reference function
	 * for use next time the user opens the dialog
	 * @param selectedfile - the file that was selected by the user
	 */
	public void saveLastDirOutput(File selecteddir) {
		appPrefs.put(PREF_LASTDIROUTPUT, selecteddir.getPath());
	}
	
	/** 
	 *
	 * @return the last directory selected by the user in the file chooser in ReferenceFileDialog task
	 */
	public String getLastNameOutput() {
		String name = appPrefs.get(PREF_LASTNAMEOUTPUT,"");
		return name;
		
	}
	
	/**
	 * This will save the name of the output file of the Evaluate Reference function
	 * for use next time the user opens the dialog
	 * @param selectedfile - the file that was selected by the user
	 */
	public void saveLastNameOutput(String name) { appPrefs.put(PREF_LASTNAMEOUTPUT, name); }
	
	
	//THE LAST FORMAT FOR THE OUTPUT FILE OF THE REFERENCE EVALUATION HAS NOT BEEN IMPLEMENTED YET BECAUSE THERE IS ONLY ONE FORMAT NOW
	
	/**
	 * The last value for the skip namespace value in the loading ontology function
	 */
	public boolean getLastSkipNamespace() { return appPrefs.getBoolean(PREF_LAST_SKIP_NAMESPACE, false); }
	public boolean getLastNoReasoner() {return appPrefs.getBoolean(PREF_LAST_NO_REASONER, false); }
	
	public String getRecentFileName( String prefix, int position) { return appPrefs.get( prefix + PREF_RECENT_FILENAME + position, ""); }
	public int getRecentFileSyntax( String prefix, int position) { return appPrefs.getInt(prefix + PREF_RECENT_SYNTAX + position, 0); }
	public int getRecentFileLanguage( String prefix, int position) { return appPrefs.getInt(prefix + PREF_RECENT_LANGUAGE + position, 0); }
	public boolean getRecentSkipNamespace( String prefix, int position) { return appPrefs.getBoolean(prefix + PREF_RECENT_SKIP + position, false); }
	public boolean getRecentNoReasoner( String prefix, int position ) {	return appPrefs.getBoolean(prefix + PREF_RECENT_REASONER + position, false); }	
	public boolean getRecentOnDisk( String prefix, int position ) { return appPrefs.getBoolean(prefix + PREF_RECENT_ONDISK + position, false); }
	public String getRecentOnDiskDirectory( String prefix, int position ) {	return appPrefs.get(prefix + PREF_RECENT_ONDISKDIRECTORY + position, ""); }
	public boolean getRecentOnDiskPersistent( String prefix, int position ) { return appPrefs.getBoolean(prefix + PREF_RECENT_ONDISKPERSISTENT + position, false); }
	
	@Deprecated public String getRecentSourceFileName( int position ) { return getRecentFileName(PREF_RECENTSOURCE,position); }
	@Deprecated public int getRecentSourceSyntax( int position) { return getRecentFileSyntax(PREF_RECENTSOURCE,position); }
	@Deprecated public int getRecentSourceLanguage( int position) {	return getRecentFileLanguage(PREF_RECENTSOURCE,position); }
	@Deprecated public boolean getRecentSourceSkipNamespace( int position) { return getRecentSkipNamespace(PREF_RECENTSOURCE, position); }
	@Deprecated public boolean getRecentSourceNoReasoner( int position) { return getRecentNoReasoner(PREF_RECENTSOURCE, position); }
	@Deprecated public boolean getRecentSourceOnDisk( int position) { return getRecentOnDisk(PREF_RECENTSOURCE, position); }
	@Deprecated public String getRecentSourceOnDiskDirectory( int position) { return getRecentOnDiskDirectory(PREF_RECENTSOURCE, position); }
	@Deprecated public boolean getRecentSourceOnDiskPersistent( int position) {	return getRecentOnDiskPersistent(PREF_RECENTSOURCE, position); }
	
	@Deprecated public String getRecentTargetFileName( int position ) {	return getRecentFileName(PREF_RECENTTARGET,position);	}
	@Deprecated public int getRecentTargetSyntax( int position) { return getRecentFileSyntax(PREF_RECENTTARGET,position); }
	@Deprecated public int getRecentTargetLanguage( int position) {	return getRecentFileLanguage(PREF_RECENTTARGET,position); }
	@Deprecated public boolean getRecentTargetSkipNamespace( int position) { return getRecentSkipNamespace(PREF_RECENTTARGET, position); }
	@Deprecated public boolean getRecentTargetNoReasoner( int position) { return getRecentNoReasoner(PREF_RECENTTARGET, position); }
	@Deprecated public boolean getRecentTargetOnDisk( int position) { return getRecentOnDisk(PREF_RECENTTARGET, position); }
	@Deprecated public String getRecentTargetOnDiskDirectory( int position) { return getRecentOnDiskDirectory(PREF_RECENTTARGET, position);	}
	@Deprecated public boolean getRecentTargetOnDiskPersistent( int position) {	return getRecentOnDiskPersistent(PREF_RECENTTARGET, position); }

	/******************************* UIMenu() **********************************************/
	
	public boolean getDisableVisualization() { return appPrefs.getBoolean(PREF_DISABLEVISUALIZATION, false); }
	public void saveDisableVisualization(boolean value) { appPrefs.putBoolean(PREF_DISABLEVISUALIZATION, value); }
	
	@Deprecated public boolean getSelectedMatchingsOnly() {	return appPrefs.getBoolean(PREF_SELECTEDMATCHINGSONLY, false); }  // View -> Selected Matchings Only (DEPRECATED in favor of doubleclicking)
	@Deprecated public void saveSelectedMatchingsOnly(boolean value) { appPrefs.putBoolean(PREF_SELECTEDMATCHINGSONLY, value); }
	
	public boolean getShowLocalname() { return appPrefs.getBoolean(PREF_SHOWLOCALNAME, true); }  // View -> Show localname
	public void saveShowLocalname(boolean value) { appPrefs.putBoolean(PREF_SHOWLOCALNAME, value); }	
	
	public boolean getShowLabel() {	return appPrefs.getBoolean(PREF_SHOWLABEL, true); }  // View -> Show label
	public void saveShowLabel(boolean value) { appPrefs.putBoolean(PREF_SHOWLABEL, value); }

	public boolean getShowMappingsShortname() {	return appPrefs.getBoolean(PREF_SHOWMAPPINGSWITHNAME, true); }  // View -> Mappings with Matcher name
	public void saveShowMappingsShortname(boolean value) { appPrefs.putBoolean(PREF_SHOWMAPPINGSWITHNAME, value); }	
	
	
	/**
	 * Support for various settings saved by the Matcher Panels
	 * Used in conjuction with the MatcherSetting enum.
	 */
	public void savePanelFloat( MatcherSetting setting, float value ) {
		appPrefs.putFloat( "PREF_" + setting.prefKey , value );		
	}
	public void savePanelBool( MatcherSetting setting, boolean value ) {
		appPrefs.putBoolean( "PREF_" + setting.prefKey , value );
	}
	public float getPanelFloat( MatcherSetting setting ) {
		return appPrefs.getFloat( "PREF_" + setting.prefKey, setting.defFloat );
		
	}
	public boolean getPanelBool( MatcherSetting setting ) {
		return appPrefs.getBoolean( "PREF_" + setting.prefKey, setting.defBool );
		
	}
	
	/**
	 * Used to return saved preferences (this method is for boolean values).
	 * This method is meant to be used by parameters panels of all matchers.
	 * 
	 * @param key The key string for the setting.  Use the same key to save the value.
	 * @param def The default value to be returned in case the preferences cannot be read.
	 * @return The saved setting.
	 * @see savePanelBool
	 */
	public boolean getPanelBool( String key, boolean def ) {
		return appPrefs.getBoolean( "PREF_MATCHERPANEL_" + key , def );
	}
	
	/**
	 * Used to save parameters panel preferences (this method is for boolean values).
	 * 
	 * @param key The key string for the setting.  Use the same key to retrieve a saved value.
	 * @param def The default value to be returned in case the preferences cannot be read.
	 * @return The saved setting.
	 * @see getPanelBool
	 */
	public void savePanelBool( String key, boolean val ) {
		appPrefs.putBoolean( "PREF_MATCHERPANEL_" + key , val );
	}
	



	public void saveInt( String key, int val ) { appPrefs.putInt("PREF_GENERIC_INT_" + key, val); }
	public int getInt( String key ) { return appPrefs.getInt("PREF_GENERIC_INT_" + key, 0); }

	
}
