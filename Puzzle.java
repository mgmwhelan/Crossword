import java.io.BufferedReader;
import java.io.FileReader;

public class Puzzle {
	private Grid grid; 
	private GeometricSolver geoS; 
	

	public Puzzle(String gridFile, String dictionaryFile) {

		grid = new Grid(gridFile); 
		Dictionary dict = new Dictionary(dictionaryFile);		
		geoS = new GeometricSolver(grid, dict); 

	}

	// public Puzzle(String gridFile, String dictionaryFile, String preferredDictionary) {

	// 	grid = new Grid(gridFile); 


	// 	PreferredDictionary dict = new PreferredDictionary(dictionaryFile, preferredDictionary);
	// 	Dictionary preferred = new Dictionary(preferredDictionary);
	// 	s = new UserSuggestedSolver(grid, dict, preferred); 
	// }


	public void solve() {
		geoS.solve(); 
	}


	public static void main(String[] args) {
		Puzzle p = new Puzzle(args[0], args[1]); 
		p.solve(); 
	}
}