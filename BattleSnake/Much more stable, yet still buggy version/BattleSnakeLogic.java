package BattleSnake;

import java.util.LinkedList;
import java.util.Random;

public class BattleSnakeLogic{
  private int numPlayers, impendingRParts = 0, impendingBParts = 0, impendingGParts = 0, impendingYParts = 0;
  //chose to have chars for directions rather than an enum; directions are kept for each body part
  private char currentRDirection, currentBDirection, currentGDirection, currentYDirection, currentRTailDirection,
    currentBTailDirection, currentGTailDirection, currentYTailDirection,currentRBodyDirection, currentBBodyDirection, 
    currentGBodyDirection, currentYBodyDirection;;
  private boolean paused; //instead of an enum, choosing a boolean for the gamemostate, as it's simpler
  //body is simply the start of the body after the tail, but not actual body parts
  protected int[] rHead = new int[2], rBody = new int[2], rTail = new int[2], bHead = new int[2], bBody = new int[2], 
    bTail = new int[2], gHead = new int[2], gBody = new int[2], gTail = new int[2], yHead = new int[2], 
    yBody = new int[2], yTail = new int[2], previousRHead = new int[2], previousBHead = new int[2], 
    previousGHead = new int[2], previousYHead = new int[2], previousRTail = new int[2], previousBTail = new int[2],
    previousGTail = new int[2], previousYTail = new int[2], food1 = new int[2], food2 = new int[2], food3 = new int[2],
    food4 = new int[2];
  //yellow and green start out as dead unless there are actually 3/4 players
  private boolean justRMoved = false, justBMoved = false, justGMoved = false, justYMoved = false, rDead = false, 
    bDead = false, gDead = true, yDead = true, rGrown = false, bGrown = false, gGrown = false, yGrown = false,
    changedRDir = false, changedBDir = false, changedGDir = false, changedYDir = false;
  //for foods, they all need to be painted in the frame initially, BattleSnake is where these booleans get modified
  protected boolean food1eaten = true, food2eaten = true, food3eaten = true, food4eaten = true;
  //a new layout, instead of dots for empty spots; null characters (\0), which look like blank spaces, will do :)
  private char[][] layout = new char[42][60];
  private LinkedList<Integer> rHeadSpots = new LinkedList<Integer>(), bHeadSpots = new LinkedList<Integer>(),
    gHeadSpots = new LinkedList<Integer>(), yHeadSpots = new LinkedList<Integer>();
  public BattleSnakeLogic(int players){
    numPlayers = players;
    if (players > 4)
      numPlayers = 4;
    else if (players < 2)
      numPlayers = 2;
    paused = true; //the game starts out as paused rather than playing
    currentRDirection = 'D';
    currentRBodyDirection = 'D';
    currentRTailDirection = 'D';
    currentBDirection = 'A';
    currentBBodyDirection = 'A';
    currentBTailDirection = 'A';
    spawnSnake('R');
    if (numPlayers >= 3){
      gDead = false;
      currentGDirection = 'D';
      currentGBodyDirection = 'D';
      currentGTailDirection = 'D';
      spawnSnake('G');
      if (numPlayers == 4){
        yDead = false;
        currentYDirection = 'A';
        currentYBodyDirection = 'A';
        currentYTailDirection = 'A';
        spawnSnake('Y');
      }
    }
    setUpSnakes();
    setUpLayout();
    spawnFood1();
    spawnFood2();
    spawnFood3();
    spawnFood4();
    spawnPowerUp();
    spawnPowerUp();
  }
  
  protected void move(){
    if (paused)
      return;
    moveR();
    moveB();
    if (numPlayers >= 3){
      moveG();
      if (numPlayers == 4)
        moveY();
    }
  }
  
