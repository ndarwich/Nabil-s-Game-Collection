package Chess;

import java.awt.*; //colors and 
import java.awt.event.*; //listeners and 
import javax.swing.*; //jpanels and jframes and jstuff
import javax.swing.border.*;
import java.util.Hashtable; //hashtable

public class Chess extends JFrame implements KeyListener, MouseListener{
  public JPanel tiles[][];
  public JPanel selectedTile;
  public Color selectedColor;
  public JLabel pieces[];
  private Board game;
  private Border blackBorder = LineBorder.createBlackLineBorder();
  Color brown = new Color(185,122,87);
  Color gold = new Color(255,255,125);
  Color cyan = new Color(0,255,255);
  Color gray = new Color(192,192,192);
  public Chess(){
    super("Chess");
    setSize(600,600);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(8, 8));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(false);
    tiles = new JPanel[8][8];
    boolean alternate = false;
    for (int i = 0; i < 8; i++){
      for (int j = 0; j < 8; j++){
        tiles[i][j] = new JPanel(new BorderLayout());
        if (alternate)
          tiles[i][j].setBackground(brown);
        else
          tiles[i][j].setBackground(gold);
        tiles[i][j].setBorder(blackBorder);
        add(tiles[i][j]);
        alternate = !alternate;
      }
      alternate = !alternate;
    }
    game = new Board();
    pieces = new JLabel[32];
    int i = 0;
    for (Piece piece: game.pieces){
      pieces[i] = new JLabel(new ImageIcon("Chess/" + piece.toString() + piece.team + ".png"));
      tiles[piece.y][piece.x].add(pieces[i++]);
    }
    setVisible(true);
    addMouseListener(this);
    addKeyListener(this);
  }
  @Override
  public void keyReleased(KeyEvent event){}
  @Override
  public void keyPressed(KeyEvent event){}
  @Override
  public void keyTyped(KeyEvent event){}
  @Override
  public void mousePressed(MouseEvent event){
    if (game.gameOver){
      dispose();
      new Chess();
      return;
    }
    int tileX = event.getX()/75; 
    int tileY = (event.getY()-25)/75; //25 PX offset for y due to window frame
    if (game.currentPiece == null){
      game.selectPiece(tileX,tileY);
      if (game.currentPiece != null){
        tiles[tileY][tileX].setBackground(cyan);
        paintAvailableMoves();
      }
    }
    else{
      repaint();
      int oldX = game.currentPiece.x;
      int oldY = game.currentPiece.y;
      if (game.movePiece(tileX,tileY)){
        tiles[tileY][tileX].removeAll(); //gets rid of a previous piece (eating a piece), IT WORKS
        if (game.newQueen){
          pieces[game.currentIndex] = new JLabel(new ImageIcon("Chess/Q" + game.pieces[game.currentIndex].team + ".png"));
          tiles[oldY][oldX].removeAll();
          tiles[oldY][oldX].add(pieces[game.currentIndex]);
          game.newQueen = false;
        }
        tiles[tileY][tileX].add(tiles[oldY][oldX].getComponents()[0]);
        tiles[oldY][oldX].removeAll();
        tiles[oldY][oldX].revalidate();
        tiles[oldY][oldX].repaint();
        tiles[tileY][tileX].revalidate();
        tiles[tileY][tileX].repaint();
      }
    }
  }
  public void paintAvailableMoves(){
    for (int i = 0; i < 8; i++){
      for (int j = 0; j < 8; j++){
        if (game.currentPiece.move(i, j))
          tiles[j][i].setBackground(gray);
      }
    }
  }
  public void repaint(){
    boolean alternate = false;
    for (int i = 0; i < 8; i++){
      for (int j = 0; j < 8; j++){
        if (alternate)
          tiles[i][j].setBackground(brown);
        else
          tiles[i][j].setBackground(gold);
        alternate = !alternate;
      }
      alternate = !alternate;
    }
  }
  @Override
  public void mouseEntered(MouseEvent event){
    
  }
  @Override
  public void mouseExited(MouseEvent event){
    
  }
  @Override
  public void mouseReleased(MouseEvent event){
    
  }
  @Override
  public void mouseClicked(MouseEvent event){}
  
  public static void main(String[] args){
    new Chess();
  }
}