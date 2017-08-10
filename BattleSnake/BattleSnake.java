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
  //separate, distinct foods are spawned to prevent errors over which tile is the parent
  private JLabel food1 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")), 
    food2 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")),
    food3 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")), 
    food4 = new JLabel(new ImageIcon("BattleSnake/Images/food.png")),
    countdown;
  private JPanel grid, foregroundPanel;
  private JPanel[][] tiles;
  private int players, speed, powerUpCountdown;
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
    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    frame.setResizable(false); //a resizable frame would cause problems with the image sizes
    grid = new JPanel();
    grid.setSize(960,672);
    grid.setLayout(new GridLayout(42, 60));
    foregroundPanel = new JPanel();
    foregroundPanel.setSize(960,672);
    foregroundPanel.setOpaque(false); //makes the foreground panel transparent, so that messages can be displayed
    foregroundPanel.setLayout(new BorderLayout());
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
    frame.add(foregroundPanel);
    frame.add(grid);
    timer = new Timer(1000, this);
    repaint();
    frame.setVisible(true);
    timer.start();
    countdown = new JLabel(new ImageIcon("BattleSnake/Images/countdown.gif"));
    foregroundPanel.add(countdown);
    frame.revalidate();
    frame.repaint();
  }
  @Override
  public void repaint(){
    repaintImportantCells();
  }
  
  public void update(){
    game.move();
    if (game.fasterTime){
      timer.setDelay((timer.getDelay()/4)*3); //makes the game go faster by shortening the delay between ticks
      game.fasterTime = false;
    }
    else if (game.slowerTime){
      timer.setDelay((timer.getDelay()/3)*4); //makes the game go slower by widening the delay between ticks
      game.slowerTime = false;
    }
    repaint();
  }
  
  public void repaintImportantCells(){
    char[][] layout = game.getLayout();
    //snake bodies are set up
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
      cleanUpRelocate(cleanUpTile[0], cleanUpTile[1], rHeadTile[0], rHeadTile[1], rPreheadTile[0], rPreheadTile[1],
                      rTailTile[0], rTailTile[1], rHead, rBody, rTail);
      
    }
    /***********************************************BLUE SNAKE TILES***********************************************/
    if (game.bAlive()){
      cleanUpTile2 = game.getPreviousBTail();
      bHeadTile = game.bHead;
      bPreheadTile = game.previousBHead;
      bTailTile = game.bTail;
      cleanUpRelocate(cleanUpTile2[0], cleanUpTile2[1], bHeadTile[0], bHeadTile[1], bPreheadTile[0], bPreheadTile[1],
                      bTailTile[0], bTailTile[1], bHead, bBody, bTail);
      
    }
    /***********************************************GREEN SNAKE TILES***********************************************/
    if (game.gAlive()){
      cleanUpTile3 = game.getPreviousGTail();
      gHeadTile = game.gHead;
      gPreheadTile = game.previousGHead;
      gTailTile = game.gTail;
      cleanUpRelocate(cleanUpTile3[0], cleanUpTile3[1], gHeadTile[0], gHeadTile[1], gPreheadTile[0], gPreheadTile[1],
                      gTailTile[0], gTailTile[1], gHead, gBody, gTail);
    }
    /***********************************************YELLOW SNAKE TILES***********************************************/
    if (game.yAlive()){
      cleanUpTile4 = game.getPreviousYTail();
      yHeadTile = game.yHead;
      yPreheadTile = game.previousYHead;
      yTailTile = game.yTail;
      cleanUpRelocate(cleanUpTile4[0], cleanUpTile4[1], yHeadTile[0], yHeadTile[1], yPreheadTile[0], yPreheadTile[1],
                      yTailTile[0], yTailTile[1], yHead, yBody, yTail);
    }
    /**************************************************FOOD TILES**************************************************/
    if (game.food1eaten){
      respawnItem(game.food1[0], game.food1[1], food1);
      game.food1eaten = false;
    }
    if (game.food2eaten){
      respawnItem(game.food2[0], game.food2[1], food2);
      game.food2eaten = false;
    }
    if (game.food3eaten){
      respawnItem(game.food3[0], game.food3[1], food3);
      game.food3eaten = false;
    }
    if (game.food4eaten){
      respawnItem(game.food4[0], game.food4[1], food4);
      game.food4eaten = false;
    }
    //200 ticks for powerups to respawn (~20 seconds, depending on current game speed)
    if (powerUpCountdown == 0){
      tiles[game.powerUp[0]][game.powerUp[1]].removeAll();
      tiles[game.powerUp[0]][game.powerUp[1]].revalidate();
      tiles[game.powerUp[0]][game.powerUp[1]].repaint();
      game.spawnRandomPowerUp();
      JLabel powerUp = new JLabel(new ImageIcon("BattleSnake/Images/" + game.getCurrentPowerUp() + ".png"));
      respawnItem(game.powerUp[0], game.powerUp[1], powerUp);
      powerUpCountdown = 200;
    }
    else{
      powerUpCountdown--;
    }
  }
  
  private void cleanUpRelocate(int a, int b, int c, int d, int e, int f, int g, int h, JLabel H, JLabel B, JLabel T){
    //cleanUp
    tiles[a][b].removeAll();
    tiles[a][b].revalidate();
    tiles[a][b].repaint();
    //head relocation
    tiles[c][d].removeAll();
    tiles[c][d].add(H);
    tiles[c][d].revalidate();
    tiles[c][d].repaint();
    //body/prehead relocation
    tiles[e][f].removeAll();
    tiles[e][f].add(B);
    tiles[e][f].revalidate();
    tiles[e][f].repaint();
    //tail reloation
    tiles[g][h].removeAll();
    tiles[g][h].add(T);
    tiles[g][h].revalidate();
    tiles[g][h].repaint();
  }
  
  private void respawnItem(int row, int col, JLabel item){
    tiles[row][col].add(item); //no need for revalidation as the spot is already empty, I presume
    tiles[row][col].repaint();
  }
  
  @Override public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_ESCAPE)
      frame.dispose();
    if (keyCode == KeyEvent.VK_R){
      frame.dispose();
      new BattleSnake();
    }
    //no moves are made when the game isn't running
    if (game.paused || gameOver){
      return;
    }
    /***********************************************RED SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_W){
      game.changeRDirection('W');
    }
    else if (keyCode == KeyEvent.VK_S){
      game.changeRDirection('S');
    }
    else if (keyCode == KeyEvent.VK_A){
      game.changeRDirection('A');
    }
    else if (keyCode == KeyEvent.VK_D){
      game.changeRDirection('D');
    }
    /***********************************************BLUE SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_UP){
      game.changeBDirection('W');
    }
    else if (keyCode == KeyEvent.VK_DOWN){
      game.changeBDirection('S');
    }
    else if (keyCode == KeyEvent.VK_LEFT){
      game.changeBDirection('A');
    }
    else if (keyCode == KeyEvent.VK_RIGHT){
      game.changeBDirection('D');
    }
    /***********************************************GREEN SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_Y){
      game.changeGDirection('W');
    }
    else if (keyCode == KeyEvent.VK_H){
      game.changeGDirection('S');
    }
    else if (keyCode == KeyEvent.VK_G){
      game.changeGDirection('A');
    }
    else if (keyCode == KeyEvent.VK_J){
      game.changeGDirection('D');
    }
    /***********************************************YELLOW SNAKE MOVES***********************************************/
    if (keyCode == KeyEvent.VK_P){
      game.changeYDirection('W');
    }
    else if (keyCode == KeyEvent.VK_SEMICOLON){
      game.changeYDirection('S');
    }
    else if (keyCode == KeyEvent.VK_L){
      game.changeYDirection('A');
    }
    else if (keyCode == KeyEvent.VK_QUOTE){
      game.changeYDirection('D');
    }
  }
  
  @Override public void actionPerformed(ActionEvent e){
    if (gameStarted){
      if (game.paused){
        game.pause();
        foregroundPanel.remove(countdown); //the countdown is removed (as it probably started by now, right?)
        foregroundPanel.revalidate();
        foregroundPanel.repaint();
      }
      gameOver = game.isOver();
      if (gameOver){
        //the gameOver transparent screen gets a png from images to indicate who won
        if (game.rAlive())
          foregroundPanel.add(new JLabel(new ImageIcon("BattleSnake/Images/gameOverR.png")), BorderLayout.CENTER);
        else if (game.bAlive())
          foregroundPanel.add(new JLabel(new ImageIcon("BattleSnake/Images/gameOverB.png")), BorderLayout.CENTER);
        else if (game.gAlive())
          foregroundPanel.add(new JLabel(new ImageIcon("BattleSnake/Images/gameOverG.png")), BorderLayout.CENTER);
        else if (game.yAlive())
          foregroundPanel.add(new JLabel(new ImageIcon("BattleSnake/Images/gameOverY.png")), BorderLayout.CENTER);
        else
          foregroundPanel.add(new JLabel(new ImageIcon("BattleSnake/Images/gameOver.png")), BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        timer.stop();
        return;
      }
      update();
    }
    else
      startGame();
  }
  private void startGame(){
    timer.stop();
    timer.setDelay(140 - (35*speed));
    timer.start();
    gameStarted = true;
  }
  @Override public void keyReleased(KeyEvent e){}
  @Override public void keyTyped(KeyEvent e){}
  public static void main(String[] args){
    new BattleSnake();
  }
}