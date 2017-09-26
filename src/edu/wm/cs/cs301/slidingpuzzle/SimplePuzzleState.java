package edu.wm.cs.cs301.slidingpuzzle;

import java.util.Arrays;
import java.util.LinkedList;

import edu.wm.cs.cs301.slidingpuzzle.PuzzleState.Operation;

public class SimplePuzzleState implements PuzzleState {

	//stores values of table in two dimensional array
	private int[][] valueTable;
	//stores last move done on a piece
	private Operation operation;
	//stores last state of the puzzle
	private PuzzleState parent;
	//stores how far the puzzle has gone since birth
	private int pathLength;
	
	//Initialize objects
	public SimplePuzzleState() {
		super();
		this.valueTable = null;
		this.operation = null;
		this.parent = null;
		this.pathLength = 0;
	}
	
	//Store objects here
	public SimplePuzzleState(int[][] valueTable, Operation operation, PuzzleState parent, int pathLength) {
		this.valueTable = valueTable;
		this.operation = operation;
		this.parent = parent;
		this.pathLength = pathLength;
	}
	
	@Override
	public void setToInitialState(int dimension, int numberOfEmptySlots) {
		// TODO Auto-generated method stub
		
		//fill the table with the dimensions given
		this.valueTable = new int[dimension][dimension];
		
  		//determine how many slots there are in this table
  		int slots = dimension * dimension;
  		//determine how many slots will NOT be free 
  		int activeSlots = slots - numberOfEmptySlots;
  		//for every spot in the table possible...
  		for (int s=1; s<=slots; s++) {
  			//go down each row...
  			for(int r=0; r<dimension; r++) {
  				//...and then each column...
  				for(int c=0; c<dimension; c++) {
  					//and at each place in the column, enter a value
  					//if we have an active slot, use it 
  					if (activeSlots > 0) {
  						this.valueTable[r][c] = s; 
	  					activeSlots--;
	  				//if we don't, enter 0, it's a free slot
  					} else {
  						this.valueTable[r][c] = 0;
  					}
  					//increase slot considertion
  					s++;
  				}
  			}
  		}
	}

	@Override
	public int getValue(int row, int column) {
		// TODO Auto-generated method stub
		//if it is at all possible to retrieve a value with the given row and column... 
		if (row >= 0 && column >= 0 && row < valueTable.length && column < valueTable.length){
			//return the value at the row and column
			return valueTable[row][column];
		}
		//just return -1 in the event we get bad news
		return -1;
	}

	@Override
	public PuzzleState getParent() {
		// TODO Auto-generated method stub
		//just return current parent
		return this.parent;
	}

