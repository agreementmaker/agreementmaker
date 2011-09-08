package am.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;

import am.Utility;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;

import com.hp.hpl.jena.rdf.model.Resource;

public class AlignmentOutput
{
    private Alignment<Mapping> alignmentSet = null;
    private RandomAccessFile raf = null;  // Why is this a RandomAccessFile? Can be a BufferedWriter for faster writing. - Cosmin.
    private String filepath = null;
    private ArrayList<String> writeList = null;  // TODO: REMOVE THIS!!!!!! VERY VERY VERY INEFFICIENT USE OF MEMORY - Cosmin.
    private StringBuilder buffer;

    
    /**
     * This constructor is used for saving as a String, not to a file.  Use only with compileString().
     * @param as The set of mappings between two ontologies.
     */
    public AlignmentOutput( Alignment<Mapping> as ) {
    	alignmentSet = as;
    	filepath = "";
    	buffer = new StringBuilder();
    }
    
    /**
     * This method saves the AlignmentSet in OAEI format in a string in memory, then returns the string.
     * @param onto1 The URI of the source ontology. Can be empty.
     * @param onto2 The URI of the target ontology. Can be empty.
     * @param uri1 The URI of the source ontology.
     * @param uri2 The URI of the target ontology.
     * @return The OAEI formatted XML string.
     */
    public String compileString(String onto1, String onto2, String uri1, String uri2)
    {
        stringNS();
        stringStart("yes", "0", "11", onto1, onto2, uri1, uri2);
        for (int i = 0, n = alignmentSet.size(); i < n; i++) {
            Mapping alignment = (Mapping) alignmentSet.get(i);
            String e1 = alignment.getEntity1().getUri();
            String e2 = alignment.getEntity2().getUri();
            String measure = Double.toString(alignment.getSimilarity());
            String prov = alignment.getProvenance();
            if( prov != null ) stringElement(e1, e2, measure, prov);
            else stringElement(e1, e2, measure);

        }
        stringEnd();
        return buffer.toString();
    }
    
    public void stringNS()
    {
        String temp = "<?xml version='1.0' encoding='utf-8'?>\n" 
                + "<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment' \n" 
                + "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' \n" 
                + "xmlns:xsd='http://www.w3.org/2001/XMLSchema#'>\n";
        buffer.append(temp);
    }
    
    public void stringStart(String xml, String level, String type, String onto1,
            String onto2, String uri1, String uri2)
    {
    	//Below two if statement is only for OAEI 09 test case 303 
    	//where uri is not mentioned in the ontology by xlmns=base:
    	if(uri1.equals("")){
    		Resource r = alignmentSet.get(0).getEntity1().getResource();
    		uri1 = r.getURI();
    		uri1 = uri1.substring(0, uri1.indexOf("#")+1);
    	}
    	if(uri2.equals("")){
    		Resource r = alignmentSet.get(0).getEntity2().getResource();
    		uri2 = r.getURI();
    		uri2 = uri2.substring(0, uri2.indexOf("#")+1);
    		if(uri2.equalsIgnoreCase("http://www.aifb.uni-karlsruhe.de/ontology#"))
    		{
    			onto2 = "http://oaei.ontologymatching.org/2009/benchmarks/303/onto.rdf#";
    		}
    	}
        String temp = "<Alignment>\n" 
                + "  <xml>" + xml + "</xml>\n" 
                + "  <level>" + level + "</level>\n"
                + "  <type>" + type + "</type>\n" 
                + "  <onto1>" + onto1 + "</onto1>\n" 
                + "  <onto2>" + onto2 + "</onto2>\n" 
                + "  <uri1>" + uri1 + "</uri1>\n" 
                + "  <uri2>" + uri2 + "</uri2>";
        buffer.append(temp);
    }
    
    public void stringElement(String res1, String res2, String measure)
    {
        String temp = "    <map>\n" 
                + "      <Cell>\n" 
                + "        <entity1 rdf:resource=\"" + res1 + "\"/>\n" 
                + "        <entity2 rdf:resource=\"" + res2 + "\"/>\n" 
                + "        <measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" 
                + measure + "</measure>\n" 
                + "        <relation>=</relation>\n"
                + "      </Cell>\n" 
                + "    </map>";
        buffer.append(temp);
    }
    
    private void stringElement(String res1, String res2, String measure, String provenance)
    {
        String temp = "    <map>\n" 
                + "      <Cell>\n" 
                + "        <entity1 rdf:resource=\"" + res1 + "\"/>\n" 
                + "        <entity2 rdf:resource=\"" + res2 + "\"/>\n" 
                + "        <measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" 
                + measure + "</measure>\n" 
                + "        <relation>=</relation>\n"
                + "		   <provenance>" + provenance + "</provenance>"
                + "      </Cell>\n" 
                + "    </map>";
        buffer.append(temp);
    }
    