  protected void moveR(){
    if (justRMoved || rDead){ //prevents duplicate moves from changing direction by falsifying the once true boolean
      justRMoved = false;
      changedRDir = false;
      return;
    }
    if (! changedRDir)
      currentRBodyDirection = currentRDirection;
    previousRHead[0] = rHead[0];
    previousRHead[1] = rHead[1];
    int[] newHead = moveHead(rHead, currentRDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      rDie();
      return;
    }
    if (layout[newHead[0]][newHead[1]] != '\0'){
      if (layout[newHead[0]][newHead[1]] == '-'){
        impendingRParts += 2;
        spawnFood1();
        food1eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '='){
        impendingRParts += 2;
        spawnFood2();
        food2eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '0'){
        impendingRParts += 2;
        spawnFood3();
        food3eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '8'){
        impendingRParts += 2;
        spawnFood4();
        food4eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == 'p')
        return;
      else if (layout[newHead[0]][newHead[1]] == 'e')
        return;
      else{
        rDie();
        return;
      }
    }
    rHead[0] = newHead[0];
    rHead[1] = newHead[1];
    layout[rHead[0]][rHead[1]] = 'w';
    rHeadSpots.add(rHead[0]);
    rHeadSpots.add(rHead[1]);
    if (impendingRParts > 0){
      impendingRParts--;
      if (! rGrown)
        rGrown = true;
    }
    else{
      if (! rHead.equals(rTail)){
        layout[rTail[0]][rTail[1]] = '\0';
        previousRTail[0] = rTail[0];
        previousRTail[1] = rTail[1];
     }
      rTail[0] = rBody[0];
      rTail[1] = rBody[1];
      rBody[0] = rHeadSpots.removeFirst();
      rBody[1] = rHeadSpots.removeFirst();
      currentRTailDirection = newTailDir(rBody, rTail);
      layout[rTail[0]][rTail[1]] = 'x';
    }
    if (rGrown)
      layout[previousRHead[0]][previousRHead[1]] = 'a';
    else
      layout[rBody[0]][rBody[1]] = 's';
  }
  
  protected void moveB(){
    if (justBMoved || bDead){
      justBMoved = false;
      return;
    }
    if (! changedBDir)
      currentBBodyDirection = currentBDirection;
    previousBHead[0] = bHead[0];
    previousBHead[1] = bHead[1];
    int[] newHead = moveHead(bHead, currentBDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      bDie();
      return;
    }
    if (layout[newHead[0]][newHead[1]] != '\0'){
      if (layout[newHead[0]][newHead[1]] == '-'){
        impendingBParts += 2;
        spawnFood1();
        food1eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '='){
        impendingBParts += 2;
        spawnFood2();
        food2eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '0'){
        impendingBParts += 2;
        spawnFood3();
        food3eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '8'){
        impendingBParts += 2;
        spawnFood4();
        food4eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == 'p')
        return;
      else if (layout[newHead[0]][newHead[1]] == 'e')
        return;
      else{
        bDie();
        return;
      }
    }
    bHead[0] = newHead[0];
    bHead[1] = newHead[1];
    layout[bHead[0]][bHead[1]] = 'r';
    bHeadSpots.add(bHead[0]);
    bHeadSpots.add(bHead[1]);
    if (impendingBParts > 0){
      impendingBParts--;
      if (! bGrown)
        bGrown = true;
    }
    else{
      if (! bHead.equals(bTail)){
        layout[bTail[0]][bTail[1]] = '\0';
        previousBTail[0] = bTail[0];
        previousBTail[1] = bTail[1];
     }
      bTail[0] = bBody[0];
      bTail[1] = bBody[1];
      bBody[0] = bHeadSpots.removeFirst();
      bBody[1] = bHeadSpots.removeFirst();
      currentBTailDirection = newTailDir(bBody, bTail);
      layout[bTail[0]][bTail[1]] = 'v';
    }
    if (bGrown)
      layout[previousBHead[0]][previousBHead[1]] = 'd';
    else
      layout[bBody[0]][bBody[1]] = 'f';
  }
  
  private void moveG(){
    if (justGMoved || gDead){
      justGMoved = false;
      return;
    }
  }
  
