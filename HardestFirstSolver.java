public class HardestFirstSolver extends PuzzleSolver {
	
	private Grid g; 
	private Dictionary d; 
	private HashSet<String> fillAlreadyInPuzzle; 
	private HashSet<Clue> unsolvedClues; 


	public HardestFirstSolver(Grid g, Dictionary d) {

		super(g, d); 
		this.g = g; 
		this.d = d;		
		fillAlreadyInPuzzle = new HashSet<String>(); 
		unsolvedClues = new HashSet<Clue>(); 
	}

	public Clue nextOrderedClue(int currentClue) {
		Clue nextHardest; 
		int minFill = Integer.MAX_VALUE; 
		int numPossibleFill = 0; 

		for (Clue c : unsolvedClues) {

			numPossibleFill = 0; 
			for (String potentialFill : d.keysThatMatch(originalFill)) {
				if (isValidFill(c, potentialFill, c.fill()))
					numPossibleFill++; 
			}
			if (numPossibleFill < minFill) {
				minFill = numPossibleFill; 
				nextHardest = c; 
			}
		}

		//since we are about to solve nextHardest, remove it from unsolved set
		unsolvedClues.remove(nextHardest); 
		return nextHardest; 

	}

	public void resetClueData(Clue c, String newFill, String oldFill) {
		fillAlreadyInPuzzle.remove(potentialFill); 
		g.fillClue(currClue, originalFill); 
		unsolvedClues.add(c); 
	}

	public void orderClues() {
		
		for (Clue c : g.getDownClues()) 
			unsolvedClues.add(c); 
		for (Clue c : g.getAcrossClues()) 
			unsolvedClues.add(c); 

		return;
	}


}