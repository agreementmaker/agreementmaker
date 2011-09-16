package classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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






public class TrainSet {

	private LinkedList<String> classList;
	//private LinkedList<Result> resultList;
	private LinkedList<Train> winnerList;
	
	public TrainSet() {
		this.classList = new LinkedList<String>();
		//this.resultList = new LinkedList<Result>();
		this.winnerList = new LinkedList<Train>();
	}
	
	
	public TrainSet(String fileName){
		this.classList= new LinkedList<String>();
		//this.resultList = new LinkedList<Result>();
		this.winnerList = new LinkedList<Train>();
		
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
			this.classList.add(className);
		}
		


		// parse the Classes
		NodeList winnerNodeList = doc.getElementsByTagName("winner");
		for( int i = 0; i < winnerNodeList.getLength(); i++ ) {
			Element currentWinner = (Element) winnerNodeList.item(i);
			String className = currentWinner.getAttribute("className");
			double score1 = Double.parseDouble( currentWinner.getAttribute("score1") );
			double score2 = Double.parseDouble( currentWinner.getAttribute("score2") );
			double score3 = Double.parseDouble( currentWinner.getAttribute("score3") );
			this.addTrain(score1,score2,score3,className);
		}
		
		
	}
	
	
	
	

	@Override
	public String toString() {
		String s = "TrainSet [ \n";
		for (Iterator<String> it = classList.iterator(); it.hasNext();) {
			String className = (String) it.next();
			s= s+ "Class ["+className +"]\n";
		}
			
		/*for (Iterator<Result> it = resultList.iterator(); it.hasNext();) {
			Result className = (Result) it.next();
			s= s+ className +"\n";
		}*/
		
		for (Iterator<Train> it = winnerList.iterator(); it.hasNext();) {
			Train className = (Train) it.next();
			s= s+ className +"\n";
		}
		return s+ "]";
	}

/*	public void addResult(Result r){
		resultList.add(r);
	}
	public void addResult(
	 String className,
	 String matcherName,
	 String param,
	 String sourceOntologyFileName,
	 String targetOntologyFileName,
	 String sourceOntologyName,
	 String targetOntologyName,
	 double precision,
	 double recall,
	 double fMeasure){
	
		Result r = new Result(className, matcherName, param, sourceOntologyFileName, targetOntologyFileName,sourceOntologyName,targetOntologyName, precision, recall, fMeasure);
		//System.out.println(r);
		resultList.add(r);
		
	}*/
	public void addTrain(Train w){
		winnerList.add(w);
	}
	public void addTrain(double score1, double score2, double score3, 
	 String className){
	
		Train w = new Train(score1, score2, score3, className  );
		winnerList.add(w);
		
	}
	
	public void addClasses(String s){
		classList.add(s);
	}
	
	
	public LinkedList<String> getClassList() {
		return classList;
	}

	public void setClassList(LinkedList<String> classList) {
		this.classList = classList;
	}

	/*public LinkedList<Result> getResultList() {
		return resultList;
	}

	public void setResultList(LinkedList<Result> resultList) {
		this.resultList = resultList;
	}
*/
	public LinkedList<Train> getWinnerList() {
		return winnerList;
	}

	public void setWinnerList(LinkedList<Train> winnerList) {
		this.winnerList = winnerList;
	}
	
	
	public void storeFile(String fileName) throws Exception{
		
		String root = "root";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElement(root);
		document.appendChild(rootElement);
		
		//add the classes
		Element classes = document.createElement("classes");
		rootElement.appendChild(classes);
		for (Iterator<String> it = classList.iterator(); it.hasNext();) {
			String c = (String) it.next();
			Element classElem = document.createElement("class");
			classElem.setAttribute("className", c);
			classes.appendChild(classElem);
		}
		
	/*	//add the result
		Element results = document.createElement("results");
		rootElement.appendChild(results);
		for (Iterator<Result> it = resultList.iterator(); it.hasNext();) {
			Result result = (Result) it.next();
			Element resultElem = document.createElement("result");
			resultElem.setAttribute("className", result.getClassName());
			resultElem.setAttribute("matcherName", result.getMatcherName());
			resultElem.setAttribute("param", result.getParam());
			resultElem.setAttribute("sourceOntologyFileName", result.getSourceOntologyFileName());
			resultElem.setAttribute("targetOntologyFileName", result.getTargetOntologyFileName());
			resultElem.setAttribute("sourceOntologyName", result.getSourceOntologyName());
			resultElem.setAttribute("targetOntologyName", result.getTargetOntologyName());
			resultElem.setAttribute("precision", ""+result.getfMeasure());
			resultElem.setAttribute("recall", ""+result.getRecall());
			resultElem.setAttribute("fMeasure", ""+result.getfMeasure());
			results.appendChild(resultElem);
		}*/
		
		//add the winner
		Element winners = document.createElement("winners");
		rootElement.appendChild(winners);
		for (Iterator<Train> it = winnerList.iterator(); it.hasNext();) {
			Train winner = (Train) it.next();
			Element winnerElem = document.createElement("winner");
			winnerElem.setAttribute("className", winner.getClassName());
			winnerElem.setAttribute("score1", winner.getScore1()+"");
			winnerElem.setAttribute("score2", winner.getScore2()+"");
			winnerElem.setAttribute("score3", winner.getScore3()+"");
			winners.appendChild(winnerElem);
		}
		
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(document);
		StreamResult result =  new StreamResult(fileName);
		transformer.transform(source, result);
		
	}
	
	