  private void moveY(){
    if (justYMoved || yDead){ //prevents duplicate moves from changing direction by falsifying the once true boolean
      justYMoved = false;
      changedYDir = false;
      return;
    }
    if (! changedYDir)
      currentYBodyDirection = currentYDirection;
    previousYHead[0] = yHead[0];
    previousYHead[1] = yHead[1];
    int[] newHead = new int[2];
    moveHead(previousYHead, currentYDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      yDie();
      return;
    }
    if (layout[newHead[0]][newHead[1]] != '\0'){
      if (layout[newHead[0]][newHead[1]] == '-'){
        impendingYParts += 2;
        spawnFood1();
        food1eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '='){
        impendingYParts += 2;
        spawnFood2();
        food2eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '0'){
        impendingYParts += 2;
        spawnFood3();
        food3eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == '8'){
        impendingYParts += 2;
        spawnFood4();
        food4eaten = true;
      }
      else if (layout[newHead[0]][newHead[1]] == 'p')
        return;
      else if (layout[newHead[0]][newHead[1]] == 'e')
        return;
      else{
        yDie();
        return;
      }
    }
    yHead[0] = newHead[0];
    yHead[1] = newHead[1];
    layout[yHead[0]][yHead[1]] = 'i';
    yHeadSpots.add(yHead[0]);
    yHeadSpots.add(yHead[1]);
    if (impendingYParts > 0){
      impendingYParts--;
      if (! yGrown)
        yGrown = true;
    }
    else{
      if (! yHead.equals(yTail)){
        layout[yTail[0]][yTail[1]] = '\0';
        previousYTail[0] = yTail[0];
        previousYTail[1] = yTail[1];
     }
      yTail[0] = yBody[0];
      yTail[1] = yBody[1];
      yBody[0] = yHeadSpots.removeFirst();
      yBody[1] = yHeadSpots.removeFirst();
      currentYTailDirection = newTailDir(yBody, yTail);
      layout[yTail[0]][yTail[1]] = ',';
    }
    if (yGrown)
      layout[previousYHead[0]][previousYHead[1]] = 'j';
    else
      layout[yBody[0]][yBody[1]] = 'k';
  }
  
  private char oppositeDirection(char dir){
    switch(dir){
      case 'W':
        return 'S';
      case 'S':
        return 'W';
      case 'A':
        return 'D';
      default:
          return 'A';
    }
  }
  
  private void spawnSnake(char snakeColor){
    switch (snakeColor){
      case 'R':
      case 'B':
        //the snakes will be in the same row in the middle if only two are present
        rBody[0] = 21; //42/2
        rBody[1] = 20; //60/3
        bBody[0] = 21; //42/2
        bBody[1] = 40; //60*2/3
        return;
      case 'G':
        //otherwise the snakes will be distributed evenly throughout the board, green snake in the middle
        rBody[0] = 14; //42/3 to leave space for a row for the green snake
        bBody[0] = 14;
        gBody[0] = 28; //42*2/3 now the green snake has its own row
        gBody[1] = 30; //60/2 for it to be in the middle column
        return;
      case 'Y':
        //the green snake's column will need to change from the middle if the fourth snake is present
        gBody[1] = 20;
        yBody[0] = 28; //will be in the same row as green
        yBody[1] = 40;
        return;
      default:
        return;
    }
  }
  
  private void setUpLayout(){
    //xsw will be the the red snake (a for body), vfr for the blue snake (d for body), and so on...
    layout[rHead[0]][rHead[1]] = 'w';
    layout[rBody[0]][rBody[1]] = 's';
    layout[rTail[0]][rTail[1]] = 'x';
    layout[bHead[0]][bHead[1]] = 'r';
    layout[bBody[0]][bBody[1]] = 'f';
    layout[bTail[0]][bTail[1]] = 'v';
    //as with SnakeLogic, the locations of the head determine the location of the pretail (body), so they are recorded
    rHeadSpots.add(rHead[0]);
    rHeadSpots.add(rHead[1]);
    bHeadSpots.add(bHead[0]);
    bHeadSpots.add(bHead[1]);
    if (numPlayers >= 3){
      layout[gHead[0]][gHead[1]] = 'y';
      layout[gBody[0]][gBody[1]] = 'h';
      layout[gTail[0]][gTail[1]] = 'n';
      gHeadSpots.add(gHead[0]);
      gHeadSpots.add(gHead[1]);
      if (numPlayers == 4){
        layout[yHead[0]][yHead[1]] = 'i';
        layout[yBody[0]][yBody[1]] = 'k';
        layout[yTail[0]][yTail[1]] = ',';
        yHeadSpots.add(yHead[0]);
        yHeadSpots.add(yHead[1]);
      }
    }
  }
  
