package am.visualization.graphviz.wordnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import am.ui.UIUtility;
import am.visualization.graphviz.GraphViz;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordnetVisualizer {
	private WordNetDatabase WordNet;
	HashMap<Synset, String> nodes;
	
	Map<Synset, ScoredSynset> sourceScoredBySynset;
	Map<Synset, ScoredSynset> targetScoredBySynset;
	
	boolean replicate;
	
	HashMap<Synset, String> definitions;
	
	Logger log;
	
	DecimalFormat format = new DecimalFormat("0.000");
	
	public WordnetVisualizer(){
		// Initialize the WordNet Interface (JAWS)
		// Initialize the WordNet interface.
		System.out.println("Initializing WordNet");
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		
		log = Logger.getLogger(WordnetVisualizer.class);
		
		// Instantiate 
		try 
		{
			WordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) 
		{
			UIUtility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
		System.out.println("Done");
	}
	
	public Synset[] getSynsets(String searchTerm){
		Synset[] synsets = WordNet.getSynsets(searchTerm, SynsetType.NOUN);
		
		//System.out.println(synsets);
		
		log.debug("[");
		for(Synset syn: synsets){
			log.debug("[");
			//System.out.println(syn);
			//syn.
			int l = syn.getWordForms().length;
			for(int i = 0; i < l; i++){
				String str = syn.getWordForms()[i];
				if(i != l-1)
					log.debug(str + ", ");
				else log.debug(str);
			}
			log.debug("]");
		}
		log.debug("]");
	
		return synsets;
	}
	
	public String synsetsToGraph(Synset[] synsets){
		return synsetsToGraph(synsets, "gif");
	}
	
	public String synsetsToGraph(Synset[] synsets, String type){
		nodes = new HashMap<Synset, String>();
		
		GraphViz gv = new GraphViz();
	    gv.addln(gv.start_graph());
	    
	    gv.addln("edge [dir=\"back\"];");
	    
	    String options;
	    String line;
	    String id;
	        
	    
	    if(targetScoredBySynset != null){
			for(ScoredSynset scored: targetScoredBySynset.values()){
				if(scored != null){
					String targetId = scored.hashCode() + "";  
					nodes.put(scored.getSynset(), targetId);
					String targetDef = scored.getSynset().getDefinition();
					targetDef = format.format(scored.getScore()) + " " + targetDef;
					targetDef = stringToHtml(targetDef);
					options = "color = blue";
					line = synsetToLine(scored.getSynset(), targetId, options)  + "\n";
					gv.addln(line);
					line = targetId + " -> " + targetId + " [ label=<" + targetDef + ">, color=transparent]";
					gv.addln(line);
				}
			}
		}
	    	    
	    //char ch = 'a';
	    //ch--;
	    for(Synset syn: synsets){
	    	//ch++;
	    	//id = ch + "";
	    	//System.out.println(syn);
	    	id = syn.hashCode() + "";
	    	nodes.put(syn, id);
	    	options = "color = red" + ", taillabel = \"bla bla\"";
			line = synsetToLine(syn, id, options);
	    	
			line += "\n" + getHypSource(syn, id, 2, 10, true);
			gv.addln(line);
			
			String def = syn.getDefinition();
			
			if(sourceScoredBySynset != null){
				ScoredSynset scored = sourceScoredBySynset.get(syn);
				if(scored != null){
					def = format.format(scored.getScore()) + " " + def;
				}
			}
						
			def = stringToHtml(def);
					
			line = id + " -> " + id + " [ label=<" + def + ">, color=transparent]";
			gv.addln(line);
	    }
	    
	    //gv.addln("A -> B;");
	    //gv.addln("A -> C;");
	    gv.addln(gv.end_graph());
	    //System.out.println(gv.getDotSource());
		
	    return gv.getDotSource();
	}
	
	
	private String stringToHtml(String def) {
		int every = 6;
		int count = 0;
		for (int i = 0; i < def.length(); i++) {
			if(def.charAt(i)==' ') count++;
			if(count==every){
				def = def.substring(0,i) + "<br/>" + def.substring(i+1);
				count = 0;
			}
		}
		return def;
	}

	//
	public String getHypSource(Synset syn, String id, int depth, int limit, boolean hypernyms){
		NounSynset[] parents;
		if(hypernyms)
			parents = ((NounSynset)syn).getHypernyms();
		else parents = ((NounSynset)syn).getHyponyms();
		String ret = "";
		
		//char base = 'a';
		//char ch = (char)(base - 1);
		for (int i = 0; i < parents.length; i++) {
			//ch = (char) (base + (i % 25));
			//String id2 = id + "" + ch;
			
			String id2 = parents[i].hashCode() + ""; 
			
			//System.out.println(hypernyms[i].toString());
			
			if(!nodes.containsKey(parents[i])){
				nodes.put(parents[i], id2);
				
				String options = null;
				
//				if(targetScoredBySynset != null){
//					ScoredSynset scored = targetScoredBySynset.get(parents[i]);
//					if(scored != null){
//						String def = format.format(scored.getScore()) + " " + parents[i].getDefinition();
//						options = "color = blue";
//						ret = id2 + " -> " + id2 + " [ label=<" + stringToHtml(def) + ">, color=transparent]";
//					}
//				}
				
				ret += synsetToLine(parents[i], id2, options)  + "\n";
			}
			else{
				id2 = nodes.get(parents[i]);
				ret += id2 + " -> " + id + ";\n"; 
				continue;
			}
			
			ret += id2 + " -> " + id + ";\n";
			
			
			if(depth < limit)
				ret += getHypSource(parents[i], id2, depth+1, limit, hypernyms);
				
			if(i != parents.length-1 && depth==limit)
				 ret += "\n";
		}
		return ret;
	}
	
	public String synsetToLine(Synset syn, String id, String options){
		//" [ label=<" + def + ">, color=transparent]"
		
		String line = id + " [label = \"";	
    	int l = syn.getWordForms().length;
		for(int i = 0; i < l; i++){
			String str = syn.getWordForms()[i];
			if(i != l-1)
				line += str + ", ";
			else line += str;
			
		}
		
		line += "\"";
		if(options != null) 
			line += ", " + options;
		line += "];";
		
		return line;
	}

	public void saveGraphOnFile(String filename, Map<Synset, ScoredSynset> sourceScoredBySynset,
								Map<Synset, ScoredSynset> targetScoredBySynset){
		this.sourceScoredBySynset = sourceScoredBySynset;
		this.targetScoredBySynset = targetScoredBySynset;
		
		Collection<ScoredSynset> sourceSynsets = sourceScoredBySynset.values();
		Synset[] synsets = new Synset[sourceSynsets.size()];
		
		int i = 0;
		for (ScoredSynset synset : sourceSynsets) {
			synsets[i] = synset.getSynset();
			i++;
		}
				
		if(synsets.length == 0) return;
		
		GraphViz gv = new GraphViz();
		
		String source = synsetsToGraph(synsets, "pdf");
		byte[] graph = gv.getGraph( source, "pdf" );
				
		String dir = "wordnetVizMatch";
		
		new File(dir).mkdir();
		
		try {
			FileOutputStream fos = new FileOutputStream(dir + File.separator + filename + ".dot");
			fos.write(source.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		File out = new File(dir + File.separator + filename + ".pdf");
	    
		//System.out.println(out.getAbsolutePath());
				
		System.out.println("WVZ: Writing graph to file [" + filename + "]...");
	    
	    gv.writeGraphToFile( graph , out );
		
	    this.sourceScoredBySynset = null;
		this.targetScoredBySynset = null;
	}
		
	
	public void saveGraphOnFile(String searchTerm){
		Synset[] synsets = getSynsets(searchTerm);
		
		if(synsets.length == 0) return;
		
		GraphViz gv = new GraphViz();
	    
		
		String source = synsetsToGraph(synsets, "pdf");
		byte[] graph = gv.getGraph( source, "pdf" );
		
		File out = new File("wordnetViz/" + searchTerm + ".pdf");
	    
		//System.out.println(out.getAbsolutePath());
				
		System.out.println("WVZ: Writing graph to file [" + searchTerm + "]...");
	    
	    gv.writeGraphToFile( graph , out );
	}
	

	public static void main(String[] args) {
		WordnetVisualizer viz = new WordnetVisualizer();
		Synset[] synsets = viz.getSynsets("medium");
		
		GraphViz gv = new GraphViz();
	    
		
		String source = viz.synsetsToGraph(synsets, "pdf");
		byte[] graph = gv.getGraph( source, "pdf" );
			
		File out = new File("out.pdf");
	    System.out.println("Writing graph to file...");
	    
	    gv.writeGraphToFile( graph , out );
	    System.out.println("Done");
	}
	
	public void setDefinitions(HashMap<Synset, String> definitions) {
		this.definitions = definitions;
	}
	
	public void setSourceScoredBySynset(
			HashMap<Synset, ScoredSynset> sourceScoredBySynset) {
		this.sourceScoredBySynset = sourceScoredBySynset;
	}
	
	public void setTargetScoredBySynset(
			HashMap<Synset, ScoredSynset> targetScoredBySynset) {
		this.targetScoredBySynset = targetScoredBySynset;
	}
}
