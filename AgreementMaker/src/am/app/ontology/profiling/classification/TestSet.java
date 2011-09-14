package am.app.ontology.profiling.classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import am.app.ontology.profiling.classification.trainingGeneration.Result;
import am.app.ontology.profiling.classification.trainingGeneration.Winner;

public class TestSet {
	private LinkedList<Test> testList;
	
	
	public TestSet() {
		this.testList = new LinkedList<Test>();
	}
	
	public TestSet(String fileName){
		
		this.testList= new LinkedList<Test>();
		
		// open and parse the benchmark XML file
		System.out.println("Reading batch file.");
		File batchFile = new File( fileName );

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc= null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			doc = db.parse(batchFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());

		


		// parse the Test
		NodeList testNodeList = doc.getElementsByTagName("test");
		for( int i = 0; i < testNodeList.getLength(); i++ ) {
			Element currentTest = (Element) testNodeList.item(i);
			String className = currentTest.getAttribute("className");
			String sourceOntology = currentTest.getAttribute("sourceOntologyFileName");
			String targetOntology = currentTest.getAttribute("targetOntologyFileName");
			addTest(sourceOntology,targetOntology,className);
			//classList.add(className);
		}
		
		
	}
	
	
	
	

	@Override
	public String toString() {
		String s = "TestSet [ \n";
		for (Iterator<Test> it = testList.iterator(); it.hasNext();) {
			Test test = (Test) it.next();
			s= s+test.toString() +"\n";
		}
		return s+ "]";
	}

	public void addTest(Test t){
		testList.add(t);
	}
	public void addTest(String sourceOntologyName, String targetOntologyName,String className){
	
		Test t = new Test(sourceOntologyName,targetOntologyName,className);
		//System.out.println(r);
		testList.add(t);
		
	}
	
	public void addTest(String sourceOntologyName, String targetOntologyName){
		
		Test t = new Test(sourceOntologyName,targetOntologyName);
		//System.out.println(r);
		testList.add(t);
		
	}
	
	
	public LinkedList<Test> getTestList() {
		return testList;
	}

	public void setTestList(LinkedList<Test> testList) {
		this.testList = testList;
	}

	public void storeFile(String fileName) throws Exception{
		
		String root = "root";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElement(root);
		document.appendChild(rootElement);
		
		//add the tests
		Element tests = document.createElement("tests");
		rootElement.appendChild(tests);
		for (Iterator<Test> it = testList.iterator(); it.hasNext();) {
			Test c = (Test) it.next();
			Element testElem = document.createElement("test");
			testElem.setAttribute("className", c.getClassName());
			testElem.setAttribute("sourceOntologyFileName", c.getSourceOntology());
			testElem.setAttribute("targetOntologyFileName", c.getTargetOntology());
			tests.appendChild(testElem);
		}
		
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(document);
		StreamResult result =  new StreamResult(fileName);
		transformer.transform(source, result);
		
	}
	
	

	
/*	public static TestSet loadOutputTrainingFromFile(String fileName){
		TestSet o = new TestSet();
		LinkedList<String> classList= new LinkedList<String>();
		
		// open and parse the benchmark XML file
		System.out.println("Reading batch file.");
		File batchFile = new File( fileName );

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc= null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			doc = db.parse(batchFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());

		


		// parse the Classes
		NodeList classNodeList = doc.getElementsByTagName("class");
		for( int i = 0; i < classNodeList.getLength(); i++ ) {
			Element currentClass = (Element) classNodeList.item(i);
			String className = currentClass.getAttribute("className");
			classList.add(className);
		}
		
		
		// parse the result
		NodeList resultNodeList = doc.getElementsByTagName("result");
		for( int i = 0; i < resultNodeList.getLength(); i++ ) {
			Element currentResult = (Element) resultNodeList.item(i);
			String className = currentResult.getAttribute("className");
			String matcherName = currentResult.getAttribute("matcherName");
			String param = currentResult.getAttribute("param");
			String sourceOntologyFileName = currentResult.getAttribute("sourceOntologyFileName");
			String sourceOntologyName = currentResult.getAttribute("sourceOntologyName");
			String targetOntologyFileName = currentResult.getAttribute("targetOntologyFileName");
			String targetOntologyName = currentResult.getAttribute("targetOntologyName");
			double precision =Double.parseDouble( currentResult.getAttribute("precision"));
			double recall = Double.parseDouble(currentResult.getAttribute("recall"));
			double fMeasure =Double.parseDouble( currentResult.getAttribute("fMeasure"));
			o.addResult(className, matcherName, param, sourceOntologyFileName, targetOntologyFileName, sourceOntologyName, targetOntologyName, precision, recall, fMeasure);
		}


		// parse the Classes
		NodeList winnerNodeList = doc.getElementsByTagName("winner");
		for( int i = 0; i < winnerNodeList.getLength(); i++ ) {
			Element currentWinner = (Element) winnerNodeList.item(i);
			String className = currentWinner.getAttribute("className");
			String sourceOntologyFileName = currentWinner.getAttribute("sourceOntologyFileName");
			String targetOntologyFileName = currentWinner.getAttribute("targetOntologyFileName");
			o.addWinner(className, sourceOntologyFileName, targetOntologyFileName);
		}
		
		o.setClassList(classList);
		return o;
		
	}
	*/
	
	
	
	
	public static void main(String[] args) {
		
	//	TestSet o = TestSet.loadOutputTrainingFromFile("Classification/prova.xml");
	//	o.storeTable("Classification/table3.txt");
		TestSet o = new TestSet();
		o.addTest("ser", "der");
		o.addTest("tre", "wer");
		o.addTest("rew", "qwe");
		
		try {
			o.storeFile("Classification/prova.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TestSet t2 = new TestSet("Classification/prova.txt");
		System.out.println(t2);
		
	}
}