/*	public void  storeTable(String fileName){
		PrintStream output= null;
		try {
			output = new PrintStream(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		for (Iterator it = resultList.iterator(); it.hasNext();) {
			Result res = (Result) it.next();
			output.println(res.getClassName()+"\t"+res.getSourceOntologyName()+"\t"+res.getTargetOntologyName()+"\t"+res.getPrecision()+"\t"+res.getRecall()+"\t"+res.getfMeasure());
			
		}
	}
	
	
	public void  storeTableVisual(String fileName){
		PrintStream output= null;
		try {
			output = new PrintStream(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		output.print("ONTOLOGIES\t");
		for (Iterator it = classList.iterator(); it.hasNext();) {
			String s = (String) it.next();
			output.print(s+"Precision\t");
			output.print(s+"Recall\t");
			output.print(s+"FMeasure\t");
		}
		output.println();
		
		boolean b = true; 
		String sourceOntologyName = "";
		String targetOntologyName = "";
		for (Iterator<Result> it = resultList.iterator(); it.hasNext();) {
			Result res = (Result) it.next();
			String sourceO = res.getSourceOntologyName();
			String targetO = res.getTargetOntologyName();
			if (sourceOntologyName.equals(sourceO)&&targetOntologyName.equals(targetO)){
				output.print(res.getPrecision()+"\t");
				output.print(res.getRecall()+"\t");
				output.print(res.getfMeasure()+"\t");
			}
			else{
				if (b) b=false; else output.println();
				output.print(res.getSourceOntologyName()+"-"+res.getTargetOntologyName()+"\t");
				sourceOntologyName = sourceO;
				targetOntologyName = targetO;
				output.print(res.getPrecision()+"\t");
				output.print(res.getRecall()+"\t");
				output.print(res.getfMeasure()+"\t");
			}
			
			
		}
		
		
	}
	
	*/
	
	/*public static TrainSet loadOutputTrainingFromFile(String fileName){
		TrainSet o = new TrainSet();
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
		/*
		TrainSet o = TrainSet.loadOutputTrainingFromFile("Classification/prova.xml");
		o.storeTable("Classification/table3.txt");
		OutputTrainingGenerator o = new OutputTrainingGenerator();
		o.addClasses("C1");
		o.addClasses("C2");
		
		o.addResult("w2", "w3", "e4", "e4", "w3","e4", "w3", 4, 5, 6);
		o.addResult("w1", "w4", "54", "t4", "63","e4", "w3", 43, 35,36);
		
		o.addWinner("er", "rt", "tt");
		o.addWinner("we", "fg", "gt");
		
		try {
			o.storeFile("Classification/prova.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}


