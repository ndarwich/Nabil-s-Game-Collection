import java.util.*;

//Bored during winter break, might as well make a snake game :) This class contains all the logic and 2-d rep
public class SnakeLogic{
  //enum for the different states of the game
  protected enum GameState{Playing, Paused, Over};
  //enum for the cardinal directions, and an O when the game is starting
  protected enum Direction{ N, S, E, W, O, NE, NW, SW, SE, EN, ES, WN, WS;
    //retrieves the opposite direction, useful for determining valid moves
    private static Direction oppositeDirection(Direction d){
      switch (d){case N: return S; case S: return N; case E: return W; case W: return E; default: return S; }
    }
    private static Direction fromString(String rep){
      char secondChar = '.';
      if (rep.length() == 2)
        secondChar = rep.charAt(1);
      switch(rep.charAt(0)){
        case 'E':
          if (secondChar == 'N')
          return EN;
          else if (secondChar == 'S')
            return ES;
          return E;
        case 'W':
          if (secondChar == 'N')
          return WN;
          else if (secondChar == 'S')
            return WS;
          return W;
        case 'N':
          if (secondChar == 'E')
          return NE;
          else if (secondChar == 'W')
            return NW;
          return N;
        case 'S':
          if (secondChar == 'E')
          return SE;
          else if (secondChar == 'W')
            return SW;
          return S;
        default:
          return O;
      }
    }
    @Override
    //O is represented as an N as in the beginning of the game the snake's head is facing North
    public String toString(){
      switch (this){case S: return "S"; case E: return "E"; case W: return "W";  case NE: return "NE"; 
        case SE: return "SE"; case SW: return "SW"; case NW: return "NW"; case ES: return "ES";
        case WS: return "WS"; case WN: return "WN"; case EN: return "EN"; default: return "N"; }
    }
  }
  private char[][] layout;
  private Direction currentDirection, pastDirection; //pastDirection will keep track of positions of the body
  private GameState currentState;
  //row, column coordinates of the head, pretail, and tail (the important parts) of the snakes are all kept
  private int[] headCoords = new int[2], preTailCoords = new int[2], tailCoords = new int[2], previousTC = new int[2], 
    previousHC = new int[2], foodCoords = new int[2];
  private int size;
  private static int impendingParts;
  private LinkedList<Integer> headSpots;
  private boolean nextLevel, nextLevelAvailable, moveMade;
  //constructor, creates the layout for the beginning of the board, and spawns a food item randomly
  public SnakeLogic(){
    currentState = GameState.Playing;
    currentDirection = Direction.O;
    pastDirection = Direction.N;
    int rows = 22, cols = 26;
    size = 0; //the snake starts out as size 0 for reference when determining the preTail
    impendingParts = 0;
    //moveMade boolean is set to true to effectively avoid a bug in which a person presses to keys too fast
    moveMade = true;
    layout = new char[rows][cols];
    headSpots = new LinkedList<Integer>();
    //nested loop to fill in all 2-d coordinates in the layout according to my vision~
    for (int i = 0; i < rows; i++){
      for (int j = 0; j < cols; j++){
        if (i % (rows - 1) == 0 || j % (cols - 1) == 0)
          layout[i][j] = '|';
        else if (i == rows/2 && j == cols/2){ //sets the snake in the middle of the layout when starting out
          layout[i-1][j] = 'h'; //the head is set first, and it will be one row above (i-1) and at the same column
          headCoords[0] = i-1;
          headCoords[1] = j;
          headSpots.add(i-1);
          headSpots.add(j);
          layout[i][j] = 'o'; //the pretail is set
          preTailCoords[0] = i;
          preTailCoords[1] = j;
          layout[i+1][j] = 't'; //the tail is set
          tailCoords[0] = i+1;
          tailCoords[1] = j;
          previousTC[0] = i+2;
          previousTC[1] = j;
          previousHC[0] = i;
          previousHC[1] = j;
        }
        //spots that are not in corners are empty spots
        else if (i != rows/2+1 || j != cols/2)
          layout[i][j] = '.';
      }
    }
    nextLevel = false;
    spawnFood(); //yummy food is spawned for the little snake to start growing :)
  }
  //most important method, which determines the snake's shape after moving, as well as the status of the game
  public void move(){
    //moves are disallowed when the game is paused/not started/over (will prevent bugs)
    if (currentDirection == Direction.O || currentState == GameState.Paused)
      return;
    if (currentState == GameState.Over)
      end();
    int previousSize = size; //the previous size of the snake is recorded, to help determine impending parts
    int originalHeadI = headCoords[0], originalHeadJ = headCoords[1]; //original head coordinates are noted too
    previousHC[0] = originalHeadI;
    previousHC[1] = originalHeadJ;
    if (moveMade)
      pastDirection = currentDirection;
    switch (currentDirection){
      case N:
        headCoords[0] -= 1;
        moveMade = true;
        break;
      case S:
        headCoords[0] += 1;
        moveMade = true;
        break;
      case E:
        headCoords[1] += 1;
        moveMade = true;
        break;
      case W:
        headCoords[1] -= 1;
        moveMade = true;
        break;
      default:
        return;
    }
    if (size >= 30){
      nextLevelAvailable = true;
      layout[0][12] = 'e';
      layout[0][13] = 'e';
    }
    int i = headCoords[0], j = headCoords[1];
    headSpots.add(i);
    headSpots.add(j);
    //code for moving into a block
    if (layout[i][j] != '.'){
      switch (layout[i][j]){
        case 'r': //rabbit grows the size by 3, and a new food is spawned
          impendingParts++;
        case 'm':
          impendingParts++;
        case 'a':
          impendingParts++;
          spawnFood();
          break;
        case 'e':
          nextLevel = true;
          end();
          break;
        default:
          end();
          break;
      }
    }
    layout[i][j] = 'h';
    if (impendingParts > 0){
      impendingParts--;
      size++;
    }
    else{
      int a = tailCoords[0], b = tailCoords[1];
      if (layout[a][b] != 'h'){
        layout[a][b] = '.'; //an empty (open) spot replaces the tail (if the head didnt catch up
        previousTC[0] = a;
        previousTC[1] = b;
      }
      int c = preTailCoords[0], d = preTailCoords[1];
      tailCoords[0] = c;
      tailCoords[1] = d;
      layout[c][d] = 't'; //the tail is placed where the pretail once was
      preTailCoords[0] = headSpots.removeFirst();
      preTailCoords[1] = headSpots.removeFirst();
      int e = preTailCoords[0], f = preTailCoords[1]; //the pretail is relocated according to previous head spots
      layout[e][f] = 'o';
    }
    if (size != 0)
      layout[originalHeadI][originalHeadJ] = '='; //any part of the body will just be an =, except for the pretail
    else
      layout[originalHeadI][originalHeadJ] = 'o';
  }
  //changes the current direction that the snake is expected to move to when performing a move()
  public void changeDirection(Direction d){
    //first checks in any moves have been made in the current direction prior to changing directions
    //if a direction is invalid/the same as the current one, nothing is to be done/
    //if the game is paused/over, directions can't change!
    if (! moveMade || currentDirection == d || d == Direction.oppositeDirection(currentDirection) 
          || currentState == GameState.Paused || currentState == GameState.Over) 
      return;
    moveMade = false;
    pastDirection = currentDirection;
    String cornerMove = currentDirection.toString() + d.toString();
    pastDirection = d.fromString(cornerMove);
    currentDirection = d;
  }
  //generates two random integers, which need to be located in an empty spot to be valid
  public int[] generateTwoInts(){
    Random rand = new Random();
    //avoids borders efficiently for the while loop
    int x = rand.nextInt(19), y = rand.nextInt(23);
    while (layout[x][y] != '.'){ //the while loop in turn will find an open space for the food to spawn
      x = rand.nextInt(20) + 1; 
      y = rand.nextInt(24) + 1; 
    }
    int[] nums = {x, y};
    return nums;
  }
  //randomly spawns one of three foods, each of which has a relative size advantage
  public void spawnFood(){
    Random rand = new Random();
    int a = rand.nextInt(3);
    foodCoords = generateTwoInts();
    //spawns the type of food appropriately
    if (a == 0)
      layout[foodCoords[0]][foodCoords[1]] = 'a'; //spawns an apple (size += 1)
    else if (a == 1)
      layout[foodCoords[0]][foodCoords[1]] = 'm'; //spawns a mouse (size += 2)
    else if (a == 2)
      layout[foodCoords[0]][foodCoords[1]] = 'r'; //spawns a rabbit (size += 3)
  }
  public boolean pause(){
    if (currentState == GameState.Playing){
      currentState = GameState.Paused;
      return true;
    }
    currentState = GameState.Playing;
    return false;
  }
  public void end(){
    currentState = GameState.Over;
  }
  
