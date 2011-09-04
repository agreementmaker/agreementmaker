package main;

import matching.NYTInstanceMatcher;
import misc.NYTConstants;

public class Main {

	public static void main(String[] args) throws Exception {
		NYTInstanceMatcher matcher = new NYTInstanceMatcher(NYTConstants.NYT_PEOPLE, NYTConstants.DBP_PERSON);
		matcher.match();
	}

}
