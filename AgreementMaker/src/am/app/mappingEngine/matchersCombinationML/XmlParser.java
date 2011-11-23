package am.app.mappingEngine.matchersCombinationML;

/**
 * Parser that reads the training and test data, called from wrappers.
 */
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class XmlParser {
	org.w3c.dom.Document dom;
	ArrayList<TrainingLayout> tlList;
	public void parseXMLFile(String filename)
	{
		try
		{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		dom = db.parse(filename);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	public ArrayList<TrainingLayout> parseDocument(String xmlfilename,String elementname,String type)
	{
		tlList=new ArrayList<TrainingLayout>();
		parseXMLFile(xmlfilename);
		Element docEle = dom.getDocumentElement();
		//xml file name
		NodeList nl = docEle.getElementsByTagName(elementname);
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element e = (Element)nl.item(i);
				if(type.equals("training"))
				{
                    TrainingLayout ot=getTrainingLayout(e);
                    tlList.add(ot);
				}
				else
				{
					TrainingLayout ot=getTestingLayout(e);
                    tlList.add(ot);
				}

					}
		}

		return tlList;
	}
	
	public TrainingLayout getTrainingLayout(Element e)
	{
		
		TrainingLayout tl=new TrainingLayout();
		NodeList n1=e.getElementsByTagName("sourceontology");
		NodeList n2=e.getElementsByTagName("targetontology");
		NodeList n3=e.getElementsByTagName("refalignment");
		/*System.out.println("----------------------------------------");
		System.out.println(n1.item(0).getAttributes().item(1).getNodeValue());
		System.out.println(n2.item(0).getAttributes().item(1).getNodeValue());
		System.out.println(n3.item(0).getAttributes().item(1).getNodeValue());
		System.out.println("---------------------------------------\n");*/
		tl.setsourceOntologyPath(n1.item(0).getAttributes().item(1).getNodeValue());
		tl.settargetOntologyPath(n2.item(0).getAttributes().item(1).getNodeValue());
		tl.setrefalignmentPath(n3.item(0).getAttributes().item(1).getNodeValue());
		return tl;
	}
	
	public TrainingLayout getTestingLayout(Element e)
	{
		
		TrainingLayout tl=new TrainingLayout();
		NodeList n1=e.getElementsByTagName("sourceontology");
		NodeList n2=e.getElementsByTagName("targetontology");
		NodeList n3=e.getElementsByTagName("refalignment");
		/*System.out.println("----------------------------------------");
		System.out.println(n1.item(0).getAttributes().item(1).getNodeValue());
		System.out.println(n2.item(0).getAttributes().item(1).getNodeValue());
		System.out.println(n3.item(0).getAttributes().item(1).getNodeValue());
		System.out.println("---------------------------------------\n");*/
		tl.setsourceOntologyPath(n1.item(0).getAttributes().item(0).getNodeValue());
		tl.settargetOntologyPath(n2.item(0).getAttributes().item(0).getNodeValue());
		tl.setrefalignmentPath(n3.item(0).getAttributes().item(0).getNodeValue());
		return tl;
	}
}

