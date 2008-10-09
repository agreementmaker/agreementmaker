package test;




  // Need to import our wordnet API: 
  import edu.gwu.wordnet.DictionaryDatabase;
import edu.gwu.wordnet.FileBackedDictionary;
import edu.gwu.wordnet.IndexWord;
import edu.gwu.wordnet.POS;
import edu.gwu.wordnet.Synset;
import edu.gwu.wordnet.Word;

  public class JWordNetTest {

    public static void main (String[] args) 
    {
      // Load Dictionary 
      DictionaryDatabase dictionary = new FileBackedDictionary("wordnetdata");

      // Look up words relating to "hello" 
      IndexWord word = dictionary.lookupIndexWord(POS.NOUN, "man");
      Synset[] senses = word.getSenses();
      int taggedCount = word.getTaggedSenseCount();
  
      // Explore related words. 
      for (int i=0; i < senses.length; i++) {   

        Synset sense = senses[i];

        // Print Synset Description 
        System.out.println((i+1) + ". " + sense.getLongDescription());

        // Print words in Synset 
        Word[] printWords = sense.getWords(); 
        for (int j=0; j < printWords.length; j++) {
          System.out.println (printWords[j].getLemma());
        }
        System.out.println("");

      } // end-outer-for 
    }

  } // end-class 


