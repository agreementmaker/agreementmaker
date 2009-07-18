package am.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AlignmentSet;

public class AlignmentOutput
{
    private AlignmentSet alignmentSet = null;
    private RandomAccessFile raf = null;
    private String filepath = null;
    private ArrayList writeList = null;

    public AlignmentOutput(AlignmentSet as, String fp)
    {
        alignmentSet = as;
        filepath = fp;
        try {
            File file = new File(fp);
            if (file.exists()) {
                file.delete();
            }
            raf = new RandomAccessFile(filepath, "rw");
            writeList = new ArrayList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void write(String onto1, String onto2, String uri1, String uri2)
    {
        writeNS();
        writeStart("yes", "0", "11", onto1, onto2, uri1, uri2);
        for (int i = 0, n = alignmentSet.size(); i < n; i++) {
            Alignment alignment = (Alignment) alignmentSet.getAlignment(i);
            String e1 = alignment.getEntity1().toString();
            String e2 = alignment.getEntity2().toString();
            String measure = Double.toString(alignment.getSimilarity());
            writeElement(e1, e2, measure);

        }
        writeEnd();
        writeToFile();
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeNS()
    {
        String temp = "<?xml version='1.0' encoding='utf-8'?>\n" 
                + "<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment' \n" 
                + "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' \n" 
                + "xmlns:xsd='http://www.w3.org/2001/XMLSchema#'>\n";
        writeList.add(temp);
    }

    public void writeStart(String xml, String level, String type, String onto1,
            String onto2, String uri1, String uri2)
    {
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

    public void writeElement(String res1, String res2, String measure, String r)
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
}