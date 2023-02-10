package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import game1.Move;

/**
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GUI extends JPanel implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	private final int SCREENWIDTH = (int)size.getWidth();
	private final int SCREENHEIGHT = (int)size.getHeight();
	private int strokeWidth = 1;
	private Board board;
	private int mouseX,mouseY,mouseFinalX,mouseFinalY;
	private Piece Pieces[][];
	private Piece thePiece;
	

	// Looks visually pleasing if the board is centered
	// and tall enough to fit most of the screen
	// but not too big to take up all of the screen
	int margin = SCREENHEIGHT/18;
	int squareLength = (SCREENHEIGHT-margin*3)/11; // x3 to leave more space at bottom of screen
	int center = SCREENWIDTH/2;
	int leftMostPixel = (int) (center-squareLength*(5.5));
	
	public GUI(){
		board= new Board();
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//Image background
		try {
			String imgLocation = "images/wood-background.jpg";
			Image background = ImageIO.read(new File(imgLocation));
			int bgWidth = background.getWidth(null);

			if(bgWidth != SCREENWIDTH) {
				// Resize image if necessary
				String imgOut = "images/test1.png";
				ImageResizer.resize(imgLocation, imgOut, ((double)SCREENWIDTH) / ((double)bgWidth));
				File fileOut = new File(imgOut);
				background = ImageIO.read(fileOut);
				g.drawImage(background,0,0,null);
				fileOut.delete();
			}

			else {
				g.drawImage(background,0,0,null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Drawing board
		// Graphics class doesn't have method to thicken stroke width
		// Small screen sizes do not need thick lines to distinguish squares
		Graphics2D g2 = (Graphics2D) g;
		if(SCREENWIDTH >= 1920) {
			strokeWidth = 4;
		} else {
			if(SCREENWIDTH >= 1080){
				strokeWidth = 2;
			}
		}
		g2.setStroke(new BasicStroke(strokeWidth));
		Rectangle rOutline, rFill;
		for(int row = 0; row<11; row++) {
			for(int col = 0; col<11; col++) {
				// Fill creates rectangle overlapping outline, so it needs to be smaller
				// to avoid overlapping
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				rOutline = new Rectangle(topLeftX,topLeftY,squareLength, squareLength);
				rFill = new Rectangle(topLeftX+strokeWidth/2,topLeftY+strokeWidth/2, squareLength-strokeWidth, squareLength-strokeWidth);
				if(row == 5) {
					// River needs different draw method
					int fullLength = squareLength*11;
					rOutline = new Rectangle(topLeftX,topLeftY,fullLength,squareLength);
					rFill = new Rectangle(topLeftX+strokeWidth/2,topLeftY+strokeWidth/2, fullLength-strokeWidth, squareLength-strokeWidth);
					row++; col--;
				}

				//TODO: This will cause problems for variants
				//Make the square appear on the screen
				g2.setColor(new Color(158,79,34));
				g2.draw(rOutline);
				if((row<3 || row > 7) && (col>=3 && col<= 7)) {
					g2.setColor(new Color(226,192,106));
				}
				else {
					g2.setColor(new Color(244,227,166));
				}
				g2.fill(rFill);

			}
			
			addMouseListener(this);

			setPieceImages(g2);
			
		}

	}

	void setPieceImages(Graphics2D g2) {
		for(int col = 0; col<11; col++) {
			for(int row = 0; row<11; row++) {
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				Piece[][] pieceList = board.getCoords();
				Piece piece = pieceList[row][col];
				//This breaks the order in Board but it works so...
				if (piece != null) {
					System.out.print(row+"\n"+col);
					String fileName = piece.getImageName();
					Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
					// figure out why is the y instead of x (i think it's because of the board's order)
					g2.drawImage(scaledImage, topLeftX+5, topLeftY+5, squareLength-10, squareLength-10, null);

				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int y = ((e.getX()-leftMostPixel)/squareLength) ;
		int x = ((e.getY()-margin)/squareLength);
		if(board.getCoords(x,y) != null){
			mouseX = x;
			mouseY = y;
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int y = ((e.getX()-leftMostPixel)/squareLength) ;
		int x = ((e.getY()-margin)/squareLength);
		if(board.getCoords(x,y) != null){
			mouseX = x;
			mouseY = y;
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int y = ((e.getX()-leftMostPixel)/squareLength) ;
		int x = ((e.getY()-margin)/squareLength);
		//Move move = new Move(mouseX, mouseY, x, y);
		//board.doMove(move);
		
		/*Piece piece = board.getCoords(mouseX,mouseY);
		if(piece != null) {
		String fileName = piece.getImageName();
		Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
		
		g2.drawImage(scaledImage, x+5, y+5, squareLength-10, squareLength-10, null);}*/
		if(board.getCoords(x, y) == null) {
			Pieces = board.getCoords();
			Piece piece = board.getCoords(mouseX,mouseY);
			Pieces[x][y] = Pieces[mouseX][mouseY];
			Pieces[mouseX][mouseY] = null;
			board.setCoords(Pieces);
			
			/*if(piece != null) {
			String fileName = piece.getImageName();
			Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
			g2.drawImage(scaledImage, x+5, y+5, squareLength-10, squareLength-10, null);}*/
			
			repaint();
			System.out.print(x +"\n" +y);
			System.out.print("----------------------\n");
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}