  private void setUpSnakes(){
    //all parts of each snake will be on its same row when starting out, as they are facing either east or west
    rHead[0] = rBody[0];
    rHead[1] =  rBody[1] + 1;
    previousRHead[0] = rBody[0];
    previousRHead[1] = rBody[1];
    rTail[0] = rBody[0];
    rTail[1] =  rBody[1] - 1;
    //blue is facing west, so its head will be on the left of the body
    bHead[0] = bBody[0];
    bHead[1] =  bBody[1] - 1;
    previousBHead[0] = bBody[0];
    previousBHead[1] = bBody[1];
    bTail[0] = bBody[0];
    bTail[1] =  bBody[1] + 1;
    if (numPlayers >= 3){
      gHead[0] = gBody[0];
      gHead[1] =  gBody[1] + 1;
      previousGHead[0] = gBody[0];
      previousGHead[1] = gBody[1];
      gTail[0] = gBody[0];
      gTail[1] =  gBody[1] - 1;
      if (numPlayers == 4){
        yHead[0] = yBody[0];
        yHead[1] =  yBody[1] - 1;
        previousYHead[0] = yBody[0];
        previousYHead[1] = yBody[1];
        yTail[0] = yBody[0];
        yTail[1] =  yBody[1] + 1;
      }
    }
  }
  
  private void spawnFood1(){
    Random rand = new Random();
    int x = rand.nextInt(42), y = rand.nextInt(60);
    while (layout[x][y] != '\0'){
      x = rand.nextInt(42); 
      y = rand.nextInt(60);
    }
    food1[0] = x;
    food1[1] = y;
    layout[x][y] = '-'; //a dash looks like a hamburger, doesn't it?
  }
  
  private void spawnFood2(){
    Random rand = new Random();
    int x = rand.nextInt(42), y = rand.nextInt(60);
    while (layout[x][y] != '\0'){
      x = rand.nextInt(42); 
      y = rand.nextInt(60);
    }
    food2[0] = x;
    food2[1] = y;
    layout[x][y] = '='; //an equal sign looks like a hamburger, doesn't it?
  }
  
  private void spawnFood3(){
    Random rand = new Random();
    int x = rand.nextInt(42), y = rand.nextInt(60);
    while (layout[x][y] != '\0'){
      x = rand.nextInt(42); 
      y = rand.nextInt(60);
    }
    food3[0] = x;
    food3[1] = y;
    layout[x][y] = '0'; //a zero looks like a hamburger, doesn't it?
  }
  
  private void spawnFood4(){
    Random rand = new Random();
    int x = rand.nextInt(42), y = rand.nextInt(60);
    while (layout[x][y] != '\0'){
      x = rand.nextInt(42); 
      y = rand.nextInt(60);
    }
    food4[0] = x;
    food4[1] = y;
    layout[x][y] = '8'; //an 8 looks like a hamburger, doesn't it?
  }
  
  private void spawnPowerUp(){
    //invincibility
    
    //stopping
  }
  
  private void rDie(){
    rDead = true;
  }
  
  private void bDie(){
    bDead = true;
  }
  
  private void gDie(){
    gDead = true;
  }
  
  private void yDie(){
    yDead = true;
  }
  
  protected void changeRDirection(char newDir){
    if (newDir == currentRDirection || newDir == oppositeDirection(currentRDirection) || paused || rDead)
      return;
    if (! justRMoved)
      currentRBodyDirection = bodyDirection(currentRDirection, newDir);
    currentRDirection = newDir;
    changedRDir = true;
    moveR();
    justRMoved = true;
    return;
  }
  
  protected void changeBDirection(char newDir){
    if (newDir == currentBDirection || newDir == oppositeDirection(currentBDirection) || paused || bDead)
      return;
    if (! justBMoved)
      currentBBodyDirection = bodyDirection(currentBDirection, newDir);
    currentBDirection = newDir;
    changedBDir = true;
    moveB();
    justBMoved = true;
    return;
  }
  
