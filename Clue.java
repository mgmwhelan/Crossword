public class Clue {
	
	private int x; // x position of start
	private int y; // y position of start
	private Direction dir; 
	private int length;  // full length of the clue
	private int idNum; //identifying number, e.g. 48 in "48 Across"
	private String fill; 


	public Clue(int x, int y, Direction dir, int length, int idNum) {
		this.x = x; 
		this.y = y; 
		this.dir = dir; 
		this.length = length; 
		this.idNum = idNum; 
		fill = "";

		for (int i = 0; i < length; i++) 
			fill = fill + ".";
	}

	public int x() {return x;}
	public int y() {return y;} 
	public int length() {return length; }
	public int idNum() {return idNum; }
	public Direction dir() {return dir; }
	public String fill() { return fill; }
	
	// returns true iff no blank spaces left in clue
	public boolean isFull() {
		for (int i = 0; i < length; i++) {
			if (fill.charAt(i) == '.')
				return false; 
		}
		return true; 
	}

	// returns true iff at least part of the clue has been filled in
	public boolean hasFill() {
		for (int i = 0; i < length; i++) {
			if (fill.charAt(i) != '.')
				return true; 
		}
		return false; 
	}

	// returns true iff no letters have been filled in
	public boolean isEmpty() {
		for (int i = 0; i < length; i++) {
			if (fill.charAt(i) != '.')
				return false; 
		}
		return true; 
	}

	public boolean fillClue(String value) {
		if (value.length() != length) 
			return false; 
		fill = value; 
		return true; 
	}


	// print clue
	public void print() { System.out.println(idNum + " " + convertEnumToString()); }

	public void printXandYCoords() {
		System.out.println(idNum + " " + convertEnumToString() + ", (" + x + "," + y + ")");
	}

	public void printFull() {
		System.out.print(idNum + " " + convertEnumToString());
		System.out.print(", (" + x + "," + y + ")");
		System.out.print(", len: " + length); 
		System.out.print(", " + fill);
		System.out.println(); 
	}

	public void printFill() { System.out.println(fill); }

	private String convertEnumToString() {
		switch (dir) {
		case ACROSS:
			return "Across";
		case DOWN:
			return "Down";
		}
		return "Invalid Direction";
	}
}