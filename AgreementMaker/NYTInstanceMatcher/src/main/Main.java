package main;

import matching.NYTInstanceMatcher;
import misc.NYTConstants;

public class Main {

	public static void main(String[] args) {
		NYTInstanceMatcher matcher = new NYTInstanceMatcher(NYTConstants.NYT_PEOPLE, NYTConstants.DBPEDIA);
	}

}
