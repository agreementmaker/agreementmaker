package am.userInterface;

import java.io.File;
import java.util.prefs.Preferences;

import am.GlobalStaticVariables;
import am.application.mappingEngine.MatcherSetting;

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
	
	/** preferences key for storing the last directory used by the dialog */
	private static final String PREF_LASTDIR = "pref_lastdirectoryused";
	
	/** what syntax and language were last used for the files. */
	private static final String PREF_LASTSYNT = "pref_lastsyntaxused";
	private static final String PREF_LASTLANG = "pref_lastlanguageused";
	private static final String PREF_LAST_SKIP_NAMESPACE = "pref_skipnamespace";
	private static final String PREF_LAST_NO_REASONER = "pref_lastnoreasoner";
	
	/** the prefix for the 10 entries for the recent source file */
	private static final String PREF_RECENTSOURCE = "recentsource_";
	private static final String PREF_RECENTTARGET = "recenttarget_";
	
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
	
	/** key for storing if the user is viewing the canvas in "selected matchings only" mode. */
	private static final String		PREF_SELECTEDMATCHINGSONLY = "pref_selectedmatchingsonly";
	
	/** key for storing if the user is viewing labels and or localnames in the canvas. */
	private static final String		PREF_SHOWLOCALNAME = "pref_SHOWLOCALNAME";
	private static final String		PREF_SHOWLABEL = "pref_SHOWLABEL";
	

	
	/**
	 * Constructor
	 */
	public AppPreferences() {
		appPrefs = Preferences.userRoot().node("/com/advis/agreementMaker");
		cleanUpFileEntries();
	}
	
	
	
	/** 
	 *
	 * @return the last directory selected by the user in the file chooser
	 */
	public File getLastDir() {
		
		File lastdir = new File( appPrefs.get(PREF_LASTDIR, "~"));
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
	
	public String getRecentSourceFileName( int position ) {
		return appPrefs.get(PREF_RECENTSOURCE + "name" + position, "");
	}
	
	public String getRecentTargetFileName( int position ) {
		return appPrefs.get(PREF_RECENTTARGET + "name" + position, "");
	}
	
	public int getRecentSourceSyntax( int position) {
		return appPrefs.getInt(PREF_RECENTSOURCE + "syntax" + position, 0);
	}
	
	public int getRecentTargetSyntax( int position) {
		return appPrefs.getInt(PREF_RECENTTARGET + "syntax" + position, 0);
	}
	
	public int getRecentSourceLanguage( int position) {
		return appPrefs.getInt(PREF_RECENTSOURCE + "language" + position, 0);
	}
	
	public int getRecentTargetLanguage( int position) {
		return appPrefs.getInt(PREF_RECENTTARGET + "language" + position, 0);
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
	 * @param syntaxlist index of selection from the syntax listbox
	 * @param languagelist index of selection from the language listbox
	 */
	public void saveOpenDialogListSelection( int syntaxlist, int languagelist, boolean skip, boolean noReasoner) {
		appPrefs.putInt(PREF_LASTSYNT, syntaxlist);
		appPrefs.putInt(PREF_LASTLANG, languagelist);
		appPrefs.putBoolean(PREF_LAST_SKIP_NAMESPACE, skip);
		appPrefs.putBoolean(PREF_LAST_NO_REASONER, noReasoner);
	}
	
	/**
	 * Save the recent files used (for use in the File menu)
	 * @param selectedfile the file selected by the user
	 * @param onthologyType what type of ontology (source or target ontology)
	 */
	public void saveRecentFile( String filename, int ontoType, int syntax, int language, boolean skip, boolean noReasoner ) {		
		if(ontoType == GlobalStaticVariables.SOURCENODE) saveRecentFile(PREF_RECENTSOURCE, filename, syntax, language,skip, noReasoner);
		else if(ontoType == GlobalStaticVariables.TARGETNODE) saveRecentFile(PREF_RECENTTARGET, filename, syntax, language, skip, noReasoner);
	}
	
	
	/**
	 * Save the recent file names to the Preferences API using keyNames for the name of the entries
	 * @param keyValues These are the names of the keys under which the file names are saved
	 * @param selectedfile
	 */
	private void saveRecentFile( String keyPrefix,  String filename, int syntax, int language, boolean skip, boolean noReasoner) {
		
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
		
		String currentName;
		int currentSyntax;
		int currentLanguage;
		boolean currentSkip;
		
		for( int i = 0; i <= 9; i++) {
						
			if( i == 0 ) {
				// we are at the top of the list
				// we need to put the current entry on the top of the list.
				
				previousName = getFileName( keyPrefix, i ); // save current top entry
				previousSyntax = getFileSyntax( keyPrefix, i);
				previousLanguage = getFileLanguage( keyPrefix, i);
				previousSkip = getSkipNamespace(keyPrefix, i);
				previousNoReasoner = getNoReasoner( keyPrefix, i);
				
				if( previousName.equals(filename) ) { // when comparing strings, use the equals() function
					// the current file is already at the top of the list
					// no need to do anything
					saveFileEntry( keyPrefix, i, filename, syntax, language, skip, noReasoner);
					break;
				}
				
				saveFileEntry( keyPrefix, i, filename, syntax, language, skip, noReasoner);
				
				continue; // we are done changing the top entry, go on to the next
				
			}
			
			// we have placed the current entry on the top, now we are moving entries down.
			currentName = getFileName( keyPrefix, i ); // save current top entry
			currentSyntax = getFileSyntax( keyPrefix, i);
			currentLanguage = getFileLanguage( keyPrefix, i);
			currentSkip = getSkipNamespace(keyPrefix, i);
			
			if ( currentName.equals("") ) {
				// we have reached an empty entry, so no need to go any further
				// save the entry we are bumping down to this empty slot
				saveFileEntry( keyPrefix, i, previousName, previousSyntax, previousLanguage, previousSkip, previousNoReasoner);
				break;
			}
			
			if( currentName.equals(filename) ) {
				// the file that we put on the top is found at this location
				// that means we bumped it to the top from this location
				// just replace this entry with the file we are bumping down, and that's it
				saveFileEntry( keyPrefix, i, previousName, previousSyntax, previousLanguage, previousSkip, previousNoReasoner);
				break;	
			}
			
			// if we get here, we are still bumping down entries
			// so let's bump
			
			saveFileEntry( keyPrefix, i, previousName, previousSyntax, previousLanguage, previousSkip, previousNoReasoner);
			
			previousName = currentName;
			previousSyntax = currentSyntax;
			previousLanguage = currentLanguage;
			previousSkip = currentSkip;
					
			
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
	private void saveFileEntry( String prefix, int position, String filename, int syntax, int language, boolean skip, boolean noReasoner) {
		appPrefs.put( prefix + "name" + position, filename); 
		appPrefs.putInt( prefix + "syntax" + position , syntax); 
		appPrefs.putInt( prefix + "language" + position, language); 
		appPrefs.putBoolean( prefix + "skip" + position, skip);
		appPrefs.putBoolean( prefix + "noreasoner" + position, noReasoner);
	}
	
	/**
	 * Internal function for returning a file name depening on the prefix and the position
	 * @param prefix either "source" or "target"
	 * @param position 10 positions, from 0 - 9
	 * @return
	 */
	private String getFileName( String prefix, int position) {
		return appPrefs.get( prefix + "name" + position, "");
	}
	
	private int getFileSyntax( String prefix, int position) {
		return appPrefs.getInt(prefix +"syntax" + position, 0);
	}
	
	private int getFileLanguage( String prefix, int position) {
		return appPrefs.getInt(prefix + "language" + position, 0);
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
			
			String filename = getFileName(prefix, i); // get the filename of the current location
			int syntax = getFileSyntax(prefix, i); // get the syntax of the current file
			int language = getFileLanguage(prefix, i); // get the language of the current file
			boolean skip = getSkipNamespace(prefix, i); // get the language of the current file
			boolean noReasoner = getNoReasoner(prefix, i); // get whether we want to use a reasoner or not
			File testfile = new File(filename);
			
			if( !testfile.exists() ) {
				// the file does not exist, so clear this entry
				saveFileEntry(prefix, i, "", 0, 0, false, false);
			} 
			else if( j < i ) {
				// the file exists
				
				// j is less than i, which means we have removed a file previously
				// move the current entry up to where j is pointing
				saveFileEntry(prefix, j, filename, syntax, language, skip, noReasoner);
				saveFileEntry(prefix, i, "", 0, 0, false, false); // clear the entry we moved up
				
				j++;
				
			}
			else {
				
				j++;
			}
		}
		
		// clear any entries that are beyond j
		for( ; j <= 9; j++) {
			saveFileEntry(prefix, j, "", 0, 0, false, false);
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
	public void saveLastNameOutput(String name) {
		appPrefs.put(PREF_LASTNAMEOUTPUT, name);
	}
	//THE LAST FORMAT FOR THE OUTPUT FILE OF THE REFERENCE EVALUATION HAS NOT BEEN IMPLEMENTED YET BECAUSE THERE IS ONLY ONE FORMAT NOW
	
	/**
	 * The last value for the skip namespace value in the loading ontology function
	 */
	public boolean getLastSkipNamespace() {
		boolean last = appPrefs.getBoolean(PREF_LAST_SKIP_NAMESPACE, false);
		return last;
	}
	private boolean getSkipNamespace( String prefix, int position) {
		return appPrefs.getBoolean(prefix + "skip" + position, false);
	}
	
	public boolean getLastNoReasoner() {
		boolean last = appPrefs.getBoolean(PREF_LAST_NO_REASONER, false);
		return last;
	}
	public boolean getNoReasoner( String prefix, int position ) {
		return appPrefs.getBoolean(prefix + "noreasoner" + position, false);
	}
	
	public boolean getRecentSourceSkipNamespace( int position) {
		return appPrefs.getBoolean(PREF_RECENTSOURCE + "skip" + position, false);
	}
	
	public boolean getRecentSourceNoReasoner( int position) {
		return appPrefs.getBoolean(PREF_RECENTSOURCE + "noreasoner" + position, false);
	}
	
	public boolean getRecentTargetSkipNamespace( int position) {
		return appPrefs.getBoolean(PREF_RECENTTARGET + "skip" + position, false);
	}
	
	public boolean getRecentTargetNoReasoner( int position) {
		return appPrefs.getBoolean(PREF_RECENTTARGET + "noreasoner" + position, false);
	}
	
	/**
	 * Support for "Selected Matchings Only" view mode
	 */
	public boolean getDisableVisualization() {
		return appPrefs.getBoolean(PREF_DISABLEVISUALIZATION, false);
	}
	
	public void saveDisableVisualization(boolean value) {
		appPrefs.putBoolean(PREF_DISABLEVISUALIZATION, value);
	}
	
	
	/**
	 * Support for "Selected Matchings Only" view mode
	 */
	public boolean getSelectedMatchingsOnly() {
		return appPrefs.getBoolean(PREF_SELECTEDMATCHINGSONLY, false);
	}
	
	public void saveSelectedMatchingsOnly(boolean value) {
		appPrefs.putBoolean(PREF_SELECTEDMATCHINGSONLY, value);
	}
	
	/**
	 * Support for showing localnames into vertex
	 */
	public boolean getShowLocalname() {
		return appPrefs.getBoolean(PREF_SHOWLOCALNAME, true);
	}
	
	public void saveShowLocalname(boolean value) {
		appPrefs.putBoolean(PREF_SHOWLOCALNAME, value);
	}
	
	/**
	 * Support for showing labels into vertex
	 */
	public boolean getShowLabel() {
		return appPrefs.getBoolean(PREF_SHOWLABEL, false);
	}
	
	public void saveShowLabel(boolean value) {
		appPrefs.putBoolean(PREF_SHOWLABEL, value);
	}

	
	
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

	
}