  protected void changeGDirection(char newDir){
    if (newDir == currentGDirection || newDir == oppositeDirection(currentGDirection) || paused || gDead)
      return;
    currentGDirection = newDir;
    if (! justGMoved)
      currentGBodyDirection = bodyDirection(currentGDirection, newDir);
    moveG();
    justGMoved = true;
    currentGBodyDirection = currentGDirection;
    return;
  }
  
  protected void changeYDirection(char newDir){
    if (newDir == currentYDirection || newDir == oppositeDirection(currentYDirection) || paused || yDead)
      return;
    currentYDirection = newDir;
    if (! justYMoved)
      currentYBodyDirection = bodyDirection(currentYDirection, newDir);
    moveY();
    justYMoved = true;
    currentYBodyDirection = currentYDirection;
    return;
  }
  
  private char bodyDirection(char previous, char current){
    switch (previous + current){ //using ascii values to determine what two directions have been passed
      case 155:
        if (current == 'D')
          return 'E';
        return 'Z';
      case 152:
        if (current == 'A')
          return 'Q';
        return 'C';
      case 151:
        if (current == 'D')
          return 'C';
        return 'Q';
      case 148:
        if (current == 'A')
          return 'Z';
        return 'E';
      default:
        return '?'; //it's not possible for the head to be facing any other way, unless hacks
    }
  }
  
  private int[] moveHead(int[] previousHead, char direction){
    switch (direction){
      case 'W':
        previousHead[0] -= 1;
        break;
      case 'S':
        previousHead[0] += 1;
        break;
      case 'D':
        previousHead[1] += 1;
        break;
      case 'A':
        previousHead[1] -= 1;
        break;
      default:
        break;
    }
    return previousHead;
  }
  
  private char newTailDir(int[] pretail, int[] tail){
    if (pretail[0] - tail[0] == -1)
      return'W';
    else if (pretail[0] - tail[0] == 1)
      return 'S';
    else if (pretail[1] - tail[1] == -1)
      return 'A';
    else
      return 'D';
  }
  
  protected void pause(){
    paused = ! paused;
  }
  
  public char getCurrentRDirection(){
    return currentRDirection;
  }
  
  public char getCurrentBDirection(){
    return currentBDirection;
  }
  
  public char getCurrentGDirection(){
    return currentGDirection;
  }
  
  public char getCurrentYDirection(){
    return currentYDirection;
  }
  
  public char getCurrentRBodyDirection(){
    return currentRBodyDirection;
  }
  
  public char getCurrentBBodyDirection(){
    return currentBBodyDirection;
  }
  
  public char getCurrentGBodyDirection(){
    return currentGBodyDirection;
  }
  
  public char getCurrentYBodyDirection(){
    return currentYBodyDirection;
  }
  
  public char getCurrentRTailDirection(){
    return currentRTailDirection;
  }
  
  public char getCurrentBTailDirection(){
    return currentBTailDirection;
  }
  
  public char getCurrentGTailDirection(){
    return currentGTailDirection;
  }
  
  public char getCurrentYTailDirection(){
    return currentYTailDirection;
  }
  
  public int[] getPreviousRTail(){
    return previousRTail;
  }
  
  public int[] getPreviousBTail(){
    return previousBTail;
  }
  
  public int[] getPreviousGTail(){
    return previousGTail;
  }
  
  public int[] getPreviousYTail(){
    return previousYTail;
  }
  
  public boolean rAlive(){
    return ! rDead;
  }
  
  public boolean bAlive(){
    return ! bDead;
  }
  
  public boolean gAlive(){
    return ! gDead;
  }
  
  public boolean yAlive(){
    return ! yDead;
  }
  
  public char[][] getLayout(){
    return layout;
  }
  
  public boolean food1eaten(){
    return food1eaten;
  }
  
  public boolean food2eaten(){
    return food2eaten;
  }
  
  public boolean food3eaten(){
    return food3eaten;
  }
  
  public boolean food4eaten(){
    return food4eaten;
  }
  
  @Override
  public String toString(){
    String builtString = "";
    for (int i = 0; i < 42; i++){
      for (int j = 0; j < 60; j++){
        builtString += layout[i][j];
      }
      builtString += '\n';
    }
    return builtString;
  }
}