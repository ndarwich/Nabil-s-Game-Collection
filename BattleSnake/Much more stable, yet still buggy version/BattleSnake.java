package BattleSnake;

import java.awt.*; //colors and borders
import java.awt.event.*; //listeners and 
import javax.swing.*; //jpanels and jframes and jstuff
import javax.swing.border.*;
import java.util.Hashtable; //hashtable

public class BattleSnake extends JFrame implements KeyListener, ActionListener{
  private Color darkGray = new Color(40,45,50); //a slightly more bluish/greenish color is made for cool neon snakes
  private BattleSnakeLogic game;
  private JFrame frame, temporaryWindow;
  private JLabel food1 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")), 
    food2 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")),
    food3 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")), 
    food4 = new JLabel(new ImageIcon("BattleSnake/Images/food.png"));
  private JPanel grid;
  private JPanel[][] tiles;
  private int players, speed;
  private boolean gameStarted = false, gameOver = false;
  private Timer timer;
  private ActionEvent update;
  public BattleSnake(){
    temporaryWindow = new JFrame("Battle Snake Setup");
    temporaryWindow.setSize(400,275);
    temporaryWindow.setLocationRelativeTo(null);
    //3  JLabels for prompts so that the user sets up their game
    JLabel prompt = new JLabel("Welcome to Battle Snake! Pick settings for your game.", SwingConstants.CENTER);
    JLabel playersPrompt = new JLabel("How many players are playing?", SwingConstants.CENTER);
    JLabel speedPrompt = new JLabel("Pick a speed for your game", SwingConstants.CENTER);
    //started to learn about JSliders, so might as well include them in this game :D
    JSlider numPlayers = new JSlider(JSlider.HORIZONTAL, 2, 4, 2), speed = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
    numPlayers.setMajorTickSpacing(1); //designates the integer difference between each major tick
    numPlayers.setPaintLabels(true); //will decorate the slider
    numPlayers.setPaintTicks(true);
    //taking advantage of the setLabelTable method to name my own labels for speeds (Thanks CS 310 for hash tables)
    Hashtable<Integer, JLabel> speeds = new Hashtable<Integer, JLabel>();
    speeds.put(1, new JLabel("Slow"));
    speeds.put(2, new JLabel("Medium"));
    speeds.put(3, new JLabel("Fast"));
    speed.setLabelTable(speeds);
    speed.setMajorTickSpacing(1);
    speed.setPaintTicks(true);
    speed.setPaintLabels(true);
    //start button, the action is simply launching a new game of snake using the legitimate constructor
    JButton start = new JButton("Battle!"),  cancel = new JButton("Cancel");
    start.addActionListener(new ActionListener(){
      @Override public void actionPerformed(ActionEvent e){
        temporaryWindow.dispose();
        new BattleSnake(numPlayers.getValue(), speed.getValue()); //the values from the sliders are used for the game
      }
    });
    //cancel button to just exit out
    cancel.addActionListener(new ActionListener(){
      @Override public void actionPerformed(ActionEvent e){
        temporaryWindow.dispose();
      }
    });
    //a gridbaglayout is set again (I love gridbaglayouts)
    GridBagLayout layout = new GridBagLayout();
    temporaryWindow.setLayout(layout);
    GridBagConstraints c = new GridBagConstraints();
    //padding and formatting of all elements in the frame ensues
    c.insets = new Insets(5,5,5,5);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 4;
    temporaryWindow.add(prompt, c);
    c.gridy = 1;
    c.gridx = 2;
    c.gridwidth = 2;
    temporaryWindow.add(playersPrompt, c);
    c.gridy = 2;
    temporaryWindow.add(numPlayers, c);
    c.gridy = 3;
    temporaryWindow.add(speedPrompt, c);
    c.gridy = 4;
    temporaryWindow.add(speed, c);
    c.gridy = 5;
    c.gridx = 1;
    temporaryWindow.add(cancel, c);
    c.gridx = 3;
    temporaryWindow.add(start, c);
    temporaryWindow.setVisible(true);
  }
  
  public BattleSnake(int players, int speed){
    game = new BattleSnakeLogic(players);
    this.players = players;
    this.speed = speed;
    frame = new JFrame("Battle Snake");
    frame.setSize(960,672); //60 columns, 42 rows * 16 pixels for each row/column
    frame.setLayout(new BorderLayout());
    frame.setBackground(darkGray); 
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    grid = new JPanel();
    grid.setSize(960,672);
    grid.setLayout(new GridLayout(42, 60));
    //instead of multiple cells to hold walls/open spots, the walls become a border, and open spots are background
    grid.setBackground(darkGray);
    grid.setBorder(new LineBorder(Color.BLACK, 16));
    tiles = new JPanel[42][60];
    for (int i = 0; i < 42; i++){
      for (int j = 0; j < 60; j++){
        tiles[i][j] = new JPanel(new BorderLayout());
        tiles[i][j].setBackground(darkGray);
        grid.add(tiles[i][j]);
      }
    }
    frame.addKeyListener(this);
    frame.add(grid);
    timer = new Timer(1000, this);
    repaint();
    frame.setVisible(true);
    timer.start();
  }
  @Override
  public void repaint(){
    repaintImportantCells();
  }
  
