package game.pieces;

import logic.moveChecking.PieceVisitor;

/**
 * Guard Piece
 */
public class Guard extends Piece {

    public Guard(boolean place) {
        super(place);
        this.type = "Guard";
    }

	@Override
	public <T> T accept(PieceVisitor<T> visitor) {
		return visitor.visit(this);
	}
}