  //returns the boolean for whether or not the next level is accessible
  public boolean nextLevelAvailable(){
    return nextLevelAvailable;
  }
  
  //returns the boolean for whether or not the snake's head is at the level's end
  public boolean nextLevel(){
    return nextLevel; 
  }
  
  //debugging print for playing in the compiler itself
  public String printLayout(){
    String layoutString = "";
    for (int i = 0; i < layout.length; i++){
      for (int j = 0; j < layout[i].length; j++){
        layoutString += layout[i][j] + " ";
      }
      layoutString += "\n";
    }
    return layoutString;
  }
  
  //finds out the direction of the tail based on the position of the pretail
  public String getTailDirection(){
    if (preTailCoords[0] - tailCoords[0] == -1)
      return Direction.N.toString();
    else if (preTailCoords[0] - tailCoords[0] == 1)
      return Direction.S.toString();
    else if (preTailCoords[1] - tailCoords[1] == -1)
      return Direction.W.toString();
    else
      return Direction.E.toString();
  }
  
  //retrieves the current layout, extremely useful for paints/repaints in the main class
  public char[][] getLayout(){
    return layout;
  }
  
  //gets the current direction of the snake, useful for determining where will the head point to
  public String getCurrentDirection(){
    return currentDirection.toString();
  }
  
  public String getPastDirection(){
    return pastDirection.toString();
  }
  public int getScore(){
    return ((size*220)/7);
  }
  
  public int[] getHeadCoords(){
    return headCoords;
  }
  public int[] getTailCoords(){
    return tailCoords;
  }
  public int[] cleanUp(){
    return previousTC;
  }
  public int[] getPreviousHeadCoords(){
    return previousHC;
  }
  public int[] getFoodCoords(){
    return foodCoords;
  }
  //determines whether or not the game goes on in the main class
  public boolean isOver(){
    return currentState == GameState.Over;
  }
}