  public void update(){
    game.move();
    repaint();
  }
  
  public void repaintImportantCells(){
    char[][] layout = game.getLayout();
    JLabel rHead = new JLabel(new ImageIcon("BattleSnake/Images/rHead" + game.getCurrentRDirection() + ".png")),
    rBody = new JLabel(new ImageIcon("BattleSnake/Images/rBody" + game.getCurrentRBodyDirection() + ".png")),
    rTail = new JLabel(new ImageIcon("BattleSnake/Images/rTail" + game.getCurrentRTailDirection() + ".png")),
    bHead = new JLabel(new ImageIcon("BattleSnake/Images/bHead" + game.getCurrentBDirection() + ".png")),
    bBody = new JLabel(new ImageIcon("BattleSnake/Images/bBody" + game.getCurrentBBodyDirection() + ".png")),
    bTail = new JLabel(new ImageIcon("BattleSnake/Images/bTail" + game.getCurrentBTailDirection() + ".png")),
    gHead = new JLabel(new ImageIcon("BattleSnake/Images/gHead" + game.getCurrentGDirection() + ".png")),
    gBody = new JLabel(new ImageIcon("BattleSnake/Images/gBody" + game.getCurrentGBodyDirection() + ".png")),
    gTail = new JLabel(new ImageIcon("BattleSnake/Images/gTail" + game.getCurrentGTailDirection() + ".png")),
    yHead = new JLabel(new ImageIcon("BattleSnake/Images/yHead" + game.getCurrentYDirection() + ".png")),
    yBody = new JLabel(new ImageIcon("BattleSnake/Images/yBody" + game.getCurrentYBodyDirection() + ".png")),
    yTail = new JLabel(new ImageIcon("BattleSnake/Images/yTail" + game.getCurrentYTailDirection() + ".png"));
    //each condition starts off by cleaning up previous tiles that used to have the tail
    //then head tiles, previous head tiles, and tail tiles are recorded for each snake to adjust in the frame
    int[] cleanUpTile, cleanUpTile2, cleanUpTile3, cleanUpTile4, rHeadTile, bHeadTile, gHeadTile, yHeadTile, 
      rPreheadTile, bPreheadTile, gPreheadTile, yPreheadTile, rTailTile, bTailTile, gTailTile, yTailTile;
    /***********************************************RED SNAKE TILES***********************************************/
    if (game.rAlive()){
      cleanUpTile = game.getPreviousRTail();
      rHeadTile = game.rHead;
      rPreheadTile = game.previousRHead;
      rTailTile = game.rTail;
      //to avoid too many array accesses, just accesses all arrays one with these integers and records values
      int a = cleanUpTile[0], b = cleanUpTile[1], c = rHeadTile[0], d = rHeadTile[1], e = rPreheadTile[0], 
        f = rPreheadTile[1], g = rTailTile[0], h = rTailTile[1];
      //cleanUp
      tiles[a][b].removeAll();
      tiles[a][b].revalidate();
      tiles[a][b].repaint();
      //head
      tiles[c][d].removeAll();
      tiles[c][d].add(rHead);
      tiles[c][d].revalidate();
      tiles[c][d].repaint();
      //body
      tiles[e][f].removeAll();
      tiles[e][f].add(rBody);
      tiles[e][f].revalidate();
      tiles[e][f].repaint();
      //tail
      tiles[g][h].removeAll();
      tiles[g][h].add(rTail);
      tiles[g][h].revalidate();
      tiles[g][h].repaint();
      
    }
    /***********************************************BLUE SNAKE TILES***********************************************/
    if (game.bAlive()){
      cleanUpTile2 = game.getPreviousBTail();
      bHeadTile = game.bHead;
      bPreheadTile = game.previousBHead;
      bTailTile = game.bTail;
      int a = cleanUpTile2[0], b = cleanUpTile2[1], c = bHeadTile[0], d = bHeadTile[1], e = bPreheadTile[0], 
        f = bPreheadTile[1], g = bTailTile[0], h = bTailTile[1];
      //cleanUp
      tiles[a][b].removeAll();
      tiles[a][b].revalidate();
      tiles[a][b].repaint();
      //head
      tiles[c][d].removeAll();
      tiles[c][d].add(bHead);
      tiles[c][d].revalidate();
      tiles[c][d].repaint();
      //body
      tiles[e][f].removeAll();
      tiles[e][f].add(bBody);
      tiles[e][f].revalidate();
      tiles[e][f].repaint();
      //tail
      tiles[g][h].removeAll();
      tiles[g][h].add(bTail);
      tiles[g][h].revalidate();
      tiles[g][h].repaint();
      
    }
    /***********************************************GREEN SNAKE TILES***********************************************/
    if (game.gAlive()){
      cleanUpTile3 = game.getPreviousGTail();
      gHeadTile = game.gHead;
      gPreheadTile = game.previousGHead;
      gTailTile = game.gTail;
      int a = cleanUpTile3[0], b = cleanUpTile3[1], c = gHeadTile[0], d = gHeadTile[1], e = gPreheadTile[0], 
        f = gPreheadTile[1], g = gTailTile[0], h = gTailTile[1];
      //cleanUp
      tiles[a][b].removeAll();
      tiles[a][b].revalidate();
      tiles[a][b].repaint();
      //head
      tiles[c][d].removeAll();
      tiles[c][d].add(gHead);
      tiles[c][d].revalidate();
      tiles[c][d].repaint();
      //body
      tiles[e][f].removeAll();
      tiles[e][f].add(gBody);
      tiles[e][f].revalidate();
      tiles[e][f].repaint();
      //tail
      tiles[g][h].removeAll();
      tiles[g][h].add(gTail);
      tiles[g][h].revalidate();
      tiles[g][h].repaint();
    }
    /***********************************************YELLOW SNAKE TILES***********************************************/
    if (game.yAlive()){
      cleanUpTile4 = game.getPreviousYTail();
      yHeadTile = game.yHead;
      yPreheadTile = game.previousYHead;
      yTailTile = game.yTail;
      int a = cleanUpTile4[0], b = cleanUpTile4[1], c = yHeadTile[0], d = yHeadTile[1], e = yPreheadTile[0], 
        f = yPreheadTile[1], g = yTailTile[0], h = yTailTile[1];
      //cleanUp
      tiles[a][b].removeAll();
      tiles[a][b].revalidate();
      tiles[a][b].repaint();
      //head
      tiles[c][d].removeAll();
      tiles[c][d].add(yHead);
      tiles[c][d].revalidate();
      tiles[c][d].repaint();
      //body
      tiles[e][f].removeAll();
      tiles[e][f].add(yBody);
      tiles[e][f].revalidate();
      tiles[e][f].repaint();
      //tail
      tiles[g][h].removeAll();
      tiles[g][h].add(yTail);
      tiles[g][h].revalidate();
      tiles[g][h].repaint();
    }
    /**************************************************FOOD TILES**************************************************/
    if (game.food1eaten){
      int a = game.food1[0], b = game.food1[1];
      tiles[a][b].add(food1);
      tiles[a][b].repaint();
      game.food1eaten = false;
    }
    if (game.food2eaten){
      int a = game.food2[0], b = game.food2[1];
      tiles[a][b].add(food2);
      tiles[a][b].repaint();
      game.food2eaten = false;
    }
    if (game.food3eaten){
      int a = game.food3[0], b = game.food3[1];
      tiles[a][b].add(food3);
      tiles[a][b].repaint();
      game.food3eaten = false;
    }
    if (game.food4eaten){
      int a = game.food4[0], b = game.food4[1];
      tiles[a][b].add(food4);
      tiles[a][b].repaint();
      game.food4eaten = false;
    }
  }
  
