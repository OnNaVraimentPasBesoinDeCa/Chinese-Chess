package logic.moveChecking;

import java.util.ArrayList;
import game.Board;
import game.pieces.Canon;
import game.pieces.Chariot;
import game.pieces.Elephant;
import game.pieces.General;
import game.pieces.Guard;
import game.pieces.Horse;
import game.pieces.Piece;
import game.pieces.Soldier;

public class GraphicalPointVisitor implements PieceVisitor<ArrayList<Integer[]>>{
	// Reminder: (0,0) is top-left (lowering y goes up, lowering x goes left)

	private ArrayList<Integer[]> legalMoves;
	private Board currentBoard;
	private int pieceX, pieceY;

	public GraphicalPointVisitor() { }

	/**
	 * Resets the possible legal moves for the piece. Avoids having multiple movesets.
	 * Also gets the coordinates of the piece
	 */
	public void init(Piece piece) {
		legalMoves = new ArrayList<Integer[]>();
		pieceX = piece.getX();
		pieceY = piece.getY();
	}

	/**
	 * Update the board whenever a move has been made
	 * @param newBoard The new state of the board
	 */
	public void updateBoard(Board newBoard) {
		currentBoard = newBoard;
	}
	
	/**
	 * Returns true if the specified square actually exists and false if it is out of bounds
	 * @param x The x coordinate of the specified square
	 * @param y The y coordinate of the specified square
	 * @return Boolean
	 */
	public Boolean isInBound(int x, int y) {
		Boolean xInRange = (x > -1 && x < 11);
		Boolean yInRange = (y > -1 && y < 11);
		return xInRange && yInRange;
	}

	/**
	 * Returns true if the specified square is empty and false if it isn't
	 * @param x The x coordinate of the specified square
	 * @param y The y coordinate of the specified square
	 * @return Boolean
	 */
	public Boolean isEmpty(int x, int y) {
		return (currentBoard.getPiece(x, y) == null);
	}
	
	/**
	 * Returns true if the specifed square has an opponent piece
	 * @param piece The piece being moved
	 * @param x The x coordinate of the specified square
	 * @param y The y coordinate of the specified square
	 * @return Boolean
	 */
	public Boolean isEdible(Piece piece, int x, int y) {
		Piece attackedPiece = currentBoard.getPiece(x,y);
		return (piece.isBlack() != attackedPiece.isBlack());
	}
	
	/**
	 * Add a specified square to the list of legal moves if it the piece, upon moving to said square:
	 * <ul>
	 * 		<li>Is not off the board (i.e. it stays within bounds), AND</li>
	 * 		<ul>
	 * 			<li>Is unoccupied by a piece, OR</li>
	 * 			<li>Eats an opponent piece</li>
	 * 		</ul>
	 * </ul>
	 * @param piece The piece being moved
	 * @param x The x coordinate of the specified square
	 * @param y The y coordinate of the specified square
	 */
	public void addIfLegal(Piece piece, int x, int y) {
		
		// Normally isEdible shouldn't ever throw an outOfBounds error
		// Because isEmpty will be true before that happens
		if(isInBound(x,y) && (isEmpty(x,y) || isEdible(piece, x,y)) ) {
			addLegal(x,y);
		}
	}

	/**
	 * Adds the specified square to the list of legal moves
	 * @param x The x coordinate of the specified square
	 * @param y The y coordinate of the specified square
	 */
	public void addLegal(int x, int y) {
		Integer[] legalPosition = {x, y};
		legalMoves.add(legalPosition);
	}

	/**
	 * Checks if the squares immediately left and right of the specified square is empty.
	 * If it is, it adds the square to the list of legal moves
	 * @param piece The piece we're moving
	 * @param x The x coordinate of the specified square
	 * @param y The y coordinate of the specified square
	 */
	public void checkLeftAndRight(Piece piece, int x, int y) {
		addIfLegal(piece, x+1, y);
		addIfLegal(piece, x-1, y);
	}

