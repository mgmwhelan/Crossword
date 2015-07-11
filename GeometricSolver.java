import java.util.HashSet;
import java.util.Hashtable;

public class GeometricSolver {
	
	private Grid g; 
	private Dictionary d; 
	private HashSet<String> fillAlreadyInPuzzle; // keeps track of strings already entered in to the grid
	private HashSet<Clue> unsolvedClues;    // keep track of clues that have not been solved yet
	private HashSet<Clue> affected; 	// keeps track of clues that affect a clue that couldn't be solved 
	private Hashtable<Integer, Clue> solveOrder;  // keeps track of the order that we are solving clues in 
	private Hashtable<Clue, HashSet<Clue>> affectedByThisClue;  // keeps track of which clue added other clues to the affected set
	private int numSolved;  // keeps track of how many clues we have already solved 

	/**************************************************************************
							Constructor / Constructor helpers 
	**************************************************************************/
	public GeometricSolver(Grid g, Dictionary d) {

		this.g = g; 
		this.d = d; 

		fillAlreadyInPuzzle = new HashSet<String>(); 
		unsolvedClues = new HashSet<Clue>(); 
		affected = new HashSet<Clue>(); 
		affectedByThisClue = new Hashtable<Clue, HashSet<Clue>>(); 
		solveOrder = new Hashtable<Integer, Clue>(); 

		numSolved = 0; 

		populateUnsolvedClues(); 
	}


	private void populateUnsolvedClues() {
		
		// add all clues to unsolved set
		for (Clue c : g.getDownClues()) 
			unsolvedClues.add(c); 
		for (Clue c : g.getAcrossClues()) 
			unsolvedClues.add(c); 

		return;
	}

	/**************************************************************************
							NEXT CLUE TO SOLVE 
	**************************************************************************/
	private Clue pickFirstClue() {	

		Clue nextHardest= g.getAcrossClueWithId(1); 

        int begin = (int) g.numAcrossClues() / 3;  // Start at roughly the first 3rd of the across clues
        int i = 1;
        boolean haveStart = false;
        while (!haveStart) {
            for (Clue c : g.getAcrossClues()) {
                if (c.length() < 4 && i > begin && !haveStart) {  // Pick a clue with length < 6
                    nextHardest = c;
                    haveStart = true;
                    break;
                }
                i++;
            }
        }
        unsolvedClues.remove(nextHardest);
        return nextHardest;
	}

	public Clue nextOrderedClue(int currentClue) {
		int numPossibleFill = 0; 
		int minFill = Integer.MAX_VALUE; 
		Clue nextClue= null; 


		// special case for the first clue
		if (currentClue == 0) {
			nextClue = pickFirstClue(); 
			unsolvedClues.remove(nextClue); 
			solveOrder.put((Integer) numSolved, nextClue);
			numSolved++; 
			return nextClue; 
		}

		// special case, we are recursing back up 
		if (currentClue < numSolved) {
			nextClue = solveOrder.get((Integer) currentClue);
			unsolvedClues.remove(nextClue); 
			return nextClue;
		}


		// we are now about to solve a clue we haven't touched before 
		// iterate through unsolved clues 
		for (Clue c : unsolvedClues) {

			// create a special case for clues that have no fill (assumes puzzles are continuous)
			// and no neighbors with fill
			if (g.isClueUntouched(c)) 
				continue; 

			String originalFill = c.fill(); 
			numPossibleFill = d.numberOfKeysThatMatch(originalFill); 

			if (numPossibleFill < minFill) {
				minFill = numPossibleFill; 
				nextClue = c; 
			}
		}

		//since we are about to solve nextClue, remove it from unsolved set
		unsolvedClues.remove(nextClue); 
		solveOrder.put((Integer) numSolved, nextClue);
		numSolved++; 
		return nextClue; 
	}

	/**************************************************************************
							GRID FILLING LOGIC
	**************************************************************************/
	// solve the grid
	public void solve() {
		if (!solve(0)) 
			System.out.println("UNSOLVABLE");
		g.printFilledGrid();
	}