    public void stringEnd()
    {
        String temp = "  </Alignment>\n</rdf:RDF>";
        buffer.append(temp);
    }

    
    public AlignmentOutput(Alignment<Mapping> as, String fp) throws Exception
    {
        alignmentSet = as;
        filepath = fp;
        try {
            File file = new File(fp);
            if (file.exists()) {
            	if( Utility.displayConfirmPane("File already exists.  Overwrite?", "File exists") ) {
            		file.delete();
            	}
            	else {
            		throw new Exception("Not overwriting existing file.");
            	}
            }
            raf = new RandomAccessFile(filepath, "rw");
            writeList = new ArrayList<String>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public AlignmentOutput(Alignment<Mapping> as, File fp) throws Exception
    {
        alignmentSet = as;
        filepath = fp.getAbsolutePath();
        try {
            raf = new RandomAccessFile(fp, "rw");
            writeList = new ArrayList<String>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public AlignmentOutput(Alignment<Mapping> alignment, Writer writer ) {
    	
    }
    
    public void write(String onto1, String onto2, String uri1, String uri2, String matcherName)
    {
        writeNS(matcherName);
        writeStart("yes", "0", "11", onto1, onto2, uri1, uri2);
        for (int i = 0, n = alignmentSet.size(); i < n; i++) {
            Mapping alignment = (Mapping) alignmentSet.get(i);
            String e1 = alignment.getEntity1().getUri();
            String e2 = alignment.getEntity2().getUri();
            String measure = Double.toString(alignment.getSimilarity());
            String prov = alignment.getProvenance();
            MappingRelation relation = alignment.getRelation();
            if( prov != null ) writeElement(e1, e2, measure, relation, prov);
            else writeElement(e1, e2, measure, relation);

        }
        writeEnd();
        writeToFile();
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeNS(String matcherName)
    {
        String temp = "<?xml version='1.0' encoding='utf-8'?>\n" 
                + "<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment' \n" 
                + "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' \n" 
                + "xmlns:xsd='http://www.w3.org/2001/XMLSchema#' \n"
                + "matcherName='" + StringEscapeUtils.escapeHtml(matcherName) + "'>\n";
        writeList.add(temp);
    }

    public void writeStart(String xml, String level, String type, String onto1,
            String onto2, String uri1, String uri2)
    {
    	//Below two if statement is only for OAEI 09 test case 303 
    	//where uri is not mentioned in the ontology by xlmns=base:
    	if(uri1 == null || uri1.equals("")){
    		Resource r = alignmentSet.get(0).getEntity1().getResource();
    		uri1 = r.getURI();
    		uri1 = uri1.substring(0, uri1.indexOf("#")+1);
    	}
    	if(uri2 == null || uri2.equals("")){
    		Resource r = alignmentSet.get(0).getEntity2().getResource();
    		uri2 = r.getURI();
    		uri2 = uri2.substring(0, uri2.indexOf("#")+1);
    		if(uri2.equalsIgnoreCase("http://www.aifb.uni-karlsruhe.de/ontology#"))
    		{
    			onto2 = "http://oaei.ontologymatching.org/2009/benchmarks/303/onto.rdf#";
    		}
    	}
        String temp = "<Alignment>\n" 
                + "  <xml>" + xml + "</xml>\n" 
                + "  <level>" + level + "</level>\n"
                + "  <type>" + type + "</type>\n" 
                + "  <onto1>" + onto1 + "</onto1>\n" 
                + "  <onto2>" + onto2 + "</onto2>\n" 
                + "  <uri1>" + uri1 + "</uri1>\n" 
                + "  <uri2>" + uri2 + "</uri2>";
        writeList.add(temp);
    }

    public void writeEnd()
    {
        String temp = "  </Alignment>\n</rdf:RDF>";
        writeList.add(temp);
    }
    
    public void writeElement(String res1, String res2, String measure)
    {
        String temp = "    <map>\n" 
                + "      <Cell>\n" 
                + "        <entity1 rdf:resource=\"" + res1 + "\"/>\n" 
                + "        <entity2 rdf:resource=\"" + res2 + "\"/>\n" 
                + "        <measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" 
                + measure + "</measure>\n" 
                + "        <relation>=</relation>\n" 
                + "      </Cell>\n" 
                + "    </map>";
        writeList.add(temp);
    }

    public void writeElement(String res1, String res2, String measure, MappingRelation r)
    {
        String temp = "    <map>\n" 
                + "      <Cell>\n" 
                + "        <entity1 rdf:resource=\"" + res1 + "\"/>\n" 
                + "        <entity2 rdf:resource=\"" + res2 + "\"/>\n"
                + "        <measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" 
                + measure + "</measure>\n"
                + "        <relation>" + r + "</relation>\n"
                + "      </Cell>\n" 
                + "    </map>";
        writeList.add(temp);
    }

    public void writeElement(String res1, String res2, String measure, MappingRelation r, String prov)
    {
        String temp = "    <map>\n" 
                + "      <Cell>\n" 
                + "        <entity1 rdf:resource=\"" + res1 + "\"/>\n" 
                + "        <entity2 rdf:resource=\"" + res2 + "\"/>\n"
                + "        <measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" 
                + measure + "</measure>\n"
                + "        <relation>" + r + "</relation>\n"
                + "        <provenance>"+prov+"</provenance>\n"
                + "      </Cell>\n" 
                + "    </map>";
        writeList.add(temp);
    }
    
    public void writeToFile()
    {
        try {
            for (int i = 0, n = writeList.size(); i < n; i++) {
                raf.seek(raf.length());
                raf.writeBytes((String) writeList.get(i) + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public String getString() {
		return buffer.toString();
	}
}