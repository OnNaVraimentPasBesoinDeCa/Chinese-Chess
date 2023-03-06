package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Chariot Piece
 */

public class Chariot extends Piece{

    public Chariot(boolean place, int x, int y) {
        super(place, x, y);
        this.type = "Chariot";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}