	@Override
	public ArrayList<Integer[]> visit(Canon piece) {
		init(piece);

		int posY = pieceY;
		
		// Checking vertical movement //
		
		// Java 'reads' left to right, therefore posY -= 1 will be the first thing to be done
		// Therefore isUnoccupied's first call will be with parameters (pieceX, pieceY-1)
		// And will stop once posY is less than 0, avoiding an outOfBoundsError
		// Because -1 >= 0 will be false before the isUnoccupied call
		while(--posY >= 0 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		// Cannon has extra move: it can only eat hopping over a piece
		while(--posY >= 0 && isEmpty(pieceX, posY)) { }
		addIfLegal(piece, pieceX, posY);

		posY = pieceY;
		while(++posY <= 10 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		while(++posY <= 10 && isEmpty(pieceX, posY)) { }
		addIfLegal(piece, pieceX, posY);

		// Checking horizontal movement //
		int posX = pieceX;
		while(--posX >= 0 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		while(--posX >= 0 && isEmpty(posX, pieceY)) { }
		addIfLegal(piece, posX, pieceY);

		posX = pieceX;
		while(++posX <= 10 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		while(++posX <= 10 && isEmpty(posX, pieceY)) { }
		addIfLegal(piece, posX, pieceY);
		
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Chariot piece) {
		init(piece);

		// Checking vertical movement
		int posY = pieceY;
		while(--posY >= 0 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		addIfLegal(piece, pieceX, posY); // We should also be able to eat the opponent

		posY = pieceY;
		while(++posY <= 10 && isEmpty(pieceX, posY)) {
			addLegal(pieceX, posY);
		}
		addIfLegal(piece, pieceX, posY);

		// Checking horizontal movement
		int posX = pieceX;
		while(posX-- >= 0 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		addIfLegal(piece, posX, pieceY);

		posX = pieceX;
		while(posX++ <= 10 && isEmpty(posX, pieceY)) {
			addLegal(posX, pieceY);
		}
		addIfLegal(piece, posX, pieceY);
		
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Elephant piece) {
		init(piece);
		addIfLegal(piece, pieceX+2, pieceY+2);
		addIfLegal(piece, pieceX+2, pieceY-2);
		addIfLegal(piece, pieceX-2, pieceY+2);
		addIfLegal(piece, pieceX-2, pieceY-2);
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(General piece) {
		init(piece);
		addIfLegal(piece, pieceX, pieceY+1);
		addIfLegal(piece, pieceX, pieceY-1);
		addIfLegal(piece, pieceX+1, pieceY);
		addIfLegal(piece, pieceX-1, pieceY);
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Guard piece) {
		init(piece);
		addIfLegal(piece, pieceX+1, pieceY+1);
		addIfLegal(piece, pieceX+1, pieceY-1);
		addIfLegal(piece, pieceX-1, pieceY+1);
		addIfLegal(piece, pieceX-1, pieceY-1);
		return legalMoves;
	}

	@Override
	public ArrayList<Integer[]> visit(Horse piece) {
		init(piece);

		// Since a horse moves straight then diagonally, check whether it can first move straight
		if(isInBound(pieceX+1, pieceY) && isEmpty(pieceX+1, pieceY)) { //right
			int tempX = pieceX+2;
			addIfLegal(piece, tempX, pieceY+1);
			addIfLegal(piece, tempX, pieceY-1);
		}

		if(isInBound(pieceX-1, pieceY) && isEmpty(pieceX-1, pieceY)) { //left
			int tempX = pieceX-2;
			addIfLegal(piece, tempX, pieceY+1);
			addIfLegal(piece, tempX, pieceY-1);
		}

		if(isInBound(pieceX, pieceY+1) && isEmpty(pieceX, pieceY+1)) { //down
			int tempY = pieceY+2;
			checkLeftAndRight(piece, pieceX, tempY);
		}

		if(isInBound(pieceX, pieceY-1) && isEmpty(pieceX, pieceY-1)) { //up
			int tempY = pieceY-2;
			checkLeftAndRight(piece, pieceX, tempY);
		}

		return legalMoves;
	}
	
	@Override
	public ArrayList<Integer[]> visit(Soldier piece) {
		init(piece);

		if(piece.isBlack()) {
			addIfLegal(piece, pieceX, pieceY+1);
			if(pieceY > 5) { // We're on Red's side, so we can move sideways
				checkLeftAndRight(piece, pieceX, pieceY);
			}
		} else {
			addIfLegal(piece, pieceX, pieceY-1);
			if(pieceY < 5) { // We're on Black's side, so we can move sideways
				checkLeftAndRight(piece, pieceX, pieceY);
			}
		}
		return legalMoves;
	}

}