	public boolean solve(int currentClue) {

		boolean solved; 

		// base case: if we are trying to solve an extra clue, we have succeeded! 
		if (currentClue >= g.numClues()) 
			return true; 

		Clue currClue = nextOrderedClue(currentClue);

		// no clues left to solve
		if (currClue == null) 
			return true;  

		// no need to try adding any more to this clue
		if ((currClue.isFull()) && d.contains(currClue.fill())) {	
			return solve(currentClue + 1);
		}

		boolean clueSolved = false; 
		String originalFill = currClue.fill(); 


		// no potential fill
		if (!d.keyThatMatches(originalFill)) {
			recordSolvedClues(currClue, false); 
			return false;
		}

		for (String potentialFill : d.randomizedKeysThatMatch(originalFill)) {
		// for (String potentialFill : d.keysThatMatch(originalFill)) {

			if (isValidFill(currClue, potentialFill, originalFill)) {

				g.fillClue(currClue, potentialFill); 
				d.remove(potentialFill); 
				succeededInSolving(currClue); 


				fillAlreadyInPuzzle.add(potentialFill); 
				g.fillClue(currClue, potentialFill); 
				g.printFilledGrid();
				

				// MAKE RECURSIVE CALL
				clueSolved = solve(currentClue + 1); 


				if (!clueSolved) { 
					if (affected.contains(currClue)) 
						resetClueData(currClue, potentialFill, originalFill); 
					else
						return false; 
				}
				else {
					recordSolvedClues(currClue, true); 
					return true; 
				}
			}
		}

		recordSolvedClues(currClue, clueSolved); 
		return clueSolved; 
	}

	private void failedToSolve(Clue c) {

		HashSet<Clue> toAdd = new HashSet<Clue>(); 

		// add affecting clues
		for (Clue c1 : g.affectingClues(c)) {
			if (!unsolvedClues.contains(c1)) {
				affected.add(c1); 
				toAdd.add(c1); 
			}
		}

		affectedByThisClue.put(c, toAdd); 
	}

	private void succeededInSolving(Clue c) {

		HashSet<Clue> toRemove;

		if (affected.contains(c)) {
			affected.remove(c); 

			// remove from affected all clues that were put there by this clue
			toRemove = affectedByThisClue.get(c); 
			if (toRemove == null) 
				return; 

			for (Clue c1 : toRemove) 
				affected.remove(c1); 
		}
	}

	private void recordSolvedClues(Clue c, boolean solved) {

		if (solved) 
			unsolvedClues.remove(c); 
		else  {
			unsolvedClues.add(c); 
			failedToSolve(c); 
		}

	}

	private void resetClueData(Clue c, String potentialFill, String originalFill) {
		fillAlreadyInPuzzle.remove(potentialFill); 
		d.add(potentialFill);
		g.fillClue(c, originalFill); 
	}

		// takes in a clue, it's new fill, and determines if there will still be possible fill 
	// for intersecting clues
	public boolean isValidFill(Clue c, String newFill, String oldFill) {

		// puzzle already contains that fill
		if (fillAlreadyInPuzzle.contains(newFill)) 
			return false; 

		boolean newUniqueCrossFill = false; 

		// fill the clue
		g.fillClue(c, newFill);
		d.remove(newFill); 

		// for all intersecting clues 
		for (Clue inters : g.interSectingClues(c)) {

			// check that intersecting clue has potential fill that would be new to the puzzle
			newUniqueCrossFill = false; 

			// if this filled in intersecting clue, make sure it's valid
			if (inters.isFull()) {
				if (d.contains(inters.fill()) || (fillAlreadyInPuzzle.contains(inters.fill())))
					continue;
				else  {
					g.fillClue(c, oldFill);
					d.add(newFill); 
					// System.out.println("Nothing that matches " + inters.fill());					
					return false; 
				} 
			}


			if (!d.keyThatMatches(inters.fill())) {
				g.fillClue(c, oldFill);
				d.add(newFill); 
				return false; 
			}
		}

		// reset original values
		g.fillClue(c, oldFill);
		d.add(newFill); 
		return true; 
	}

	/**************************************************************************
							DEBUGGING							
	**************************************************************************/

	private void printAffected() {
		System.out.println("AFFECTED CLUES"); 

		for (Clue c : affected) 
			c.printFull(); 

		System.out.println();
	}

	private void printStackTrace() {
		System.out.println("STACK TRACE");

		for (int i = 0; i < numSolved; i++) {
			System.out.print(i + " "); 
			solveOrder.get(i).printFull(); 
		}

		System.out.println();
	}
}