package crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

import misc.Utilities;


public class NYTTopicPageCrawler {
	String urlString;  
	
	public NYTTopicPageCrawler(String urlString){
		this.urlString = urlString;
	}
	
	public ArrayList<String> crawl() 
	{
		String html = null;
		try {
			html = Utilities.getPage(urlString);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problems in retrieving the page");
			return new ArrayList<String>();
		}
		
		HTMLEditorKit.ParserCallback callback = new CallBack();
		
	    BufferedReader reader;
		try {
			
			ParserDelegator delegator = new ParserDelegator();
			System.out.println("Parsing...");
			delegator.parse(new StringReader(html), callback, true);
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> endpoints = ((CallBack)callback).getArticles();
		System.out.println(endpoints.size() + " endpoints");
		return endpoints;
	}
	
	public static void main(String[] args) {
		//NYTTopicPageCrawler crawler = new NYTTopicPageCrawler("http://topics.nytimes.com/top/reference/timestopics/people/s/olympia_j_snowe/index.html");
		NYTTopicPageCrawler crawler = new NYTTopicPageCrawler("http://topics.nytimes.com/top/reference/timestopics/people/s/olympia_j_snowe/index.html");
		//NYTTopicPageCrawler crawler = new NYTTopicPageCrawler("http://www.google.it");
		
		//"http://www.google.it"
		crawler.crawl();
	}
}
// Implement the call back class. Just like a SAX content handler
class CallBack extends HTMLEditorKit.ParserCallback {
	boolean DEBUG = false;
	
	boolean firstTable = false;
	boolean stop = false;
	
	boolean insideH5 = false;
	
	int row = 0;
	Tag latest = null;
	
	boolean blockName = false;
	
	ArrayList<String> articles = new ArrayList<String>();
	
	public void flush() throws BadLocationException{}
	
	public ArrayList<String> getArticles() {
		return articles;
	}
	public void handleComment(char[] data, int pos){}

	public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos)
	{
		if(DEBUG) System.out.println("Tag: " + tag );
		latest = tag;
		
		if(tag == Tag.H5){
			if(DEBUG) System.out.println("begin H5");
			insideH5 = true;
		}
		
		if(insideH5 && tag == Tag.A){
			Object attr = a.getAttribute(Attribute.HREF);
			System.out.println(attr);
			if(attr != null)
				articles.add(attr.toString());
		}
	}

	public void handleEndTag(HTML.Tag t, int pos){
		if(t == Tag.H5){
			if(DEBUG) System.out.println("end H5");
			insideH5 = false;
		}
	}
	public void handleSimpleTag(HTML.Tag t,MutableAttributeSet a, int pos){
	}
	public void handleError(String errorMsg, int pos){}
	public void handleEndOfLineString(String eol){}

	public void handleText(char[] data, int pos)
	{

	}
	
	
}

