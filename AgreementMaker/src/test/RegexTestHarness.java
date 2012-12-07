package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestHarness {

    public static void main(String[] args) throws IOException{
        //Console console = System.console();
    	InputStreamReader console1 = new InputStreamReader(System.in);
        BufferedReader console = new BufferedReader(console1);
        /*if (console == null) {
            System.err.println("No console.");
            System.exit(1);
        }*/
        while (true) {

        	System.out.print("%nEnter your regex: ");
        	String regex = console.readLine();
            Pattern pattern = 
            Pattern.compile(regex);
            
            System.out.print("Enter input string to search: ");
            String text = console.readLine();
            Matcher matcher = 
            pattern.matcher(text);

            boolean found = false;
            while (matcher.find()) {
                System.out.format("I found the text \"%s\" starting at " +
                   "index %d and ending at index %d.%n",
                    matcher.group(), matcher.start(), matcher.end());
                found = true;
            }
            if( found) {
            	System.out.println("Trying to replace:");
            	System.out.println( text.replace(regex, "\\\'"));
            }
            if(!found){
                System.out.format("No match found.%n");
            }
        }
    }
}
