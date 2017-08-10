package Snake; //used for the parent (MyGames) to import it.

import java.awt.*; //colors and borders
import java.awt.event.*; //listeners and 
import javax.swing.*; //jpanels and jframes and jstuff
import java.io.*; //opening and writing in the highScores file

/* Update 1/5/2016 - Finally finished this program, what took me significant time to figure out was properly updating
 * JPanels throughout the board, having the new frame for when the game ends and the user submits their score (more
 * actionListeners, new layout, PADDING, having the logic sync completely with the frame, and complicated methods). 
 * Making the different PNGs and GIFs also took a HUGE while!*/

//A friendly game, with a twist! You can now level up and play with an even faster snake! Or tactically rack up points
//at the current level. Controls are wasd/arrow keys to move, space to pause, esc to end the game.
public class Snake extends JFrame implements KeyListener, ActionListener{
  private JFrame frame; //the frame that the user will see
  private JPanel tiles[][], grid; //tiles -> 2-d representation of 2-d board elements, grid -> the 2-d board itself
  private SnakeLogic game; //the logic that will run this game
  private final JLabel statusBar, score; //text that will guide the user throughout the game
  //different color codes for colors used throughout the interface
  private final Color brown = new Color(81,38,14), silver = new Color(192,192,192), darkBrown = new Color(60,38,14);
  private int level, previousScore; //used to keep track 
  private boolean updated, justMoved; //boolean to see if the board has been updated yet to contain blue entrances
  //different constant JLabels to hold icons of images/gifs saved in the directory. Will be used in the gui.
  private final JLabel apple = new JLabel(new ImageIcon("Snake/Images/Apple.png"), JLabel.CENTER), 
    mouse = new JLabel(new ImageIcon("Snake/Images/Mouse.png"), JLabel.CENTER), 
    rabbit = new JLabel(new ImageIcon("Snake/Images/Rabbit.png"), JLabel.CENTER),
    //gifs for next level available
    wpL = new JLabel(new ImageIcon("Snake/Images/wpL.gif"), JLabel.CENTER),
    wpR = new JLabel(new ImageIcon("Snake/Images/wpR.gif"), JLabel.CENTER);
  private Timer timer; //actions are performed during ticks from this timer
  public Snake(){
    updated = false;
    tiles = new JPanel[22][26];
    timer = new Timer(100, this);
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
    justMoved = false;
    frame.setResizable(true);
    frame.setLocationRelativeTo(null); //centers the window
    frame.add(grid);
    frame.add(statusBar, BorderLayout.NORTH);
    frame.add(score, BorderLayout.SOUTH);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //dispose as MyGames may be open
    frame.addKeyListener(this);
    previousScore = 0;
    level = 1;
    timer.start();
    frame.setVisible(true);
  }
  public Snake(int score, int level){
    this();
    timer.stop();
    previousScore = score;
    timer = new Timer(100 - (15*(level)), this);
    if (level+1 < 5)
      statusBar.setText(String.format("You are currently in level %d, foods are worth %dx as much.", ++level, level*2-2));
    else
      statusBar.setText("You are currently at the highest level. Quickly collect points to unlock the entrance again!");
    this.level = level;
    timer.start();
  }
  @Override
  public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP){
      game.changeDirection(SnakeLogic.Direction.N);
      justMoved = true;
      update();
    }
    else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN){
      game.changeDirection(SnakeLogic.Direction.S);
      justMoved = true;
      update();
    }
    else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT){
      game.changeDirection(SnakeLogic.Direction.W);
      justMoved = true;
      update();
    }
    else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT){
      game.changeDirection(SnakeLogic.Direction.E);
      update();
      justMoved = true;
    }
    else if (keyCode == KeyEvent.VK_SPACE){
      if (game.pause()) //calls the pause method on SnakeLogic and checks the boolean to determine the timer progress
        timer.stop();
      else
        timer.start();
    }
    else if ((keyCode == KeyEvent.VK_ESCAPE)){
      if (game.pause()) //calls the pause method on SnakeLogic and checks the boolean to determine the timer progress
        timer.stop();
      else{
        timer.start();
        game.end();
      }
    }
  }
  
  @Override public void keyReleased(KeyEvent e){}
  @Override public void keyTyped(KeyEvent e){}
  @Override public void actionPerformed(ActionEvent e){
    update();
  }
  //the frame will sync with the current game's state  in the paint() and update() methods, while performing actions
  private void paint(){
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
  //after each tick (100 milliseconds when starting), the game performs a move, and 
  private void update(){
    if (justMoved){
      justMoved = false;
      return;
    }
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
        window.setSize(320,200);
        //the new window will have a GridBagLayout, suitable for having multiple buttons/textfields
        GridBagLayout boxes = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints(); //constraints are put to each item added to the frame
        window.setLayout(boxes);
        JLabel scorePrompt = new JLabel(String.format("You scored %d!", scoreInt), SwingConstants.CENTER);
        JLabel namePrompt = new JLabel("Enter your name:", SwingConstants.CENTER);
        JTextField textField = new JTextField(15);
        JButton enter = new JButton("Submit");
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
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
  private static void saveScore(String text) throws IOException{
    FileWriter highScores = new FileWriter("Snake/High Scores.txt",true);
    highScores.write(System.getProperty("line.separator"));
    highScores.write(text);
    highScores.close();
  }
  private void updateCells(){
    int[] cleanUp = game.cleanUp(), headCoords = game.getHeadCoords(), tailCoords = game.getTailCoords(), 
      bodyCoords = game.getPreviousHeadCoords(), foodCoords = game.getFoodCoords();
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
    //the head, body, and tail are chosen accordingly to the fields in the SnakeLogic, files exist for all directions
    JLabel head = new JLabel(new ImageIcon("Snake/Images/Head" + game.getCurrentDirection() + ".png"));
    JLabel tail = new JLabel(new ImageIcon("Snake/Images/Tail" + game.getTailDirection() + ".png"));
    JLabel body = new JLabel(new ImageIcon("Snake/Images/Body" + game.getPastDirection() + ".png"));
    //this took me a long time but i finally realized that revalidating & repainting fixes the removal and readdition
    //of jlabels within jpanels
    int h1 = headCoords[0], h2 = headCoords[1], b1 = bodyCoords[0], b2 = bodyCoords[1], c1 = cleanUp[0], c2 = cleanUp[1],
      t1 = tailCoords[0], t2 = tailCoords[1], f1 = foodCoords[0], f2 = foodCoords[1];
    //cleans up tiles after the tail passed them
    tiles[c1][c2].removeAll();
    tiles[c1][c2].revalidate();
    tiles[c1][c2].repaint();
    tiles[c1][c2].setBackground(brown);
    //adjusting the head JPanel
    tiles[h1][h2].removeAll();
    tiles[h1][h2].add(head);
    tiles[h1][h2].revalidate();
    tiles[h1][h2].repaint();
    //adjusting the body JPanel
    tiles[b1][b2].removeAll();
    tiles[b1][b2].add(body);
    tiles[b1][b2].revalidate();
    tiles[b1][b2].repaint();
    //adjusting the tail JPanel
    tiles[t1][t2].removeAll();
    tiles[t1][t2].add(tail);
    tiles[t1][t2].revalidate();
    tiles[t1][t2].repaint();
    //adjusting the food JPanel
    tiles[f1][f2].setBackground(darkBrown);
    //the layout from the game is taken to retrieve information about the available food
    char[][] layout = game.getLayout();
    //foods are spwaned according to the layout
    switch (layout[f1][f2]){
      case 'a':
        tiles[f1][f2].add(apple, BorderLayout.CENTER);
        return;
      case 'm':
        tiles[f1][f2].add(mouse, BorderLayout.CENTER);
        return;
      default:
        tiles[f1][f2].add(rabbit, BorderLayout.CENTER);
        return;
    }
  }
  
  public static void main(String[] args){
    new Snake();
  }
  
  //event classes, will perform appropriate actions when relative buttons are clicked
  private static class entered implements ActionListener{
    private JTextField textField;
    private int scoreInt;
    private JFrame window;
    private entered(JTextField textField, int scoreInt, JFrame window){
      this.textField = textField;
      this.scoreInt = scoreInt;
      this.window = window;
    }
    //writes the name and high scoreof the user into the text file
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
      return;
    }
  }
  private static class cancelled implements ActionListener{
    private JFrame window;
    private cancelled(JFrame window){
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