import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class Grid {

	private int N; 
	private boolean[][] grid; 
	private Hashtable<Integer,Clue> acrossClues; 
	private Hashtable<Integer,Clue> downClues; 
	private Clue[][] acrossByCoord; 
	private Clue[][] downByCoord; 
	private int numDown; 
	private int numAcross; 


	/**************************************************************************
							GRID CONSTRUCTION
	**************************************************************************/
	public Grid(String filename) {


		readGridFromFile(filename); //read grid geometry in from a file

		numAcross = 0; 
		numDown = 0; 
		acrossClues = new Hashtable<Integer,Clue>();
		downClues = new Hashtable<Integer,Clue>();
		acrossByCoord = new Clue[N][N];
		downByCoord = new Clue[N][N];
		createAllClues(); // find start of all across and down clues
	}

	private void createAllClues() {
		int currClue = 1; 
		boolean newClue = false; 

		for (int y = 0; y < N; y++) {
			for (int x = 0; x < N; x++) {
				if (grid[x][y]) {
					newClue = addAcrossClues(x, y, currClue); 
					newClue = newClue | addDownClues(x, y, currClue); 
					if (newClue)
						currClue++;
				}
				newClue = false; 
			}
		}
	}

	private boolean addAcrossClues(int x, int y, int currClue) {
		boolean newClueAdded = false; 

		if (x - 1 < 0)
			newClueAdded = true; 
		else if (!grid[x-1][y]) 
			newClueAdded = true; 

		if (!newClueAdded) return false; 

		addClue(x, y, currClue, Direction.ACROSS); 
		return newClueAdded;
	}

	private boolean addDownClues(int x, int y, int currClue) {
		boolean newClueAdded = false; 

		if (y - 1 < 0) 
			newClueAdded = true; 
		else if (!grid[x][y-1]) 
			newClueAdded = true; 

		if (!newClueAdded) return false; 

		addClue(x, y, currClue, Direction.DOWN); 
		return newClueAdded;
	}

	private void addClue(int x, int y, int currClue, Direction dir) {
		int clueLength; 
		Clue newClue; 

		clueLength = findClueLength(x, y, dir); 

		newClue = new Clue(x, y, dir, clueLength, currClue);

		if (dir == Direction.ACROSS) {
			acrossClues.put((Integer)currClue, newClue);
			numAcross++; 
		}
		else {
			downClues.put((Integer)currClue, newClue);
			numDown++; 
		}

		indexClue(newClue); 
	}

	// adds entries to downByCoord and acrossByCoord whenever a new clue is created
	private void indexClue(Clue c) {

		if (c.dir() == Direction.ACROSS) {
			for (int i = 0; i < c.length(); i++) {
				acrossByCoord[c.x() + i][c.y()] = c; 
			}
		}
		else {
			for (int i = 0; i < c.length(); i++) {
				downByCoord[c.x()][c.y() + i] = c;
			}
		}
	}

	private int findClueLength(int x, int y, Direction dir) {
		int clueLength = 1; 

		if (dir == Direction.ACROSS) {
			while ((x + clueLength < N) && (grid[x+clueLength][y])) 
				clueLength++; 
		}
		else {
			while ((y + clueLength < N) && (grid[x][y+clueLength])) 
				clueLength++; 
		}
		return clueLength; 
	}

	private void readGridFromFile(String filename) {
		String line; 
		int currLine = 0; 

	    try {
		    BufferedReader reader = new BufferedReader(new FileReader(filename));
		    line = reader.readLine(); 
		    N = Integer.parseInt(line.trim()); 
		    grid = new boolean[N][N]; 

		   	while ((line = reader.readLine()) != null) { 
		   		for (int i = 0; i < N; i++) {
		   			grid[i][currLine] = (line.charAt(i) == 'O');
		   		}
		   		currLine++; 
		   	}
		}
		catch (Exception e) { System.out.println("Error in readGridFromFile: " + e); }
	}
	/**************************************************************************
							GRID ACCESS
	**************************************************************************/
	public int N() { return N; }

	public boolean isOpenCell(int x, int y) { return grid[x][y]; }

	public int numDownClues() { return numDown; }

	public int numAcrossClues() { return numAcross; }

	public int numClues() { return numDown + numAcross; }

	public Iterable<Clue> getDownClues() { return downClues.values(); }

	public Iterable<Clue> getAcrossClues() { return acrossClues.values(); }

	public Clue getDownClueAt(int x, int y) { return downByCoord[x][y]; }

	public Clue getAcrossClueAt(int x, int y) { return acrossByCoord[x][y]; }

	public int getPosInDownClue(int x, int y, Clue c) { return (y - c.y()); }

	public int getPosInAcrossClue(int x, int y, Clue c) { return (x - c.x()); }

	public Clue getDownClueWithId(int id) { return downClues.get(id); }

	public Clue getAcrossClueWithId(int id) { return acrossClues.get(id); }

	public boolean isClueUntouched(Clue c) {

		if (!c.isEmpty())
			return false; 

		for (Clue intersect : interSectingClues(c))
			if (!intersect.isEmpty())
				return false; 

		return true;
	}

	// return all clues that intersect C, and all clues that intersect those clues
	public Iterable<Clue> affectingClues(Clue c) {
		Queue<Clue> toReturn = new Queue<Clue>(); 

		for (Clue c1 : interSectingClues(c)) {
			toReturn.enqueue(c1); 
			for (Clue c2 : interSectingClues(c1)) 
				toReturn.enqueue(c2); 
		}

		return toReturn;
	}

	// return all clues that intersect c 
	public Iterable<Clue> interSectingClues(Clue c) {

		if (c.dir() == Direction.ACROSS) 
			return interSectingAcrossClue(c);
		return interSectingDownClue(c);
	}

	// return all across clues that intersect down Clue c
	private Iterable<Clue> interSectingDownClue(Clue c) {
		Queue<Clue> toReturn = new Queue<Clue>(); 

		for (int i = 0; i < c.length(); i++) {
			toReturn.enqueue(acrossByCoord[c.x()][c.y() + i]);
		}

		return toReturn; 
	}

	// return all down clues that insersect across Clue c
	private Iterable<Clue> interSectingAcrossClue(Clue c) {
		Queue<Clue> toReturn = new Queue<Clue>(); 

		for (int i = 0; i < c.length(); i++) {
			toReturn.enqueue(downByCoord[c.x() + i][c.y()]);
		}

		return toReturn; 
	}

	public void fillClue(Clue c, String newFill) {
		c.fillClue(newFill); 
		updateInterSectingClues(c); 
	}

	// this function is meant to be called when Clue C was recently filled
	public void updateInterSectingClues(Clue c) {


		if (c.dir() == Direction.ACROSS) 
			updateInterSectingDownClues(c);
		else
			updateInterSectingAcrossClues(c);
	}

	// Clue c is a DOWN clue
	private void updateInterSectingAcrossClues(Clue c)  {

		for (int i = 0; i < c.length(); i++) {
			Clue intClue = getAcrossClueAt(c.x(), c.y() + i);

			int atPos = c.x() - intClue.x();

			
			intClue.fillClue(newFill(intClue.fill(), c.fill().charAt(i), atPos)); 

		}
	}

	// Clue c is an ACROSS clue
	private void updateInterSectingDownClues(Clue c) {
		
		for (int i = 0; i < c.length(); i++) {

			// System.out.println(c.x() + ", " + c.y() + ", " + i);
			Clue intClue = getDownClueAt(c.x() + i, c.y());
			
			int atPos = c.y() - intClue.y(); 
			// intClue.printFull(); 
			intClue.fillClue(newFill(intClue.fill(), c.fill().charAt(i), atPos)); 
		}
	}

	// Fill that results from placing newChar at position atPos in oldFill
	private String newFill(String oldFill, char newChar, int atPos) {
		String toReturn = "";

		// System.out.println(oldFill + ", " + newChar + ", " + atPos);


		// if atPos does not correspond to first character, append all of 
		// oldFill before atPos
		if (atPos > 0) 
			toReturn = oldFill.substring(0, atPos);

		// append newChar 
		toReturn = toReturn + newChar;

		if (atPos < oldFill.length() - 1) 
			toReturn = toReturn + oldFill.substring(atPos + 1, oldFill.length()); 

		return toReturn; 
	}

	// returns true iff there are no blank spaces on the grid
	public boolean isFull() {

		for (Clue c : getDownClues()) 
			if (!c.isFull()) 
				return false; 

		for (Clue c : getAcrossClues()) 
			if (!c.isFull()) 
				return false; 

		return true; 
	}
		
	/**************************************************************************
							GRID PRINTING
	**************************************************************************/

	private String[][] fillGridHelper() {

		String[][] toReturn = new String[N][N];

		for (Clue c : acrossClues.values()) {
			for (int i = 0; i < c.length(); i++) {
				toReturn[c.x() + i][c.y()] = "" + c.fill().charAt(i); 
			}
		}

		return toReturn;
	}

	public void printFilledGrid() {

		System.out.println(); 
		String[][] fill = fillGridHelper();

		try {
			PrintStream out = new PrintStream(System.out, true, "UTF-8");
			String closed = "\u25A0";
			String open = "\u25A1";

			for (int y = 0; y < N; y++) {
				for (int x = 0; x < N; x++) {
					if (grid[x][y]) {

						if (!fill[x][y].equals(".")) 
							out.print(fill[x][y] + " ");
						else 
							out.print(open + " "); 
					}
					else
						out.print(closed + " ");
				}
				System.out.println(); 
			}
		}
		catch (Exception e) {System.out.println(e);}
	}

	public void printGrid()  {

		try {
			PrintStream out = new PrintStream(System.out, true, "UTF-8");
			String closed = "\u25A0";
			String open = "\u25A1";

			for (int y = 0; y < N; y++) {
				for (int x = 0; x < N; x++) {
					if (grid[x][y]) 
						out.print(open + " ");
					else
						out.print(closed + " ");
				}
				System.out.println(); 
			}
			System.out.println("Across clues: " + numAcross);
			System.out.println("Down clues: " + numDown); 
		}
		catch (Exception e) {System.out.println(e);}
	}

	// print all across clues, followed by down clues 
	public void printClues() {
		System.out.println();

		// print across clues first 
		for (Clue c : acrossClues.values()) 
			// c.print(); 
			c.printFull();

		System.out.println();
		// print down clues
		for (Clue c : downClues.values()) {
			// c.print(); 
			c.printFull();
		}
	}

	public void printFilledAcrossClues() {
		System.out.println("Across:"); 

		for (Clue c : acrossClues.values()) {
			System.out.print(c.idNum() + " " ); 
			c.printFill();
		}
	}

	public void printFilledDownClues() {
		System.out.println("Down:");

		for (Clue c : downClues.values()) {
			System.out.print(c.idNum() + " " );
			c.printFill();
		}
	}

	public void printFilledClues() {
		printFilledAcrossClues(); 
		printFilledDownClues(); 
	}

	/**************************************************************************
							TESTING METHODS
	**************************************************************************/
	public void testFillClue() {
		if (N != 4)
			return; 

		Clue c = downClues.get(1); 

		printFilledGrid();
		fillClue(c, "test"); 


		printFilledGrid();

		Clue c2 = downClues.get(2); 
		fillClue(c2, "dogs"); 
		printFilledGrid();

		fillClue(downClues.get(3), "cats");
		printFilledGrid();

		printFilledClues(); 
	}

	/**************************************************************************
							MAIN METHOD
	**************************************************************************/
	public static void main(String[] args) {

		Grid g = new Grid(args[0]); 
		g.printGrid(); 

		g.printClues();
		// g.testFillClue(); 
	}
}