	//auto-generated
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(valueTable);
		return result;
	}

	//auto-generated
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePuzzleState other = (SimplePuzzleState) obj;
		if (!Arrays.deepEquals(valueTable, other.valueTable))
			return false;
		return true;
	}

	@Override
	public Operation getOperation() {
		// TODO Auto-generated method stub
		
		//return this.operation
		return this.operation;
	}

	@Override
	public int getPathLength() {
		// TODO Auto-generated method stub
		//return the path length 
		return this.pathLength;
	}

	@Override
	public PuzzleState move(int row, int column, Operation op) {
		// TODO Auto-generated method stub
		
		//if space clicked is free, don't move
		if (getValue(row, column) == 0) {
			return null;
		}
		
		//spawn a new table to mess with
		int[][] newTable = new int[valueTable.length][valueTable[0].length];
		//for every row
		for (int r = 0; r < valueTable.length; r++) {
			//for every column
			for (int c = 0; c < valueTable[0].length; c++) {
				//copy the values
				newTable[r][c] = valueTable[r][c];
			}
		}
		
		//check if moving right is possible first
		switch (op) {
		//try right move
		case MOVERIGHT:
			//if the move is not out of bounds ...
			if (column + 1 < valueTable.length) {
				//if the space is free...
				if (isEmpty(row, column + 1)) {
					//the move is good, allow it
					//store the old value temporarily
					int temporary = valueTable[row][column];
					//set the space we're moving from to be free
					newTable[row][column] = 0;
					//set the space we're moving towards to the old value
					newTable[row][column + 1] = temporary;
					//increase the next state's pathlength too
					//pathLength = pathLength + 1;
					//make the next state's operation right move
					break;
				//if its not free, not good
				} else {
					return null;
				}
			//if its out of bounds, not good
			} else {
				return null;
			}
		//so on and so forth with left, down, and up
		case MOVELEFT:
			if (column - 1 >= 0) {
				if (isEmpty(row, column - 1)) {
					int temporary = valueTable[row][column];
					newTable[row][column] = 0;
					newTable[row][column - 1] = temporary;
				} else {
					return null;
				}
			} else {
				return null;
			}
			break;
		case MOVEUP:
			if (row - 1 >= 0){
				if (isEmpty(row - 1, column)){
					int temporary = valueTable[row][column];
					newTable[row][column] = 0;
					newTable[row - 1][column] = temporary;
				} else {
					return null;
				}
			} else {
				return null;
			}
			break;
		case MOVEDOWN:
			if (row + 1 >= 0) {
				if (isEmpty(row + 1, column)){
					int temporary = valueTable[row][column];
					newTable[row][column] = 0;
					newTable[row + 1][column] = temporary;
				} else {
					return null;
				}
			} else {
				return null;
			}
			break;
		default:
			break;
		}
		//return new state with updated moves
		return new SimplePuzzleState(newTable, op, this, this.pathLength + 1);
	}

	@Override
	public PuzzleState drag(int startRow, int startColumn, int endRow, int endColumn) {
		// TODO Auto-generated method stub
		
		//this time lets just spawn another state with the same stuff as our current state, for a change
		PuzzleState nextState = new SimplePuzzleState();
		//same stuff as current state, please
		nextState = this;
		
		//need to know how far away in rows and columns we are from where we want to go
		int difference_in_row = (endRow + 1) - (startRow + 1);
		int difference_in_column = (endColumn + 1) - (startColumn + 1);
		
		//while we're not where we want to arrive...
		while (difference_in_row != 0 || difference_in_column != 0) {
			//if where we're pulling is empty, can't do it
			if (isEmpty(startRow, startColumn)) {
				return null;
			}
			//if where we're pushing is not empty, can't do it
			if (!isEmpty(endRow, endColumn)) {
				return null;
			}
			//move to the right
			if (isEmpty(startRow, startColumn + 1) && difference_in_column > 0) {
				//make next state the next move
				nextState = nextState.move(startRow, startColumn, Operation.MOVERIGHT);
				//increment here so we're closer to where we want to go
				startColumn++;
				//decrement here so we're closer to where we want to go
				difference_in_column--;;
				//recursively call drag again, because it wont run after we arrive where we're going
				return nextState.drag(startRow, startColumn, endRow, endColumn);
			}
			//move left, so on and so forth
			if (isEmpty(startRow, startColumn - 1) && difference_in_column < 0) {
				nextState = nextState.move(startRow, startColumn, Operation.MOVELEFT);
				startColumn--;
				difference_in_column++;
				return nextState.drag(startRow, startColumn, endRow, endColumn);
			}
			//move down
			if (isEmpty(startRow + 1, startColumn) && difference_in_row > 0) {
				nextState = nextState.move(startRow, startColumn, Operation.MOVEDOWN);
				startRow++;
				difference_in_row--;
				return nextState.drag(startRow, startColumn, endRow, endColumn);
			}
			//move up
			if (isEmpty(startRow - 1, startColumn) && difference_in_row < 0) {
				nextState = nextState.move(startRow, startColumn, Operation.MOVEUP);
				startRow--;
				difference_in_row++;
				return nextState.drag(startRow, startColumn, endRow, endColumn);
			}
		}
		//just return ourselves since we know from recursion we're where we want to be
		return this;
	}

	@Override
	public PuzzleState shuffleBoard(int pathLength) {

		// TODO Auto-generated method stub
		
		//same thing that we did in drag
		PuzzleState nextState = new SimplePuzzleState();
		nextState = this;
		
		//while path length is greater than 0
		while (pathLength > 0) {
			//while we're not broken...
			while (true) {
				//use linked lists to collect and store where all the free spaces are
				LinkedList<Integer> freeRows = new LinkedList<Integer>();
				LinkedList<Integer> freeColumns = new LinkedList<Integer>();
				for (int r = 0; r < valueTable.length; r++) {
					//for every column and row...
					for (int c = 0; c < valueTable.length; c++) {
						if (isEmpty(r,c)) {
							freeRows.add(r);
							freeColumns.add(c);
						}
					}
				}
				//pick a number from the amount of free spaces i have to determine which free space im going to manipulate
				int randomFreeSpace = (int) Math.floor(Math.random() * freeRows.size());
				//get the random Row now
				int startRow = freeRows.get(randomFreeSpace);
				//along with the matching random Column
				int startColumn = freeColumns.get(randomFreeSpace);
				//get me a random direction to move in
				int random = (int) Math.floor(Math.random() * 4);
				//until further notice, a new operation is null, to be set at a later date
				Operation newOperation = null;
				//consideration for each possible move
				if (random == 0) {
					newOperation = Operation.MOVEUP;
					startRow++;
				} else if (random == 1) {
					newOperation = Operation.MOVEDOWN;
					startRow--;
				} else if (random == 2) {
					newOperation = Operation.MOVELEFT;
					startColumn++;
				} else if (random == 3) {
					newOperation = Operation.MOVERIGHT;
					startColumn--;
				}
				
				//all this below ensures we do not randomly go back where we once were
				if (this.getOperation() == Operation.MOVEDOWN && newOperation == Operation.MOVEUP) {
					//dont let it move up then
					continue;
				} else if (this.getOperation() == Operation.MOVEUP && newOperation == Operation.MOVEDOWN) {
					continue;
				} else if (this.getOperation() == Operation.MOVERIGHT && newOperation == Operation.MOVELEFT) {
					continue;
				} else if (this.getOperation() == Operation.MOVELEFT && newOperation == Operation.MOVERIGHT) {
					continue;
				}
				
				//if what we're about to mess with is not in range
				if (startRow < 0 || startRow >= valueTable.length || startColumn < 0 || startColumn >= valueTable.length) {
					//send the while loop back again, get a different value
					continue;
				}
				//if what we're about to mess with is another fellow empty space
				if (isEmpty(startRow, startColumn)) {
					//send it back again ....
					continue;
				}
				//we're assuming we've caught everything, and that what we're about to mess with is good
				PuzzleState result = this.move(startRow, startColumn, newOperation);
				nextState = result;
				//break out of the while loop
				break;
			}
			//recursively call ourselves again, decrementing by 1
			return nextState.shuffleBoard(pathLength - 1);
		}
		//we return ourselves since we trust that the aforementioned recursion has brought us to where we'd like to be 
		return this;
	}

	@Override
	public boolean isEmpty(int row, int column) {
		// TODO Auto-generated method stub
		//make sure the block being asked for is NOT inexistent
		if (row >= 0 && column >= 0 && row < valueTable.length && column < valueTable.length){	
			//check if its free or not
			if (this.valueTable[row][column] == 0) {
				//say its free
				return true;
			}
		}
		//otherwise, its not
		return false;
	}

	@Override
	public PuzzleState getStateWithShortestPath() {
		// TODO Auto-generated method stub
		//honestly took a stab that this might be it, and it was
		return this;
	}
}
