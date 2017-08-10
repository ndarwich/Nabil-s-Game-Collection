package TetrisWorld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

//checkpoint 11 6/10/2016 2:48 AM- THE GAME IS ALMOST DONE! SQUARE COUNTER WORKS. JUST REPAINTING PIECES LEFT!!!!
//checkpoint 12 6/10/2016 3:56 PM- Everything works great, now time to make code more efficient
//checkpoint 13 6/10/2016 11:56 PM - Sound added, oode as efficient as ever
public class TetrisWorld extends JFrame implements KeyListener, ActionListener{
  private TetrisWorldLogic game;
  private int everythingLoaded = 0, currentScore = 0, squareCounter = 0, ggNumber = 0;
  private boolean restartMusic = false, paused = false;
  private char currentS = 'w';
  private JFrame frame;
  private GridClass grid;
  private SideClass sidebar;
  private JLabel score, squares, pauseScreen;
  private JPanel[][] tiles, vip; //jpanels for the tiles and the Very Important Pieces (the ones on the side)
  private Timer timer;
  private AudioInputStream backgroundMusic, pauseSound, gameOverSound, dropSound, holdSound;
  private Clip clip, pauseClip, gameOverClip, dropClip, holdClip;
//  private AudioData musicData;
  //images are loaded for the background of the game and the sidebar
  private Image background = Toolkit.getDefaultToolkit().createImage("TetrisWorld/Images/background.png"),
    sidebarImage = Toolkit.getDefaultToolkit().createImage("TetrisWorld/Images/sidebar.png");
  private JLabel[] arrows = {retrieveJLabel("s"), retrieveJLabel("a"), retrieveJLabel("w"), retrieveJLabel("d")};
  private JLabel[] nextPieces = {retrieveJLabel("y"), retrieveJLabel("p"), retrieveJLabel("g"), retrieveJLabel("r"), 
    retrieveJLabel("b"), retrieveJLabel("o"), retrieveJLabel("c")};
  private JLabel[] heldPieces = {retrieveJLabel("y"), retrieveJLabel("p"), retrieveJLabel("g"), retrieveJLabel("r"), 
    retrieveJLabel("b"), retrieveJLabel("o"), retrieveJLabel("c")};
  public TetrisWorld(){
    game = new TetrisWorldLogic();
    frame = new JFrame("Tetris World");
    frame.setSize(850,702);
    frame.setLayout(new BorderLayout());
    frame.setBackground(Color.BLACK); 
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setResizable(false);
    grid = new GridClass();
    sidebar = new SideClass();
    pauseScreen = retrieveJLabel("pauseScreen");
    grid.setSize(700,700);
    sidebar.setSize(150,700);
    grid.setLayout(new GridLayout(28, 28));
    sidebar.setLayout(new GridLayout(7,1));
    //the tiles are initilized and put in the grid
    tiles = new JPanel[28][28];
    for (int i = 0; i < 28; i++){
      for (int j = 0; j < 28; j++){
        tiles[i][j] = new JPanel(new BorderLayout());
        tiles[i][j].setOpaque(false);
        if (i == 0 || i == 27 || j == 0 || j == 27){
          tiles[i][j].add(retrieveJLabel("borderBlock"), BorderLayout.CENTER);
          tiles[i][j].repaint();
        }
        grid.add(tiles[i][j]);
      }
    }
    //the vip are initialized
    vip = new JPanel[7][1];
    for (int i = 0; i < 7; i++){
      vip[i][0] = new JPanel(new BorderLayout());
      vip[i][0].setOpaque(false);
      sidebar.add(vip[i][0]);
    }
    updateShift('s');
    //////////////////////score gui text setup////////////////
    score = new JLabel("0");
    score.setFont(new Font("Serif", Font.BOLD, 26));
    score.setForeground(Color.RED);
    score.setHorizontalAlignment(JLabel.CENTER);
    //////////////////////square gui text setup////////////////
    squares = new JLabel("0");
    squares.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
    squares.setForeground(Color.BLUE);
    squares.setHorizontalAlignment(JLabel.CENTER);
    vip[5][0].add(squares);
    updateShift(game.getShift());
    vip[6][0].add(score, BorderLayout.CENTER);
    vip[6][0].repaint();
    updateNext(); //next pieces are updated on the gui
    //grid on the left, sidebar on the right
    frame.add(grid,BorderLayout.WEST);
    frame.add(sidebar, BorderLayout.CENTER);
    frame.addKeyListener(this);
    timer = new Timer(350, this);
    timer.start();
    repaint();
    initializeMusic();
    frame.setVisible(true);
  }
  private void updateShift(char shift){
    if (shift != currentS){
      currentS = shift;
      vip[0][0].removeAll();
      vip[0][0].add(arrows[game.getShiftID()]);
      vip[0][0].revalidate();
      vip[0][0].repaint();
    }
  }
  private void updateHeld(){
    vip[1][0].removeAll();
    vip[1][0].add(heldPieces[game.getHeldPieceID()]);
    vip[1][0].revalidate();
    vip[1][0].repaint();
  }
  private void updateNext(){
    vip[2][0].removeAll();
    vip[3][0].removeAll();
    vip[4][0].removeAll();
    int[] next3 = game.getNextPieces();
    vip[2][0].add(nextPieces[next3[0]]);
    vip[3][0].add(nextPieces[next3[1]]);
    vip[4][0].add(nextPieces[next3[2]]);
    vip[2][0].revalidate();
    vip[2][0].repaint();
    vip[3][0].revalidate();
    vip[3][0].repaint();
    vip[4][0].revalidate();
    vip[4][0].repaint();
  }
  private JLabel retrieveJLabel(String fileName){
    return new JLabel(new ImageIcon("TetrisWorld/Images/" + fileName + ".png"));
  }
  private void pause(){
    if (! paused){
      paused = true;
      timer.stop();
      clip.stop();
      try{
        pauseClip.start();
      }
      catch (Exception e){
        System.out.println(e);
      }
      frame.remove(grid);
      frame.add(pauseScreen, BorderLayout.WEST);
      frame.revalidate();
      frame.repaint();
    }
    else{
      paused = false;
      clip.loop(Clip.LOOP_CONTINUOUSLY);
      frame.remove(pauseScreen);
      pauseClip.stop();
      pauseClip.setFramePosition(0);
      frame.add(grid,BorderLayout.WEST);
      frame.revalidate();
      frame.repaint();
      timer.start();
    } 
  }
  private void gameOver(){
    timer.setDelay(11000/52); //52 GG tiles need to be placed in 11 seconds
    try{
      gameOverClip.start();
    }
    catch (Exception e){
      System.out.println(e);
    }
  }
  private void paintPiece(int row, int col){
    tiles[row][col].removeAll();
    if (game.getBoard()[row][col] != ' '){
      String color = retrieveColor(game.getBoard()[row][col]);
      tiles[row][col].add(retrieveJLabel(color + "Block"));
    }
    tiles[row][col].revalidate();
    tiles[row][col].repaint();
    grid.revalidate();
    grid.repaint();
  }
  private void paintPiece(int row, int col, char c){
    tiles[row][col].removeAll();
    String color = retrieveColor(c);
    tiles[row][col].add(retrieveJLabel(color + "Block"));
    tiles[row][col].revalidate();
    tiles[row][col].repaint();
    grid.revalidate();
    grid.repaint();
  }
  private void paintGG(int pieceNumber){
    int shiftForSecond = 0;
    if (pieceNumber >= 27){
      shiftForSecond = 9;
      pieceNumber -= 27;
    }
    switch (pieceNumber){
      case 0:
        paintPiece(11,12 + shiftForSecond,'n');
        return;
      case 1:
        paintPiece(11,11 + shiftForSecond,'n');
        return;
      case 2:
        paintPiece(10,11 + shiftForSecond,'n');
        return;
      case 3:
        paintPiece(10,10 + shiftForSecond,'n');
        return;
      case 4:
        paintPiece(10,9 + shiftForSecond,'n');
        return;
      case 5:
        paintPiece(10,8 + shiftForSecond,'n');
        return;
      case 6:
        paintPiece(10,7 + shiftForSecond,'n');
        return;
      case 7:
        paintPiece(11,7 + shiftForSecond,'n');
        return;
      case 8:
        paintPiece(11,6 + shiftForSecond,'n');
        return;
      case 9:
        paintPiece(12,6 + shiftForSecond,'n');
        return;
      case 10:
        paintPiece(13,6 + shiftForSecond,'n');
        return;
      case 11:
        paintPiece(14,6 + shiftForSecond,'n');
        return;
      case 12:
        paintPiece(15,6 + shiftForSecond,'n');
        return;
      case 13:
        paintPiece(16,6 + shiftForSecond,'n');
        return;
      case 14:
        paintPiece(16,7 + shiftForSecond,'n');
        return;
      case 15:
        paintPiece(17,7 + shiftForSecond,'n');
        return;
      case 16:
        paintPiece(17,8 + shiftForSecond,'n');
        return;
      case 17:
        paintPiece(17,9 + shiftForSecond,'n');
        return;
      case 18:
        paintPiece(17,10 + shiftForSecond,'n');
        return;
      case 19:
        paintPiece(16,11 + shiftForSecond,'n');
        return;
      case 20:
        paintPiece(16,12 + shiftForSecond,'n');
        return;
      case 21:
        paintPiece(17,12 + shiftForSecond,'n');
        return;
      case 22: //act like you're actually writing a g
        return;
      case 23:
        paintPiece(15,12 + shiftForSecond,'n');
        return;
      case 24:
        paintPiece(14,12 + shiftForSecond,'n');
        return;
      case 25:
        paintPiece(14,11 + shiftForSecond,'n');
        return;
      case 26:
        paintPiece(14,10 + shiftForSecond,'n');
        return;
      default:
        if (shiftForSecond==9)
        timer.stop();
        return;
    }
  }
  private void initializeMusic(){
    try{
     backgroundMusic = AudioSystem.getAudioInputStream(new File("TetrisWorld/Audio/background.wav"));
     pauseSound = AudioSystem.getAudioInputStream(new File("TetrisWorld/Audio/pause.wav"));
     gameOverSound = AudioSystem.getAudioInputStream(new File("TetrisWorld/Audio/gameOver.wav"));
     holdSound = AudioSystem.getAudioInputStream(new File("TetrisWorld/Audio/hold.wav"));
     dropSound = AudioSystem.getAudioInputStream(new File("TetrisWorld/Audio/drop.wav"));
     dropClip = AudioSystem.getClip();
     holdClip = AudioSystem.getClip();
     gameOverClip = AudioSystem.getClip();
     pauseClip = AudioSystem.getClip();
     clip = AudioSystem.getClip();
     clip.open(backgroundMusic);
     pauseClip.open(pauseSound);
     gameOverClip.open(gameOverSound);
     dropClip.open(holdSound);
     holdClip.open(dropSound);
     clip.loop(Clip.LOOP_CONTINUOUSLY);
   }
    catch (Exception e){
      System.out.println(e);
    } 
  }
  private String retrieveColor(char c){
    switch (c){
      case 'y':
        return "yellow";
      case 'p':
        return "purple";
      case 'r':
        return "red";
      case 'g':
        return "green";
      case 'b':
        return "blue";
      case 'o':
        return "orange";
      case 'c':
        return "cyan";
      case 'i':
        return "invisible";
      case 'w':
        return "white";
      case 'n':
        return "black";
      default:
        return "gray";
    }
  }
  
