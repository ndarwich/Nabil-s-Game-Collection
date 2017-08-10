package HillHeroes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class HillHeroes extends JFrame implements MouseListener, MouseMotionListener{
  private JPanel grid; //the grid of tiles, to be colored in soon
  private JFrame frame; //the frame itself, which will be what the user will be seeing
  private JPanel hillBoard;
  private JPanel tiles[][] = new JPanel[9][9]; //the 9x9 2-d array that will fill the grid with JPanels
  private Component selectedPiece;
  private int currentRow; //records mouse x and y positions, simplified
  private int currentCol;
  private int lastRow = 3; //records previous x and y positions
  private int lastCol = 1;
  private boolean pieceSelected;
  private Border blackBorder = BorderFactory.createLineBorder(Color.black);
  private Border greenBorder = BorderFactory.createMatteBorder(3,3,3,3, Color.green);
  private Border hillBorder = BorderFactory.createCompoundBorder(blackBorder, greenBorder); //compound border for hill tiles
  private Board board;
  private Piece boardPieceSelected;
  private final JLabel statusBar;
  private static JFrame window;
  boolean redTurn; //booleans to hold whose turn it is, blue goes first because it's blue
  boolean blueTurn;
  public HillHeroes(){
    redTurn = false;
    blueTurn = true;
    pieceSelected = false;
    board = new Board(); //board instantiated, the logic behind the whole game
    grid = new JPanel(); //the grid is created
    grid.setSize(450, 450);
    grid.setLayout(new GridLayout(9, 9));
    frame = new JFrame("Hill Heroes"); //new frame, this will be what is inside the window
    frame.setSize(467, 505);
    frame.setLayout(new BorderLayout());
    frame.setResizable(false); //the window won't be able to be resized
    frame.setLocationRelativeTo(null); //centers the window
    statusBar = new JLabel("Welcome to Hill Heroes! Select a piece and start conquering the hill! Blue first."); 
    frame.add(grid);
    frame.add(statusBar, BorderLayout.SOUTH);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addMouseListener(this); //to allow mouse interaction with the frame, these two are a must
    frame.addMouseMotionListener(this);
    paint();
    adjustBoard();
    for (int i = 0; i < 9; i++){for (int j = 0; j < 9; j++){grid.add(tiles[i][j]);}}
    frame.setVisible(true); //sets visible after everything is painted to not require repainting
  }
  @Override public void mousePressed(MouseEvent e){}
  @Override public void mouseDragged(MouseEvent e){}
  @Override public void mouseReleased(MouseEvent e){}
  @Override public void mouseClicked(MouseEvent e){
    currentRow = (e.getY()-30)/50;
    currentCol = (e.getX()-7)/50;
    if (board.state == Board.GameState.Over){
      window.dispose();
      frame.dispose();
      window = new HillHeroes();
      return;
    }
    repaint(lastRow, lastCol);
    int[] possibleMoves = new int[]{-1,0,1};
    for (int i = 0; i < 3; i++){
      for (int j = 0; j < 3; j++){
        repaint(lastRow + possibleMoves[i], lastCol + possibleMoves[j]);
      }
    }
    if (pieceSelected){
      pieceSelected = false;
      //moves the piece that was selected
      try{
        Piece selected = board.getPiece(lastRow, lastCol);
        board.move(selected, currentRow, currentCol);
        adjustBoard();
        if (redTurn){
          redTurn = false;
          blueTurn = true;
        }
        else{
          redTurn = true;
          blueTurn = false;
        }
        statusBar.setText(String.format("%s moved to %d, %d", selected, currentRow, currentCol));        
        board.checkWinner();
      }
      catch (Exception ex){
        statusBar.setText(String.format("%s", ex));
      }
      System.out.println(board);
      tiles[currentRow][currentCol].removeAll();
      resetBlock(currentRow, currentCol);
      return;
    }
    Component selectedPiece = grid.findComponentAt(e.getX() - 7, e.getY() - 30);
    if (pieceSelected == false && selectedPiece instanceof JLabel){
      try{
        Piece selected = board.getPiece(currentRow, currentCol);
        if (redTurn && selected.getTeam() != Piece.Team.Red)
          throw new InvalidPieceSelected("It is red's turn!");
        else if (blueTurn && selected.getTeam() != Piece.Team.Blue)
          throw new InvalidPieceSelected("It is blue's turn!");
        pieceSelected = true;
        lastRow = currentRow;
        lastCol = currentCol;
        tiles[currentRow][currentCol].setBackground(Color.yellow);
        paintAvailableMoves(currentRow, currentCol);
        statusBar.setText(String.format("Piece selected at %d, %d", currentRow, currentCol));
      }
      catch (Exception ex){
        statusBar.setText(String.format("%s",ex));
      }
    }
    else
      statusBar.setText(String.format("Mouse clicked at %d, %d, no piece selected", currentRow, currentCol));
  }
  
  @Override public void mouseMoved(MouseEvent e){
    currentRow = (e.getY()-30)/50;
    currentCol = (e.getX()-7)/50;
    if (currentRow > 8 || currentCol > 8 || tiles[currentRow][currentCol].getBackground() == Color.black){
      return;
    }
    if (tiles[lastRow][lastCol].getBackground() == Color.yellow){
      return;
    }
    if ((currentRow != lastRow || currentCol != lastCol)){
      repaint(lastRow, lastCol);
      lastRow = currentRow;
      lastCol = currentCol;
    }
    tiles[currentRow][currentCol].setBackground(new Color(152,252,254));
  }
  @Override public void mouseEntered(MouseEvent e){}
  @Override public void mouseExited(MouseEvent e){}  
  private void paint(){
    for (int a = 0; a < 9; a++){
      for (int b = 0; b < 9; b++){
        tiles[a][b] = new JPanel();
        tiles[a][b].setBackground(Color.white);
        //if the tile is within the hill, have it have the green black compound border
        if (a < 6 && a > 2 && b < 6 && b > 2)
          tiles[a][b].setBorder(hillBorder);
        //otherwise, just the thin black border
        else
          tiles[a][b].setBorder(blackBorder);
        //gets rid of all the corner tiles by coloring them completely black
        if ((a < 3 && (b < 3 || b > 5)) || (a > 5 && (b < 3 || b > 5)))
          tiles[a][b].setBackground(Color.black);
        //top and bottom edge coloring
        if (b >= 3 && b <= 5){
          if (a == 0)
            tiles[a][b].setBackground(new Color(112,146,190));
          else if (a == 8)
            tiles[a][b].setBackground(new Color(220,82,96));
        }
        //left and right edge coloring
        if (a >= 3 && a <= 5){
          if (b == 0)
            tiles[a][b].setBackground(new Color(255,239,149));
          else if (b == 8)
            tiles[a][b].setBackground(new Color(127,127,127));
        }
        //the four remaining tiles (in the corner of the edges)...
        if ((a == 6 || a == 2) && (b == 6 || b == 2))
          tiles[a][b].setBackground(Color.white);
      }
    }
  }
  
  //repaints a specific piece in the board to its original color
  private void repaint(int a, int b){
    if (a < 0 || b < 0 || a > 8 || b > 8)
      return;
    tiles[a][b].setBackground(Color.white);
    if ((a < 3 && (b < 3 || b > 5)) || (a > 5 && (b < 3 || b > 5)))
      tiles[a][b].setBackground(Color.black);
    if (b >= 3 && b <= 5){
      if (a == 0)
        tiles[a][b].setBackground(new Color(112,146,190));
      else if (a == 8)
        tiles[a][b].setBackground(new Color(220,82,96));
    }
    else if (a >= 3 && a <= 5){
      if (b == 0)
        tiles[a][b].setBackground(new Color(255,239,149));
      else if (b == 8)
        tiles[a][b].setBackground(new Color(127,127,127));
    }
    else if ((a == 6 || a == 2) && (b == 6 || b == 2))
      tiles[a][b].setBackground(Color.white);
  }
  
  private void paintAvailableMoves(int row, int col){
    int[] possibleMoves = new int[]{-1,0,1};
    for (int i = 0; i < 3; i++){
      for (int j = 0; j < 3; j++){
        if (board.canMove(board.getPiece(row, col), row + possibleMoves[i], col + possibleMoves[j])){
          tiles[row + possibleMoves[i]][col + possibleMoves[j]].setBackground(new Color(111,251,53));
        }
      }
    }
  }
  
  private void resetBlock(int a, int b){
    char c = board.getLayout()[a][b];
    if (c == '.' || c == '|'){
      tiles[a][b].removeAll();
    }
    else
      tiles[a][b].add(new JLabel(new ImageIcon("HillHeroes/Images/" + (Piece.fromChar(c, a, b)).toString() + ".png")));
    return;
  }
  private void adjustBoard(){
    for (int i = 0; i < 9; i++){
      for (int j = 0; j < 9; j++){
        resetBlock(i,j);
        repaint(i,j);
      }
    }
  }
  public static void main(String[] args){
    window = new HillHeroes(); //constructor is called, a new window and frame are created
    window.setResizable(false); //disallows the resizing of the window
    window.setLocationRelativeTo(null); //centers the window
  }
}