import java.util.HashSet;

public class HardestFirstSolver extends PuzzleSolver {
	
	private HashSet<Clue> unsolvedClues; 


	public HardestFirstSolver(Grid g, Dictionary d) {

		super(g, d); 

		unsolvedClues = new HashSet<Clue>(); 
	}

	private Clue pickFirstClue() {
		int minFill = Integer.MAX_VALUE; 
		int numPossibleFill = 0; 
		Clue nextHardest= null; 

		// get hardest clue
		for (Clue c : unsolvedClues) {

			String originalFill = c.fill(); 
			numPossibleFill = d.numberOfKeysThatMatch(originalFill); 

			if (numPossibleFill < minFill) {
				minFill = numPossibleFill; 
				nextHardest = c; 
			}
		}
		
		unsolvedClues.remove(nextHardest);
        return nextHardest;

	}

	public Clue nextOrderedClue(int currentClue) {

		System.out.println("finding hardest clue"); 
		Clue nextHardest= null; 
		int minFill = Integer.MAX_VALUE; 
		int numPossibleFill = 0; 

		if (currentClue == 0) 
			return pickFirstClue(); 

		System.out.println(unsolvedClues.size() + " unsolved clues");

		for (Clue c : unsolvedClues) {

			// create a special case for clues that have no fill
			// and no neighbors with fill
			if (g.isClueUntouched(c)) 
				continue; 

			String originalFill = c.fill(); 
			numPossibleFill = d.numberOfKeysThatMatch(originalFill); 

			if (numPossibleFill < minFill) {
				minFill = numPossibleFill; 
				nextHardest = c; 
			}
		}

		//since we are about to solve nextHardest, remove it from unsolved set
		unsolvedClues.remove(nextHardest); 
		return nextHardest; 

	}

	public void recordSolvedClues(Clue c, boolean solved) {
		if (solved) 
			unsolvedClues.remove(c); 
		else 
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