  @Override
  public void repaint(){
    int actualScore = currentScore;
    if (game.getState() == 's')
      actualScore = game.getScore();
    if (currentScore != actualScore){
      currentScore = actualScore;
      score.setText("" + actualScore);
    }
    if (game.getJustPlacedItem())
      updateNext();
    if (game.getUpdateSquares()){
      squareCounter += game.getSquaresJustCleared();
      squares.setText("" + squareCounter);
    }
    for (int i = 1; i < 27; i++){
      for (int j = 1; j < 27; j++){
        paintPiece(i, j);
      }
    }
    //my weird by heuristical approach to loading every item on the screen
    if (everythingLoaded++ < 10)
      sidebar.repaint();
  }
  
  @Override
  public void actionPerformed(ActionEvent e){
    if (game.getState() == 'o'){
      gameOver();
      clip.stop();
      paintGG(ggNumber++);
    }
    else if (timer.equals(e.getSource())){
      repaint();
      game.move(game.getShift());
      repaint();
    }
  }
  @Override
  public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();
    int leftOrRight = e.getKeyLocation(); //helps determine which shift on the keyboard was pressed
    char currentShift = game.getShift();
    if (game.getState() == 'o')
      return;
    if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P){
      pause();
    }
    if (paused){
      return;
    }
    if (keyCode == KeyEvent.VK_SHIFT && leftOrRight == KeyEvent.KEY_LOCATION_LEFT){
      game.switchShift();
      updateShift(game.getShift());
    }
    if (keyCode == KeyEvent.VK_SHIFT && leftOrRight == KeyEvent.KEY_LOCATION_RIGHT){
      if (! game.getPieceJustHeld()){
        holdClip.setFramePosition(0);
        holdClip.start();
      }
      game.hold();
      if (game.getPieceJustHeld()){
        updateHeld();
      }
      if (! game.getPieceHeld())
        updateNext();
      
    }
    if (keyCode == KeyEvent.VK_UP){
      if (currentShift == 's')
        game.rotate();
      else
        game.move('w');
    }
    if (keyCode == KeyEvent.VK_DOWN){
      if (currentShift == 'w')
        game.rotate();
      else
        game.move('s');
    }
    if (keyCode == KeyEvent.VK_LEFT){
      if (currentShift == 'd')
        game.rotate();
      else
        game.move('a');
    }
    if (keyCode == KeyEvent.VK_RIGHT){
      if (currentShift == 'a')
        game.rotate();
      else
        game.move('d');
    }
    if (keyCode == KeyEvent.VK_R){
      game.rotate();
    }
    if (keyCode == KeyEvent.VK_SPACE){
      dropClip.setFramePosition(0);
      dropClip.start();
      game.hardDrop();
    }
//    if (keyCode == KeyEvent.VK_D){
//      System.out.println(game);
//    }
    repaint();
  }
  @Override public void keyReleased(KeyEvent e){}
  @Override public void keyTyped(KeyEvent e){}
  public static void main(String[] args){
    new TetrisWorld();
  }
  private class GridClass extends JPanel{
    @Override
    protected void paintComponent(Graphics g){
      super.paintComponent(g);
      g.drawImage(background, 0, 0, null);
    }
  }
  private class SideClass extends JPanel{
    @Override
    protected void paintComponent(Graphics g){
      super.paintComponent(g);
      g.drawImage(sidebarImage, 0, 0, null);
    }
  }
}