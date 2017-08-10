import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.io.*;

public class Snake extends JFrame implements KeyListener, ActionListener{
  private JFrame frame; //the frame itself, which will be what the user will be seeing
  private JPanel tiles[][], grid;
  private SnakeLogic game;
  private final JLabel statusBar, score;
  Color brown = new Color(81,38,14), silver = new Color(192,192,192), darkBrown = new Color(60,38,14);
  private int level, previousScore;
  private boolean updated;
  private JLabel body = new JLabel(new ImageIcon("Body.png"), JLabel.CENTER), 
    apple = new JLabel(new ImageIcon("Apple.png"), JLabel.CENTER), 
    mouse = new JLabel(new ImageIcon("Mouse.png"), JLabel.CENTER), 
    rabbit = new JLabel(new ImageIcon("Rabbit.png"), JLabel.CENTER),
    wpL = new JLabel(new ImageIcon("wpL.gif"), JLabel.CENTER),
    wpR = new JLabel(new ImageIcon("wpR.gif"), JLabel.CENTER);
  public Timer timer;
  public Snake(){
    updated = false;
    tiles = new JPanel[22][26];
    timer = new Timer(85, this);
    grid = new JPanel();
    frame = new JFrame("Snake");
    game = new SnakeLogic();
    String prompt = "Welcome to Snake! Rack up as many points as possible without crashing. Score high to unlock " +
      "the next level!";
    statusBar = new JLabel(prompt, SwingConstants.CENTER);
    score = new JLabel("Score: " + game.getScore(), SwingConstants.CENTER);
    grid.setSize(780, 660);
    grid.setLayout(new GridLayout(22,26));
    paint();
    for (int i = 0; i < 22; i++){for (int j = 0; j < 26; j++){grid.add(tiles[i][j]);}}
    frame.setSize(780, 660);
    frame.setLayout(new BorderLayout());
    frame.setResizable(true);
    frame.setLocationRelativeTo(null); //centers the window
    frame.add(grid);
    frame.add(statusBar, BorderLayout.NORTH);
    frame.add(score, BorderLayout.SOUTH);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.addKeyListener(this);
    previousScore = 0;
    level = 1;
    timer.start();
  }
  public Snake(int score, int level){
    this();
    timer.stop();
    previousScore = score;
    if (level < 5){
      timer = new Timer(85 - (15*(level)), this);
      level++;
      this.level = level;
      statusBar.setText(String.format("You are currently in level %d, foods are worth %dx as much.", level, level*2-2));
    }
    else
      statusBar.setText("You are currently at the highest level. Quickly collect points to unlock the entrance again!");
    timer.start();
  }
  @Override
  public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();
    if ((keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)){
      game.changeDirection(SnakeLogic.Direction.N);
    }
    else if ((keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)){
      game.changeDirection(SnakeLogic.Direction.S);
    }
    else if ((keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT)){
      game.changeDirection(SnakeLogic.Direction.W);
    }
    else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)){
      game.changeDirection(SnakeLogic.Direction.E);
    }
    else if ((keyCode == KeyEvent.VK_SPACE)){
      if (game.pause()) //calls the pause method on SnakeLogic and checks the boolean to determine the timer progress
        timer.stop();
      else
        timer.start();
    }
  }
  
  @Override public void keyReleased(KeyEvent e){}
  @Override public void keyTyped(KeyEvent e){}
  @Override public void actionPerformed(ActionEvent e){
    update();
  }
  //the frame will sync with the current game's state  in the paint() and update() methods, while performing actions
  public void paint(){
    char[][] layout = game.getLayout();
    for (int a = 0; a < 22; a++){
      for (int b = 0; b < 26; b++){
        tiles[a][b] = new JPanel(new BorderLayout());
        if (layout[a][b] == '|')
          tiles[a][b].setBackground(silver);
        else
          tiles[a][b].setBackground(brown);
      }
    }
    update();
  }
  @Override
  public void repaint(){
    frame.repaint();
    grid.repaint();
    update();
  }
  //after each tick (100 milliseconds when starting), the game performs a move, and 
  public void update(){
    int scoreInt = game.getScore()*(level*2) + previousScore;
    //if the game is over, updating the board is not necessary, but a window should pop up
    if (game.isOver()){
      timer.stop();
      frame.dispose();
      dispose();
      if (game.nextLevel()){
        game = new SnakeLogic();
        new Snake(scoreInt,level);
      }
      else{
        timer.stop();
        JFrame window = new JFrame("GameOver");
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout boxes = new GridBagLayout();
        window.setSize(320,200);
        window.setLayout(boxes);
        JLabel scorePrompt = new JLabel(String.format("You scored %d!", scoreInt), SwingConstants.CENTER);
        JLabel namePrompt = new JLabel("Enter your name:", SwingConstants.CENTER);
        JTextField textField = new JTextField(15);
        JButton enter = new JButton("Enter");
        JButton cancel = new JButton("Cancel");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 30, 5);
        c.gridwidth = 2;
        c.ipadx = 160;
        c.gridy = 0;
        window.add(scorePrompt, c);
        c.insets.set(5, 5, 5, 5);
        c.gridy = 1;
        window.add(namePrompt, c);
        c.gridy = 2;
        window.add(textField, c);
        c.ipadx = 80;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 3;
        window.add(enter, c);
        c.gridx = 1;
        window.add(cancel, c);
        window.setVisible(true);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        enter.addActionListener(new entered(textField, scoreInt, window));
        cancel.addActionListener(new cancelled(window));
        return;
      }
    }
    //otherwise, a move is performed in the tick, and the JFrame is synced appropriately with the 2-d game logic
    game.move();
    score.setText(String.format("Level: %d Score: %d", level, scoreInt));
    updateCells();
  }
  public static void saveScore(String text) throws IOException{
    FileWriter highScores = new FileWriter("High Scores.txt",true);
    highScores.write(System.getProperty("line.separator"));
    highScores.write(text);
    highScores.close();
  }
  public void updateCells(){
    char[][] layout = game.getLayout();
    int[] cleanUp = game.cleanUp();
    int[] headCoords = game.getHeadCoords();
    int[] tailCoords = game.getTailCoords();
    int[] bodyCoords = game.getPreviousHeadCoords();
    int[] foodCoords = game.getFoodCoords();
    if (game.nextLevelAvailable() && ! updated){
      tiles[0][12].setBackground(Color.BLUE);
      tiles[0][12].add(wpL);
      tiles[0][13].setBackground(Color.BLUE);
      tiles[0][13].add(wpR);
      if (level < 5)
        statusBar.setText("Next level available! Make your way to the blue entrance to start it.");
      else
        statusBar.setText("You unlocked it! Be swift.");
      updated = true;
    }
    JLabel head = new JLabel(new ImageIcon("Head" + game.getCurrentDirection() + ".png"));
    JLabel tail = new JLabel(new ImageIcon("Tail" + game.getTailDirection() + ".png"));
    tiles[cleanUp[0]][cleanUp[1]].removeAll();
    tiles[cleanUp[0]][cleanUp[1]].setBackground(brown);
    tiles[headCoords[0]][headCoords[1]].setBackground(darkBrown);
    tiles[headCoords[0]][headCoords[1]].add(head, BorderLayout.CENTER);
    tiles[bodyCoords[0]][bodyCoords[1]].remove(head);
    tiles[bodyCoords[0]][bodyCoords[1]].add(body, BorderLayout.CENTER);
    tiles[tailCoords[0]][tailCoords[1]].remove(body);
    tiles[tailCoords[0]][tailCoords[1]].add(tail, BorderLayout.CENTER);
    tiles[foodCoords[0]][foodCoords[1]].setBackground(darkBrown);
    switch (layout[foodCoords[0]][foodCoords[1]]){
      case 'a':
        tiles[foodCoords[0]][foodCoords[1]].add(apple, BorderLayout.CENTER);
        return;
      case 'm':
        tiles[foodCoords[0]][foodCoords[1]].add(mouse, BorderLayout.CENTER);
        return;
      default:
        tiles[foodCoords[0]][foodCoords[1]].add(rabbit, BorderLayout.CENTER);
        return;
    }
  }
  public static void main(String[] args){
    new Snake();
  }
  private static class entered implements ActionListener{
    private JTextField textField;
    private int scoreInt;
    private JFrame window;
    entered(JTextField textField, int scoreInt, JFrame window){
      this.textField = textField;
      this.scoreInt = scoreInt;
      this.window = window;
    }
    @Override
    public void actionPerformed(ActionEvent e){
      String highScore = textField.getText() + " - " + scoreInt;
      try{
        saveScore(highScore);
      }
      catch (IOException f){
        System.out.println("Something may have gone wrong");
      }
      window.setVisible(false);
      window.dispose();
      new Snake();
      return;
    }
  }
  private static class cancelled implements ActionListener{
    private JFrame window;
    cancelled(JFrame window){
      this.window = window;
    }
    @Override
    public void actionPerformed(ActionEvent e){
      window.setVisible(false);
      window.dispose();
      return;
    }
  }
}