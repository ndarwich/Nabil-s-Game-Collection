package BattleSnake;

import java.util.LinkedList;
import java.util.Random;

public class BattleSnakeLogic{
  private int numPlayers, impendingRParts = 0, impendingBParts = 0, impendingGParts = 0, impendingYParts = 0;
  //chose to have chars for directions rather than an enum; directions are kept for each body part
  private char currentRDirection, currentBDirection, currentGDirection, currentYDirection, currentRTailDirection,
    currentBTailDirection, currentGTailDirection, currentYTailDirection,currentRBodyDirection, currentBBodyDirection, 
    currentGBodyDirection, currentYBodyDirection, currentPowerUp;
  protected boolean paused; //instead of an enum, choosing a boolean for the gamestate, as it's simpler
  //body is simply the start of the body after the tail, but not actual body parts
  protected int[] rHead = new int[2], rBody = new int[2], rTail = new int[2], bHead = new int[2], bBody = new int[2], 
    bTail = new int[2], gHead = new int[2], gBody = new int[2], gTail = new int[2], yHead = new int[2], 
    yBody = new int[2], yTail = new int[2], previousRHead = new int[2], previousBHead = new int[2], 
    previousGHead = new int[2], previousYHead = new int[2], previousRTail = new int[2], previousBTail = new int[2],
    previousGTail = new int[2], previousYTail = new int[2], food1 = new int[2], food2 = new int[2], food3 = new int[2],
    food4 = new int[2], powerUp = new int[2];
  //yellow and green start out as dead unless there are actually 3/4 players
  protected boolean justRMoved = false, justBMoved = false, justGMoved = false, justYMoved = false, rDead = false, 
    bDead = false, gDead = true, yDead = true, rGrown = false, bGrown = false, gGrown = false, yGrown = false,
    changedRDir = false, changedBDir = false, changedGDir = false, changedYDir = false;
  //for foods, they all need to be painted in the frame initially, BattleSnake is where these booleans get modified
  protected boolean food1eaten = true, food2eaten = true, food3eaten = true, food4eaten = true, fasterTime = false,
    slowerTime = false;
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
    spawnRandomPowerUp();
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
    if (rDead || justRMoved){ //prevents duplicate moves from changing direction by falsifying the once true boolean
      justRMoved = false;
      return;
    }
    if (! changedRDir)
      currentRBodyDirection = currentRDirection;
    changedRDir = false;
    previousRHead[0] = rHead[0];
    previousRHead[1] = rHead[1];
    int[] newHead = moveHead(rHead, currentRDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      rDie();
      return;
    }
    int currentRParts = impendingRParts;
    impendingRParts += eatFoodOrDie(newHead);
    if (impendingRParts - currentRParts == -1){ //if poison is picked up/the snake collides
      rDie();
      return;
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
      rTail[0] = rBody[0]; //the tail moves up to the pretail
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
    changedBDir = false;
    previousBHead[0] = bHead[0];
    previousBHead[1] = bHead[1];
    int[] newHead = moveHead(bHead, currentBDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      bDie();
      return;
    }
   int currentBParts = impendingBParts;
   impendingBParts += eatFoodOrDie(newHead);
   if (impendingBParts - currentBParts == -1){
      bDie();
      return;
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
    if (! changedGDir)
      currentGBodyDirection = currentGDirection;
    changedGDir = false;
    previousGHead[0] = gHead[0];
    previousGHead[1] = gHead[1];
    int[] newHead = moveHead(gHead, currentGDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      gDie();
      return;
    }
    int currentGParts = impendingGParts;
    impendingGParts += eatFoodOrDie(newHead);
    if (impendingGParts - currentGParts == -1){
      gDie();
      return;
    }
    gHead[0] = newHead[0];
    gHead[1] = newHead[1];
    layout[gHead[0]][gHead[1]] = 'y';
    gHeadSpots.add(gHead[0]);
    gHeadSpots.add(gHead[1]);
    if (impendingGParts > 0){
      impendingGParts--;
      if (! gGrown)
        gGrown = true;
    }
    else{
      if (! gHead.equals(gTail)){
        layout[gTail[0]][gTail[1]] = '\0';
        previousGTail[0] = gTail[0];
        previousGTail[1] = gTail[1];
     }
      gTail[0] = gBody[0]; //the tail moves up to the pretail
      gTail[1] = gBody[1];
      gBody[0] = gHeadSpots.removeFirst();
      gBody[1] = gHeadSpots.removeFirst();
      currentGTailDirection = newTailDir(gBody, gTail);
      layout[gTail[0]][gTail[1]] = 'n';
    }
    if (gGrown)
      layout[previousGHead[0]][previousGHead[1]] = 'g';
    else
      layout[gBody[0]][gBody[1]] = 'h';
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
    int[] newHead = moveHead(yHead, currentYDirection);
    if (newHead[0] < 0 || newHead[0] >= 42 || newHead[1] < 0 || newHead[1] >= 60){
      yDie();
      return;
    }
    int currentYParts = impendingYParts;
    impendingYParts += eatFoodOrDie(newHead);
   if (impendingYParts - currentYParts == -1){
      yDie();
      return;
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
    int[] pair = makeAValidRandomPair();
    int x = pair[0];
    int y = pair[1];
    food1[0] = x;
    food1[1] = y;
    layout[x][y] = '-'; //a dash looks like a hamburger, doesn't it?
  }
  
  private void spawnFood2(){
    int[] pair = makeAValidRandomPair();
    int x = pair[0];
    int y = pair[1];
    food2[0] = x;
    food2[1] = y;
    layout[x][y] = '='; //an equal sign looks like a hamburger, doesn't it?
  }
  
  private void spawnFood3(){
    int[] pair = makeAValidRandomPair();
    int x = pair[0];
    int y = pair[1];
    food3[0] = x;
    food3[1] = y;
    layout[x][y] = '0'; //a zero looks like a hamburger, doesn't it?
  }
  
  private void spawnFood4(){
    int[] pair = makeAValidRandomPair();
    int x = pair[0];
    int y = pair[1];
    food4[0] = x;
    food4[1] = y;
    layout[x][y] = '8'; //an 8 looks like a hamburger, doesn't it?
  }
  
  protected void spawnRandomPowerUp(){
    int randomInt = new Random().nextInt(7);
    switch (randomInt){
      case 0:
        currentPowerUp = 'q';
        break;
      case 1:
        currentPowerUp = 'e';
        break;
      case 2:
        currentPowerUp = 't';
        break;
      case 3:
        currentPowerUp = 'u';
        break;
      case 4:
        currentPowerUp = 'o';
        break;
      case 5:
        currentPowerUp = '[';
        break;
      default:
        currentPowerUp = ']';
        break;
    }
    int[] pair = makeAValidRandomPair();
    layout[pair[0]][pair[1]] = currentPowerUp;
    powerUp[0] = pair[0];
    powerUp[1] = pair[1];
  }
  
  private int[] makeAValidRandomPair(){
    Random rand = new Random();
    int x = rand.nextInt(42), y = rand.nextInt(60);
    while (layout[x][y] != '\0'){ //food may only spawn in an empty spot
      x = rand.nextInt(42); 
      y = rand.nextInt(60);
    }
    int[] validRandomPair = new int[2];
    validRandomPair[0] = x;
    validRandomPair[1] = y;
    return validRandomPair;
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
    if (newDir == currentRDirection || newDir == oppositeDirection(currentRDirection) || paused || rDead || justRMoved)
      return;
    currentRBodyDirection = bodyDirection(currentRDirection, newDir);
    currentRDirection = newDir;
    changedRDir = true;
    moveR();
    justRMoved = true;
    return;
  }
  
  protected void changeBDirection(char newDir){
    if (newDir == currentBDirection || newDir == oppositeDirection(currentBDirection) || paused || bDead || justBMoved)
      return;
    currentBBodyDirection = bodyDirection(currentBDirection, newDir);
    currentBDirection = newDir;
    changedBDir = true;
    moveB();
    justBMoved = true;
    return;
  }
  
  protected void changeGDirection(char newDir){
    if (newDir == currentGDirection || newDir == oppositeDirection(currentGDirection) || paused || gDead || justGMoved)
      return;
    currentGBodyDirection = bodyDirection(currentGDirection, newDir);
    currentGDirection = newDir;
    changedGDir = true;
    moveG();
    justGMoved = true;
    return;
  }
  
  protected void changeYDirection(char newDir){
    if (newDir == currentYDirection || newDir == oppositeDirection(currentYDirection) || paused || yDead || justYMoved)
      return;
    currentYBodyDirection = bodyDirection(currentYDirection, newDir);
    currentYDirection = newDir;
    changedYDir = true;
    moveY();
    justYMoved = true;
    return;
  }
  
  private char bodyDirection(char previous, char current){
    if (previous == current)
      return current;
    switch (current){ //using ascii values to determine what two directions have been passed
      case 'W':
        if (previous == 'A' || previous == 'Q' || previous == 'Z')
          return 'C';
        return 'Z';
      case 'A':
        if (previous == 'W' || previous == 'C' || previous == 'Z')
          return 'Q';
        return 'Z';
      case 'S':
        if (previous == 'A' || previous == 'Q' || previous == 'Z')
          return 'E';
        return 'Q';
      case 'D':
        if (previous == 'W' || previous == 'C' || previous == 'Z')
          return 'E';
        return 'C';
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
  
  private int eatFoodOrDie(int[] futureCoords){
    if (layout[futureCoords[0]][futureCoords[1]] != '\0'){
      if (layout[futureCoords[0]][futureCoords[1]] == '-'){
        spawnFood1();
        food1eaten = true;
        return 2;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == '='){
        spawnFood2();
        food2eaten = true;
        return 2;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == '0'){
        spawnFood3();
        food3eaten = true;
        return 2;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == '8'){
        spawnFood4();
        food4eaten = true;
        return 2;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == 'q'){ //powerups (see spawnRandomPowerUp() for details)
        fasterTime = true;
        return 0;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == 'e'){
        slowerTime = true;
        return 0;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == 't'){
        return 0;
      }  
      else if (layout[futureCoords[0]][futureCoords[1]] == 'u'){
        return 0;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == 'o'){
        return -1;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == '['){
        return 10;
      }
      else if (layout[futureCoords[0]][futureCoords[1]] == ']'){
        return 6;
      }
      //head collisions cause both snakes to die
      else if (layout[futureCoords[0]][futureCoords[1]] == 'w')
        rDie();
      else if (layout[futureCoords[0]][futureCoords[1]] == 'r')
        bDie();
      else if (layout[futureCoords[0]][futureCoords[1]] == 'y')
        gDie();
      else if (layout[futureCoords[0]][futureCoords[1]] == 'i')
        yDie();
      return -1; //a negative one indicates the death of the currentsnake
    }
    return 0;
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
  
  protected boolean isOver(){
    //if 3 of the four snakes are dead (or all 4 for that matter) the game is over
    if ((rDead && bDead && gDead) || (rDead && bDead && yDead) || (rDead && gDead && yDead) || (bDead && gDead && yDead)){
      return true;
    }
    return false;
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
  
  public String getCurrentPowerUp(){
    switch (currentPowerUp){
      case 'q':
        return "clock1";
      case 'e':
        return "clock2";
      case 't':
        return "energy1";
      case 'u':
        return "energy2";
      case 'o':
        return "poison";
      case '[':
        return "life";
      default:
        return "giantburger";
    }
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