  @Override public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();
    /***********************************************RED SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_W){
      game.changeRDirection('W');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_S){
      game.changeRDirection('S');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_A){
      game.changeRDirection('A');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_D){
      game.changeRDirection('D');
      repaint();
    }
    /***********************************************BLUE SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_UP){
      game.changeBDirection('W');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_DOWN){
      game.changeBDirection('S');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_LEFT){
      game.changeBDirection('A');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_RIGHT){
      game.changeBDirection('D');
      repaint();
    }
    /***********************************************GREEN SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_Y){
      game.changeGDirection('W');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_H){
      game.changeGDirection('S');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_G){
      game.changeGDirection('A');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_J){
      game.changeGDirection('D');
      repaint();
    }
    /***********************************************YELLOW SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_P){
      game.changeYDirection('W');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_SEMICOLON){
      game.changeYDirection('S');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_L){
      game.changeYDirection('A');
      repaint();
    }
    else if (keyCode == KeyEvent.VK_QUOTE){
      game.changeYDirection('D');
      repaint();
    }
  }
  @Override public void actionPerformed(ActionEvent e){
    if (gameStarted)
      update();
    else
      startGame();
  }
  private void startGame(){
    timer.stop();
    timer.setDelay(100 - (25*speed));
    game.pause();
    timer.start();
    gameStarted = true;
  }
  @Override public void keyReleased(KeyEvent e){}
  @Override public void keyTyped(KeyEvent e){}
  public static void main(String[] args){
    new BattleSnake();
  }
}