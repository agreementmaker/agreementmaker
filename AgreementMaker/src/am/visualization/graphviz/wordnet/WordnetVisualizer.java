package am.visualization.graphviz.wordnet;

import java.io.File;
import java.util.HashMap;

import am.Utility;
import am.visualization.graphviz.GraphViz;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordnetVisualizer {
	private WordNetDatabase WordNet;
	HashMap<Synset, String> nodes;
	
	boolean replicate;
	
	
	public WordnetVisualizer(){
		// Initialize the WordNet Interface (JAWS)
		// Initialize the WordNet interface.
		System.out.println("Initializing WordNet");
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		
		// Instantiate 
		try 
		{
			WordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) 
		{
			Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
		System.out.println("Done");
	}
	
	public Synset[] getSynsets(String searchTerm){
		Synset[] synsets = WordNet.getSynsets(searchTerm, SynsetType.NOUN);
		
		//System.out.println(synsets);
		
		System.out.print("[");
		for(Synset syn: synsets){
			System.out.print("[");
			//System.out.println(syn);
			//syn.
			int l = syn.getWordForms().length;
			for(int i = 0; i < l; i++){
				String str = syn.getWordForms()[i];
				if(i != l-1)
					System.out.print(str + ", ");
				else System.out.print(str);
			}
			System.out.print("]");
		}
		System.out.println("]");
	
		return synsets;
	}
	
	public byte[] synsetsToGraph(Synset[] synsets){
		nodes = new HashMap<Synset, String>();
		
		GraphViz gv = new GraphViz();
	    gv.addln(gv.start_graph());
	    
	    gv.addln("edge [dir=\"back\"];");
	    
	    char ch = 'a';
	    ch--;
	    String id;
	    String line;
	    for(Synset syn: synsets){
	    	ch++;
	    	id = ch + "";
	    	nodes.put(syn, id);
	    	String options = "color = red" + ", taillabel = \"bla bla\"";
			line = synsetToLine(syn, id, options);
	    	
			line += "\n" + getHypernymSource(syn, id, 2, 10, true);
			gv.addln(line);
			
			String def = "bla bla bla <br/>bla bla bla";
			def = syn.getDefinition();
			
			int every = 6;
			int count = 0;
			for (int i = 0; i < def.length(); i++) {
				if(def.charAt(i)==' ') count++;
				if(count==every){
					def = def.substring(0,i) + "<br/>" + def.substring(i+1);
					count = 0;
				}
			}
			
			
			line = id + " -> " + id + " [ label=<" + def + ">, color=transparent]";
			gv.addln(line);
	    }
	    
	    //gv.addln("A -> B;");
	    //gv.addln("A -> C;");
	    gv.addln(gv.end_graph());
	    //System.out.println(gv.getDotSource());
		
	    return gv.getGraph( gv.getDotSource(), "gif" );
	}
	
	
	//
	public String getHypernymSource(Synset syn, String id, int depth, int limit, boolean hypernyms){
		NounSynset[] parents;
		if(hypernyms)
			parents = ((NounSynset)syn).getHypernyms();
		else parents = ((NounSynset)syn).getHyponyms();
		String ret = "";
		
		char base = 'a';
		char ch = (char)(base - 1);
		for (int i = 0; i < parents.length; i++) {
			ch = (char) (base + (i % 25));
			String id2 = id + "" + ch;
			
			//System.out.println(hypernyms[i].toString());
			
			if(!nodes.containsKey(parents[i])){
				nodes.put(parents[i], id2);
				ret += synsetToLine(parents[i], id2, null)  + "\n";
			}
			else{
				id2 = nodes.get(parents[i]);
				ret += id2 + " -> " + id + ";\n"; 
				continue;
			}
			
			ret += id2 + " -> " + id + ";\n";
			
			
			if(depth < limit)
				ret += getHypernymSource(parents[i], id2, depth+1, limit, hypernyms);
				
			if(i != parents.length-1 && depth==limit)
				 ret += "\n";
		}
		return ret;
	}
	
	public String synsetToLine(Synset syn, String id, String options){
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

	public static void main(String[] args) {
		WordnetVisualizer viz = new WordnetVisualizer();
		Synset[] synsets = viz.getSynsets("sheep");
		byte[] graph = viz.synsetsToGraph(synsets);
		
		File out = new File("out.gif");
	    System.out.println("Writing graph to file...");
	    
	    GraphViz gv = new GraphViz();
	    gv.writeGraphToFile( graph , out );
	    
	}
}
