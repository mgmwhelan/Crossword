import java.io.BufferedReader;
import java.io.FileReader;

public class Dictionary {
	
	private TST<String> d;

	/**************************************************************************
							DICTIONARY CONSTRUCTION
	**************************************************************************/

	public Dictionary(String filename) {
		d = new TST<String>(); 

		populateDictionary(filename); 
	}

	public Dictionary(String mainFilename, String preferredFilename) {
	}

	private void populateDictionary(String dictionaryFile) {
	    int searchIndex = 0; 
	    int nextIndex = 0; 
	    String line; 
	    String word;

	    try {
		    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile));
		   	while ((line = reader.readLine()) != null) { 

	   			searchIndex = line.indexOf(",", searchIndex);
		   		while (searchIndex >= 0) {
		   			nextIndex = line.indexOf(",", searchIndex+1); 

		   			if (nextIndex > 0) 
		   				word = line.substring(searchIndex+1, nextIndex);
		   			else
		   				word = line.substring(searchIndex+1);

		   			if (word.length() > 0) {
			   			d.put(word.toUpperCase(), ""); 
			   		}

		   			searchIndex = nextIndex;

		   		}
		   	}
		}
		catch (Exception e) {System.out.println("Error in populateDictionary: " + e); }
	}

	/**************************************************************************
							DICTIONARY ACCESS
	**************************************************************************/

	public int size() { return d.size();}

	public void add(String key) { d.put(key.toUpperCase(), ""); }

	public boolean contains(String key) { 
		return (d.contains(key.toUpperCase()));
	}

	public Iterable<String> keysThatMatch(String pattern) { 
		if (pattern == null) 
			return null; 

		return d.keysThatMatch(pattern.toUpperCase());
	}

	public Iterable<String> randomizedKeysThatMatch(String pattern) {
		RandomizedQueue<String> rand = new RandomizedQueue<String>(); 
		Queue<String> toReturn = new Queue<String>();
		int counter = 0; 

		for (String s : d.keysThatMatch(pattern.toUpperCase())) {
			rand.enqueue(s); 
			counter++; 
		}

		// add non-preferred words to queue
		while (counter > 0) {
			toReturn.enqueue(rand.dequeue());
			counter--;
		}

		return toReturn; 
	}

	public boolean keyThatMatches(String pattern) {

		if (pattern == null) 
			return false;

		return d.keyThatMatches(pattern.toUpperCase()); 
	}

	public void remove(String key) { d.remove(key.toUpperCase());	}

	public int numberOfKeysThatMatch(String pattern) {
		int toReturn = 0; 

		for (String s : d.keysThatMatch(pattern.toUpperCase())) 
			toReturn++; 

		return toReturn; 
	}

	public void printDictionary() {

		for (String s : d.keys()) 
			System.out.println(s); 
	}

	public static void main(String[] args) {

		Dictionary dict = new Dictionary(args[0]); 		

		System.out.println(dict.contains("deer"));

		System.out.println("Matches");
		for (String s : dict.keysThatMatch("d..."))
			System.out.println(s); 

		System.out.println("Matches2");
		for (String s : dict.keysThatMatch("z..."))
			System.out.println(s); 

		dict.printDictionary(); 

		System.out.println(dict.contains("ZWEI"));
		System.out.println(dict.contains("zwei"));

	}
}