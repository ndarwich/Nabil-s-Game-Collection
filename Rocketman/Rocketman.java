package Rocketman;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Random;

public class Rocketman extends JFrame implements KeyListener, ActionListener{
  private ArrayList<Obstacle> obstacles; //holds all fireballs in the current level
  private ArrayList<Brick> bricks; //holds all bricks (for walls) in current level
  private ArrayList<Safespot> safespots; //holds all safespots in current level
  private ArrayList<KeyAndDoor> keyAndDoors; //holds all keys and doors in current level
  private Timer timer; //flow for the game
  private JLabel rocketman, portal, deaths; //one rocketman and one portal in a level (gameOver is an exception)
  private int x, y, savedX, savedY, portalX, portalY, level; //hold saved positions, current positions, etc
  private int numDeaths = 0; //deaths are recorded
  private boolean movingLeft = false, movingRight = false, movingUp = false, movingDown = false; //booleans 4 movement
  private Random randomPlace = new Random(0); //a random number generator
  public Rocketman(){
    //GUI basicas, naming the window, setting the size,etc..
    super("Rocket Man");
    setSize(600,410);
    setLayout(null); //null layout to have absolute positioning
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(false);
    setLocationRelativeTo(null);
    getContentPane().setBackground(new Color(3,3,3)); //setting the background color to almost black black
    timer = new Timer(15, this); //timer that controls our game movements
    //all arraylists initialized empty
    obstacles = new ArrayList<Obstacle>();
    bricks = new ArrayList<Brick>();
    safespots = new ArrayList<Safespot>();
    keyAndDoors = new ArrayList<KeyAndDoor>();
    //the deaths JLabel is initialized (it will show up in the end game over screen)
    deaths = new JLabel("Total Deaths: " + numDeaths, SwingConstants.CENTER);
    deaths.setFont(new Font("Nabil", Font.PLAIN, 26));
    deaths.setBounds(0,20,600,60);
    deaths.setForeground(Color.YELLOW);
    level = 1; //starting with level 1
    level1();
    timer.start();
    addKeyListener(this);
    setVisible(true);
  }
  private void level1(){
    //the rocketman and portal are initialized in relevant x and y positions
    initializeRocketman(20,180);
    //spacebricks are made to create boundaries for where the rocketman can go
    makeBricks(0,0,600,120);
    makeBricks(0,120,20,140);
    makeBricks(0,260,600,120);
    //circles, all are cocentric, with smaller radii
    makeCircleObstacle(125,185,60,0,true,2);
    makeCircleObstacle(265,185,60,0,true,4);
    makeCircleObstacle(405,185,60,0,false,2);
    makeCircleObstacle(125,185,40,0,true,2);
    makeCircleObstacle(265,185,40,0,true,4);
    makeCircleObstacle(405,185,40,0,false,2);
    makeCircleObstacle(125,185,20,0,true,2);
    makeCircleObstacle(265,185,20,0,true,4);
    makeCircleObstacle(405,185,20,0,false,2);
    makeCircleObstacle(125,185,60,2,true,2);
    makeCircleObstacle(265,185,60,2,false,4);
    makeCircleObstacle(405,185,60,2,false,2);
    makeCircleObstacle(125,185,40,2,true,2);
    makeCircleObstacle(265,185,40,2,false,4);
    makeCircleObstacle(405,185,40,2,false,2);
    makeCircleObstacle(125,185,20,2,true,2);
    makeCircleObstacle(265,185,20,2,false,4);
    makeCircleObstacle(405,185,20,2,false,2);
    //centers of circles, don't move at all
    makeLineObstacle(125,185,125,185,1);
    makeLineObstacle(265,185,265,185,1);
    makeLineObstacle(405,185,405,185,1);
    initializePortal(500,140);
  }
  private void level2(){
    initializeRocketman(0,180);
    makeBricks(0,0,600,60); //top bricks
    makeBricks(0,320,600,62); //bottom bricks
    //top and bottom of rocketman
    makeBricks(0,60,40,118);
    makeBricks(0,202,40,120);
    //top and bottom of safespot
    makeBricks(220,60,60,118);
    makeBricks(220,202,60,120);
    //in between fireballs
    makeBricks(70,90,120,196);
    makeBricks(310,90,120,198);
    //top and bottom of portal
    makeBricks(460,90,120,40);
    makeBricks(460,250,120,40);
    //rectangle fireballs that move clockwise
    makeRectangleObstacle(50,70,50,70,200,290,true,7.5);
    makeRectangleObstacle(290,70,290,70,440,290,true,3.9);
    //rectangle fireballs that move counterclockwise
    makeRectangleObstacle(200,290,50,70,200,290,false,7.5);
    makeRectangleObstacle(440,290,290,70,440,290,false,3.9);
    makeLineObstacle(460,66,560,66,1);
    makeLineObstacle(560,296,460,296,1);
    makeSafespot(240,180);
    initializePortal(500,140);
  }
  private void level3(){
    initializeRocketman(0,180);
    //top and bottom of rocketman
    makeBricks(0,0,60,176);
    makeBricks(0,202,60,180);
    //top and bottom overall
    makeBricks(60,0,500,40);
    makeBricks(60,340,260,40);
    makeBricks(440,340,120,40);
    makeBricks(560,0,40,380);
    //bricks that allow the alternate route
    makeBricks(300,90,140,200);
    //key and door associated with the level
    makeKeyAndDoor(420,50,300,290,20,50);
    //center 4 diagonal line fireballs
    makeLineObstacle(60,60,280,280,4);
    makeLineObstacle(280,280,60,60,4);
    makeLineObstacle(60,280,280,60,4);
    makeLineObstacle(280,60,60,280,4);
    //8 circle fireballs with same radius
    makeCircleObstacle(170,170,110,0,true,8);
    makeCircleObstacle(170,170,110,2,false,8);
    makeCircleObstacle(170,170,110,1,true,8);
    makeCircleObstacle(170,170,110,3,false,8);
    makeCircleObstacle(170,170,110,0,false,8);
    makeCircleObstacle(170,170,110,2,true,8);
    makeCircleObstacle(170,170,110,1,false,8);
    makeCircleObstacle(170,170,110,3,true,8);
    //right 6 vertical fireballs
    makeLineObstacle(442,55,442,320,1);
    makeLineObstacle(462,320,462,55,1);
    makeLineObstacle(482,55,482,320,1.5);
    makeLineObstacle(502,320,502,55,1.5);
    makeLineObstacle(522,55,522,320,3);
    makeLineObstacle(542,320,542,55,3);
    makeSafespot(300,50);
    initializePortal(340,280);
  }
  private void level4(){
    initializeRocketman(340,19);
    //top and bottom bricks
    makeBricks(140,40,380,20);
    makeBricks(140,340,460,40);
    //left and right bricks
    makeBricks(0,0,40,380);
    makeBricks(560,0,40,340);
    //wall
    makeBricks(140,60,20,280);
    //there will be 12 doors in this level that lead to the portal
    //the 12 keys are put in randomly
    for (int i = 0; i < 12; i++){
      makeKeyAndDoor(randomPlace.nextInt(380)+160,randomPlace.nextInt(260)+60,40,40+i*20,100,20);
    }
    //obstacles are made to make getting keys a bit more challenging
    makeCircleCross(280,195,true,10,6);
    makeCircleCross(480,135,false,5,3);
    makeCircleCross(480,266,true,5,2); //making them look like gears in action :)
    //hidden behind the doors are even more crosses
    makeCircleCross(85,105,true,20,2);
    makeCircleCross(85,225,false,20,2);
    initializePortal(40,280);
  }
  public void level5(){
    initializeRocketman(80,0);
    //top bricks
    makeBricks(0,0,40,20);
    makeBricks(140,0,460,20);
    //bottom bricks
    makeBricks(0,360,600,20);
    //left & right bricks
    makeBricks(0,20,20,340);
    makeBricks(580,20,20,340);
    //vertical brick wall near entrance
    makeBricks(160,20,20,300);
    //vertical brick wall on the right of the portal (where the key is)
    makeBricks(340,20,20,80);
    //horizontal wall under the portal
    makeBricks(240,100,300,20);
    //horizontal wall near bottom
    makeBricks(180,300,360,20);
    //horizontal wall above that one ^
    makeBricks(220,240,360,20);
    //key to unlock door to the left of portal
    makeKeyAndDoor(380,50,240,20,20,80);
    //X line obstacles in the beginning
    makeLineObstacle(25,25,145,145,5);
    makeLineObstacle(145,145,25,25,5);
    makeLineObstacle(145,25,25,145,5);
    makeLineObstacle(25,145,145,25,5);
    makeRectangleObstacle(25,25,25,25,145,145,true,5);
    makeRectangleObstacle(145,145,25,25,145,145,true,5);
    makeRectangleObstacle(145,25,25,25,145,145,false,5);
    makeRectangleObstacle(25,145,25,25,145,145,false,5);
    makeLineObstacle(25,165,145,285,5);
    makeLineObstacle(145,285,25,165,5);
    makeLineObstacle(145,165,25,285,5);
    makeLineObstacle(25,285,145,165,5);
    makeRectangleObstacle(25,165,25,165,145,285,true,5);
    makeRectangleObstacle(145,285,25,165,145,285,true,5);
    makeRectangleObstacle(145,165,25,165,145,285,false,5);
    makeRectangleObstacle(25,285,25,165,145,285,false,5);
    //four rectangle fireballs below the bottom extra wall
    makeRectangleObstacle(185,323,185,323,525,343,true,4);
    makeRectangleObstacle(525,343,185,323,525,343,true,4);
    makeRectangleObstacle(185,323,185,323,525,343,false,8);
    makeRectangleObstacle(525,343,185,323,525,343,false,8);
    //vertical line fireballs in between the two walls, patience is key here
    makeLineObstacle(525,260,525,287,2.5);
    makeLineObstacle(485,287,485,260,2.5);
    makeLineObstacle(445,260,445,287,2.5);
    makeLineObstacle(405,287,405,260,2.5);
    makeLineObstacle(365,260,365,287,2.5);
    makeLineObstacle(325,287,325,260,2.5);
    makeLineObstacle(285,260,285,287,2.5);
    makeLineObstacle(245,287,245,260,2.5);
    //circle crosses
    makeCircleCross(295,175,true,5,3);
    makeCircleCross(455,175,false,5,3);
    //random obstacles near key
    makeLineObstacle(365,25,565,85,4);
    makeLineObstacle(565,85,365,25,4);
    makeLineObstacle(365,85,565,25,4);
    makeLineObstacle(565,25,365,85,4);
    //more vertical fireballs near the portal
    makeLineObstacle(190,25,190,225,1);
    makeLineObstacle(215,225,215,25,1);
    //safe spots near the horizontal walls
    makeSafespot(190,240);
    makeSafespot(550,100);
    makeSafespot(550,300);
    initializePortal(250,10);
  }
  //a special game over level for users to enjoy
  public void gameOver(){
    deaths.setText("Total Deaths: " + numDeaths);
    initializeRocketman(290,40);
    //multiple portals are here, each making a new saved position rather than leading to a different level
    initializePortal(30,50);
    initializePortal(30,180);
    initializePortal(470,50);
    initializePortal(470,180);
    //border obstacles
    makeRectangleObstacle(5,5,5,5,572,360,true,5);
    makeRectangleObstacle(5,5,5,5,572,360,false,5);
    makeRectangleObstacle(572,5,5,5,572,360,true,5);
    makeRectangleObstacle(572,5,5,5,572,360,false,5);
    makeRectangleObstacle(5,360,5,5,572,360,true,5);
    makeRectangleObstacle(5,360,5,5,572,360,false,5);
    makeRectangleObstacle(572,360,5,5,572,360,true,5);
    makeRectangleObstacle(572,360,5,5,572,360,false,5);
    //jlabels that show the game is over and how many deaths the user has
    JLabel gameOver = new JLabel(new ImageIcon("Rocketman/GameOver.png"));
    gameOver.setBounds(0,0,600,410);
    add(deaths);
    add(gameOver);
    revalidate();
    repaint();
  }
  //HELPER METHODS: all are self-explanatory, easen the process of making repeated in-game objects/tasks
  //helper method to easen making wals, a top-left coord is required then the width and height
  private void makeBricks(int x,int y,int width,int height){
    Brick brick = new Brick(x,y,width,height);
    bricks.add(brick);
    add(brick);
  }
  private void makeSafespot(int x, int y){
    Safespot safespot = new Safespot(x,y);
    safespots.add(safespot);
    add(safespot);
  }
  private void makeKeyAndDoor(int keyX, int keyY, int doorX, int doorY, int doorWidth, int doorHeight){
    KeyAndDoor keyAndDoor = new KeyAndDoor(keyX, keyY, doorX, doorY, doorWidth, doorHeight);
    keyAndDoors.add(keyAndDoor);
    add(keyAndDoor.key);
    add(keyAndDoor.door);
  }
  private void makeLineObstacle(int x1, int y1, int x2, int y2, double time){
    Obstacle obstacle = new LineObstacle(x1, y1, x2, y2, time);
    obstacles.add(obstacle);
    add(obstacle);
  }
  private void makeRectangleObstacle(int x, int y, int x1, int y1, int x3, int y3, boolean clockwise, double time){
    Obstacle obstacle = new RectangleObstacle(x,y,x1,x3,y1,y3,clockwise, time);
    obstacles.add(obstacle);
    add(obstacle);
  }
  private void makeCircleObstacle(int x, int y, int radius, int position, boolean clockwise, double time){
    Obstacle obstacle = new CircleObstacle(x,y,radius,position, clockwise, time);
    obstacles.add(obstacle);
    add(obstacle);
  }
  private void makeCircleCross(int centerX, int centerY, boolean clockwise, double time, int radius){
    makeLineObstacle(centerX, centerY, centerX, centerY, 1);
    for (int i = 1; i <= radius; i++){
      makeCircleObstacle(centerX, centerY, i*20, 0, clockwise, time);
      makeCircleObstacle(centerX, centerY, i*20, 1, clockwise, time);
      makeCircleObstacle(centerX, centerY, i*20, 2, clockwise, time);
      makeCircleObstacle(centerX, centerY, i*20, 3, clockwise, time);
    }
  }
  private void initializeRocketman(int x, int y){
    rocketman = new JLabel(new ImageIcon("Rocketman/rocketman.png"));
    this.x = x;
    this.y = y;
    savedX = x;
    savedY = y;
    rocketman.setBounds(x, y, 20, 20);
    add(rocketman);
    rocketman.revalidate();
    rocketman.repaint();
  }
  private void initializePortal(int x, int y){
    portal = new JLabel(new ImageIcon("Rocketman/portal.gif"));
    portalX = x+50;//+50 to allow the rocketman to hit the center of the portal
    portalY = y+50;
    portal.setBounds(x, y, 100, 100);
    add(portal);
    portal.revalidate();
    portal.repaint();
  }
  //checks for game over or being out of bounds
  private void check(){
    boolean reset = false;
    //special case when the game is over and you go in any of the four portals, which will all reset to a random place
    if (level >= 6){
      if ((x <= 80 && 60 <= x && y <= 100 && 80 <= y) || (x <= 80 && 60 <= x && y <= 230 && 210 <= y)
            || (x <= 520 && 500 <= x && y <= 100 && 80 <= y) || (x <= 520 && 500 <= x && y <= 230 && 210 <= y)){
        savedX = randomPlace.nextInt(580);
        savedY = randomPlace.nextInt(360);
        reset();
        return;
      }
    }
    //checking if the level was completed
    if (x <= portalX && portalX <= x + 20 && y <= portalY && portalY <= y + 20){
      nextLevel();
      return;
    }
    for (Obstacle o: obstacles){
      if (x <= o.x && o.x <= x + 20 && y <= o.y && o.y <= y + 20){
        reset = true;
      }
      else if (x <= o.x+14 && o.x+14 <= x + 20  && y <= o.y && o.y <= y + 20){
        reset = true;
      }
      else if (x <= o.x && o.x <= x + 20 && y <= o.y+14 && o.y+14 <= y + 20){
        reset = true;
      }
      else if (x <= o.x+14 && o.x+14 <= x + 20 && y <= o.y+14 && o.y+14 <= y + 20){
        reset = true;
      }
    }
    if (reset){
      numDeaths++;
      if (level >= 6)
        deaths.setText("Total Deaths: " + numDeaths);
      reset();
    }
  }
  //moves on with the game (when the portal is touched)
  private void nextLevel(){
    level++;
    resetAll();
    switch(level){
      case 1:
        level1();
        break;
      case 2:
        level2();
        break;
      case 3:
        level3();
        break;
      case 4:
        level4();
        break;
      case 5:
        level5();
        break;
      default:
        //debug - System.out.println(level);
        gameOver();
        break;
    }
    reset();
    revalidate();
    repaint();
  }
  //resets portal man to saved coordinates
  private void reset(){
    x = savedX;
    y = savedY;
  }
  //removes everything from the screen to prepare for the next level, also clearing all internals
  private void resetAll(){
    timer.stop();
    reset();
    remove(rocketman);
    remove(portal);
    portal.revalidate();
    portal.repaint();
    for (Obstacle obstacle: obstacles){
      remove(obstacle);
    }
    obstacles.clear();
    for (Brick brick: bricks){
      remove(brick);
    }
    bricks.clear();
    for (Safespot safespot: safespots){
      remove(safespot);
    }
    safespots.clear();
    for (KeyAndDoor keyAndDoor: keyAndDoors){
      //for unopened doors
      remove(keyAndDoor.key);
      remove(keyAndDoor.door);
    }
    keyAndDoors.clear();
    if (level != 1)
      timer.start();
  }
  //releasing a movement key will stop the movement of portal man with the in-game timer
  @Override
  public void keyReleased(KeyEvent event){
    int k = event.getKeyCode();
    if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP){
      movingUp = false;
    }
    if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT){
      movingLeft = false;
    }
    if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN){
      movingDown = false;
    }
    if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT){
      movingRight = false;
    }
    //DEBUG - can go to the next level easily
    if (k == KeyEvent.VK_N){
      nextLevel();
    }
  }
  //pressing a key will make portal man move with the in-game timer
  @Override
  public void keyPressed(KeyEvent event){
    int k = event.getKeyCode();
    if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP){
      movingUp = true;
    }
    if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT){
      movingLeft = true;
    }
    if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN){
      movingDown = true;
    }
    if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT){
      movingRight = true;
    }
  }
  @Override
  public void keyTyped(KeyEvent event){}
  //portal man moves with each clock tick, also checking if hes not out of bounds
  @Override public void actionPerformed(ActionEvent event){
    int speed = 2;
    if (movingUp){
      if (withinBounds(x,y-speed))
        y-=speed;
    }
    if (movingLeft){
      if (withinBounds(x-speed,y))
        x-=speed;
    }
    if (movingDown){
      if (withinBounds(x,y+speed))
        y+=speed;
    }
    if (movingRight){
      if (withinBounds(x+speed,y))
        x+=speed;
    }
    rocketman.setBounds(x, y, 20, 20);
    rocketman.revalidate();
    rocketman.repaint();
    for (Obstacle o: obstacles){
      o.move();
    }
    check();
  }
  //helper method to help rocketman avoid going inside walls/out of bounds
  private boolean withinBounds(int x, int y){
    //out of the map (when it's outside the jframe's boundaries
    if (y < 0 || y > 363 || x < 0 || x > 573){
      return false;
    }
    //checks each of the rectangle bricks, if inside, return false
    for (Brick brick: bricks){
      //all corners of our 20x20 rocketman are checked
      if ((x >= brick.x && x <= brick.x + brick.width || x+20 >= brick.x && x+20 <= brick.x + brick.width) 
            && (y+20 >= brick.y && y+20 <= brick.y + brick.height || y >= brick.y && y <= brick.y + brick.height))
        return false;
    }
    KeyAndDoor removableKAD = null;
    //checking doors, which are essentially bricks, yet also checking if the keys were hit
    for (KeyAndDoor kAD: keyAndDoors){
      //the key's boundaries are checked, if portal man is within it, it gets picked up
      if ((x >= kAD.keyX && x <= kAD.keyX + 20 || x+20 >= kAD.keyX && x+20 <= kAD.keyX + 20) &&
          (y >= kAD.keyY && y <= kAD.keyY + 20 || y+20 >= kAD.keyY && y+20 <= kAD.keyY + 20)){
        removableKAD = kAD;
      }
      //all corners of our 20x20 rocketman and the door itself
      if ((x >= kAD.doorX && x <= kAD.doorX + kAD.doorWidth || x+20 >= kAD.doorX && x+20 <= kAD.doorX + kAD.doorWidth) 
            && (y+20 >= kAD.doorY && y+20 <= kAD.doorY + kAD.doorHeight || y >= kAD.doorY && y <= kAD.doorY + kAD.doorHeight))
        return false;
    }
    //if the key was picked up, the key and door are removed from the screen
    if (removableKAD != null){
      remove(removableKAD.key);
      remove(removableKAD.door);
      keyAndDoors.remove(removableKAD);
      revalidate();
      repaint();
    }
    //checking for within bounds of any safespot, saving if the rocketman is touching it
    for (Safespot safespot: safespots){
      if ((x >= safespot.x && x <= safespot.x + 20 || x+20 >= safespot.x && x+20 <= safespot.x + 20) &&
          (y >= safespot.y && y <= safespot.y + 20 || y+20 >= safespot.y && y+20 <= safespot.y + 20)){
        savedX = safespot.x;
        savedY = safespot.y;
      }
    }
    return true;
  }
  public static void main(String[] args){
    new Rocketman();
  }
}