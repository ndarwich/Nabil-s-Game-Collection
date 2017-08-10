package TetrisWorld;

import java.util.Random;

public class TetrisWorldLogic{
  private char[][] board;
  private char[] blockColors = {'y', 'p', 'g', 'r', 'b', 'o', 'c', 'i'};
  private char currentShift, gameState;
  private Piece[] next3Pieces;
  private Piece currentPiece, shadow, heldPiece;
  private int score = 0, shifted = 0, squaresJustCleared = 0;
  private boolean justShifted = false, pieceJustHeld = false, shadowNotCast = true, 
    justPlacedItem = false, pieceHeld = false, updateSquares = false, nextUpdate = false;
  private Random seed = new Random();
  private final String sq = "     ..  ..     ", t = "     .  ...     ", s = "      .. ..     ", 
    st = "    ..   ..     ", l = "     ... .      ", lt = "    ...   .     ", li = "    ....        ";
  private Piece sqP = new Piece(12, 12, sq, 4, 0), tP = new Piece(12, 12, t, 4, 1), sP = new Piece(12, 12, s, 4, 2), 
    stP = new Piece(12, 12, st, 4, 3), lP = new Piece(12, 12, l, 4, 4), ltP = new Piece(12, 12, lt, 4, 5), 
    liP = new Piece(12, 12, li, 4, 6);
  private Piece[] pieces = {sqP, tP, sP, stP, lP, ltP, liP};
  public TetrisWorldLogic(){
    board = new char[28][28];
    for (int i = 0; i < 28; i++){
      for (int j = 0; j < 28; j++){
        if (i == 0 || i == 27 || j == 0 || j == 27)
          board[i][j] = '|';
        else
          board[i][j] = ' ';
      }
    }
    //randomly decides the current piece and the next 3
    next3Pieces = new Piece[3];
    int previousRNG = seed.nextInt(pieces.length);
    currentPiece = pieces[previousRNG].duplicate();
    placeShadow();
    ///////Random numbers tend to get repetitive, and we want to avoid that, pieces are not the same any time////////
    previousRNG = seed.nextInt(pieces.length);
    while (previousRNG == currentPiece.getID())
      previousRNG = seed.nextInt(pieces.length);
    next3Pieces[0] = pieces[previousRNG].duplicate();
    previousRNG = seed.nextInt(pieces.length);
    while (previousRNG == next3Pieces[0].getID() || previousRNG == currentPiece.getID())
      previousRNG = seed.nextInt(pieces.length);
    next3Pieces[1] = pieces[previousRNG].duplicate();
    previousRNG = seed.nextInt(pieces.length);
    while (previousRNG == next3Pieces[1].getID() || previousRNG == next3Pieces[0].getID() || previousRNG == currentPiece.getID())
      previousRNG = seed.nextInt(pieces.length);
    next3Pieces[2] = pieces[previousRNG].duplicate();
    //the shift will alternate between w and s with each turn, controlling where the piece fall (w = north, s = south)
    currentShift = 's';
    gameState = 's'; //the game started
    repaintBoard(currentPiece);
  }
  
  public int[][] relativeCoordinates(Piece piece){
    int[][] coordinates = new int[4][2];
    String[] componentLocations = piece.componentLocations();
    for (int i = 0; i < componentLocations.length; i++){
      int relativeRow = piece.getRow() + Integer.parseInt("" + componentLocations[i].charAt(0));
      int relativeCol = piece.getCol() + Integer.parseInt("" + componentLocations[i].charAt(1));
      coordinates[i][0] = relativeRow;
      coordinates[i][1] = relativeCol;
    }
    return coordinates;
  }
  
  private boolean canFit(Piece copy){
    String[] componentLocations = copy.componentLocations();
    for (int i = 0; i < componentLocations.length; i++){
      int checkRow = copy.getRow() + Integer.parseInt("" + componentLocations[i].charAt(0));
      int checkCol = copy.getCol() + Integer.parseInt("" + componentLocations[i].charAt(1));
      if (checkRow <= 0 || checkCol <= 0 || checkRow >= 27 || checkCol >= 27)
        return false; //avoids index oob
      if (board[checkRow][checkCol] != ' ' && board[checkRow][checkCol] != 'i'){
        return false;
      }
    }
    return true;
  }
  //useful when holding and checking if a piece can fit, also for shadow
  private void clearPieceFromBoard(Piece piece){
    String[] componentLocations = piece.componentLocations();
    for (int i = 0; i < componentLocations.length; i++){
      int clearRow = piece.getRow() + Integer.parseInt("" + componentLocations[i].charAt(0));
      int clearCol = piece.getCol() + Integer.parseInt("" + componentLocations[i].charAt(1));
      if (board[clearRow][clearCol] == blockColors[piece.getID()]) //avoids conflict with invisible blocks
        board[clearRow][clearCol] = ' ';
    }
  }
  
  private void repaintBoard(Piece piece){
    String[] componentLocations = piece.componentLocations();
    for (int i = 0; i < componentLocations.length; i++){
      int fillRow = piece.getRow() + Integer.parseInt("" + componentLocations[i].charAt(0));
      int fillCol = piece.getCol() + Integer.parseInt("" + componentLocations[i].charAt(1));
      board[fillRow][fillCol] = blockColors[piece.getID()];
    }
  }
  
  public void move(char direction){
    if (justPlacedItem){
      justPlacedItem = false;
      shadowNotCast = true;
    }
    //attempts to move an invisible copy prior to moving the actual current piece
    Piece copy = currentPiece.duplicate();
    copy.move(direction);
    clearPieceFromBoard(currentPiece);
    //if the piece can go in that direction and fits
    if (canFit(copy)){
      currentPiece.move(direction);
      if (direction != currentShift || shadowNotCast){
        clearPieceFromBoard(shadow);
        placeShadow();
        shadowNotCast = false;
      }
    }
    //if the current piece cannot go further in the current shift, it stays still, the game moves on
    else if (direction == currentShift){
      repaintBoard(currentPiece); //places the current piece back in the board before moving on
      updateSquares = false;
      justPlacedItem = true;
      switch (currentShift){
        case 's':
          score += 100; //normal tetris is not much of a challenge
          break;
        default:
          score += 145;
          break;
      }
      inspectCenter(); //the center is inspected to determine whether the game should be over
      //loops through all rows & columns outside the center to see if a square formed
      for (int i = 1; i < 12; i++){
        //the board is square, so the rows and columns are checked at once
        if (inspectColumn(27-i)){ //the right column is checked first, as it is unlikely to be filled
          if (inspectColumn(i)){ //then the left column
            if (inspectRow(i)){ //then the top row
              if (inspectRow(27-i)){ //and then the bottom row (most likely to be already filled)
                pushAll(i);
                updateSquares = true; //sends a boolean to the gui to update the number of squares
                squaresJustCleared++;
                score += 10000 + 2450*(squaresJustCleared-1);
              }
            }
          }
        }
      }
      moveOn();
    }
    
    repaintBoard(currentPiece);
  }
  public boolean inspectRow(int row){
    int column =(row < board.length/2) ? row:board.length-row-1;
    for (int i = column; i < board.length-column; i++){
      if (board[row][i] == ' ' || board[row][i] == 'i')
        return false;
    }
    return true;
  }
  public boolean inspectColumn(int column){
    int row =(column < board.length/2) ? column:board.length-column-1; //if column = 26, row = 2
    for (int i = row; i < board.length-row; i++){
      if (board[i][column] == ' ' || board[i][column] == 'i')
        return false;
    }
    return true;
  }
  private void pushAll(int rowCol){
    pushSouthWest(rowCol);
    pushNorthWest(rowCol);
    pushNorthEast(rowCol);
    pushSouthEast(rowCol);
  }
  //copies pieces from the next row (+1) and  next column (+1) 
  /* e.g:
   * ||||     ||||   
   * |    --> |ab 
   * | ab     |c  
   * | c      |   
   * This ONLY applies for the second quadrant, thus the ranges of i and j
   */
  private void pushNorthWest(int rowCol){
    for (int i = rowCol; i <= 13; i++){
      for (int j = rowCol; j <= 13; j++){
        if (i == 13 || j == 13){
          //gets the last row and column that is pushed to be filled with spaces
          if (board[i][j] != ' ')
            board[i][j] = ' ';
          continue;
        }
        if (board[i][j] != board[i+1][j+1])
          board[i][j] = board[i+1][j+1]; 
      }
    }
  }
  //copies pieces from the previous row (-1) and  next column (+1) 
  /* e.g:
   * | a        |   
   * | bc   --> |a  
   * |          |bc 
   * ||||       ||||
   * This ONLY applies for the third quadrant, thus the ranges of i and j
   */
  private void pushSouthWest(int rowCol){
    for (int i = 27-rowCol; i >= 13; i--){
      for (int j = rowCol; j <= 13; j++){
        if (i == 13 || j == 13){
          //gets the last row and column that is pushed to be filled with spaces
          if (board[i][j] != ' ')
            board[i][j] = ' ';
          continue;
        }
        //debug- System.out.println(board[i][j] + " replaced by " + board[i-1][j+1] + " at " + i + ", " + j);
        if (board[i][j] != board[i-1][j+1])
          board[i][j] = board[i-1][j+1]; 
      }
    }
  }
  //copies pieces from the previous row (-1) and  previous column (-1) 
  /* e.g:
   *  a |          |
   * cb |   -->   a|
   *    |        cb| 
   * ||||       ||||
   * This ONLY applies for the fourth quadrant, thus the ranges of i and j
   */
  private void pushSouthEast(int rowCol){
    for (int i = 27-rowCol; i >= 13; i--){
      for (int j = 27-rowCol; j >= 13; j--){
        if (i == 13 || j == 13){
          //gets the last row and column that is pushed to be filled with spaces
          if (board[i][j] != ' ')
            board[i][j] = ' ';
          continue;
        }
        if (board[i][j] != board[i-1][j-1])
          board[i][j] = board[i-1][j-1]; 
      }
    }
  }
  //copies pieces from the next row (+1) and  previous column (-1) 
  /* e.g:
   * ||||       ||||
   *  ab|   -->    |
   *   c|       ab | 
   *    |        c |
   * This ONLY applies for the first quadrant, thus the ranges of i and j
   */
  private void pushNorthEast(int rowCol){
    for (int i = rowCol; i <= 13; i++){
      for (int j = 27-rowCol; j >= 13; j--){
        if (i == 13 || j == 13){
          //gets the last row and column that is pushed to be filled with spaces
          if (board[i][j] != ' ')
            board[i][j] = ' ';
          continue;
        }
        if (board[i][j] != board[i+1][j-1])
          board[i][j] = board[i+1][j-1]; 
      }
    }
  }
  // Pushes the board down when a row is cleared ------------ Not used in TetrisWorld as lines are cleared differently
  /* public void pushDown(int row){
   for (int i = 1; i < board[row].length -1; i++){
   if (board[row-1][i] != board[row][i])
   board[row][i] = board[row-1][i] ;
   }
   }*/
  public void hardDrop(){
    for (int i = 0; i < 26; i++){
      move(currentShift);
      if (justPlacedItem) //stops moving when it is placed
        break;
    }
    return;
  }
  private Piece hardDrop(Piece piece){
    if (justPlacedItem)
      return piece;
    Piece shadow = piece.duplicate();
    for (int i = 0; i < 26; i++){
      piece = shadow.duplicate();
      shadow.move(currentShift);
      if (! canFit(shadow))
        break;
    }
    return piece;
  }
  private void placeShadow(){
    if (justPlacedItem)
      return;
    clearPieceFromBoard(currentPiece);
    shadow = hardDrop(currentPiece);
    shadow.setID(7);
    repaintBoard(shadow);
    repaintBoard(currentPiece);
  }
  
  public void rotate(){
    if (justPlacedItem)
      justPlacedItem = false;
    //attempts to rotate an invisible copy prior to rotating the actual current piece, very similar to moving
    Piece copy = currentPiece.duplicate();
    clearPieceFromBoard(currentPiece);
    copy.rotateCW();
    //if the invisible copy did fit, the current piece is rotated prior to repainting
    if (canFit(copy)){
      currentPiece.rotateCW();
      clearPieceFromBoard(shadow);
      placeShadow();
    }
    repaintBoard(currentPiece);
  }
  public void hold(){
    if (pieceJustHeld || gameState == 'o')
      return;
    clearPieceFromBoard(currentPiece);
    clearPieceFromBoard(shadow);
    currentPiece.reset();
    if (pieceHeld){
      Piece temp = currentPiece.duplicate(); //temporary piece to hold a deep copy of the previous old piece
      currentPiece = heldPiece;
      heldPiece = temp;
    }
    else{
      heldPiece = currentPiece;
      moveOn();
    }
    if (! pieceHeld)
      pieceHeld = true;
    repaintBoard(currentPiece);
    shadowNotCast = true;
    pieceJustHeld = true;
  }
  private void moveOn(){
    if (gameState == 'o') //no new piece spawns if the game is over
      return;
    currentPiece = next3Pieces[0];
    shadow = currentPiece.duplicate();
    //shifts all the elements in the array back one
    for (int i = 0; i <= next3Pieces.length - 2; i++){
      next3Pieces[i] = next3Pieces[i+1];
    }
    //and puts a randomly generated piece as the new last piece
    int newPieceID = seed.nextInt(pieces.length); 
    //makes sure that no same pieces follow each othersame process of avoiding random repetitions
    while (newPieceID == next3Pieces[1].getID() || newPieceID == next3Pieces[0].getID() || newPieceID == currentPiece.getID())
      newPieceID = seed.nextInt(pieces.length); 
    next3Pieces[next3Pieces.length-1] = pieces[newPieceID].duplicate();
    repaintBoard(currentPiece);
    shifted = 0;
    justShifted = false;
    pieceJustHeld = false;
  }
  //inspects whether there are any blocks in the center, if so, the game is over
  private void inspectCenter(){
    for (int i = 12; i <= 15; i++){
      for (int j = 12; j <= 15; j++){ //boundaries of the center
        if (board[i][j] != ' ')
          gameState = 'o';
      }
    }
  }
  public void switchShift(){
    if (shifted == 3)
      return;
    shifted++;
    score += 20;
    clearPieceFromBoard(shadow);
    shadowNotCast = true;
    if (currentShift == 's')
      currentShift = 'a';
    else if (currentShift == 'a')
      currentShift = 'w';
    else if (currentShift == 'w')
      currentShift = 'd';
    else
      currentShift = 's';
  }
  /////////////////////////////////////GETTERS//////////////////////////////
  public char[][] getBoard(){
    return board;
  }
  public char getState(){
    return gameState;
  }
  public boolean itemJustPlaced(){
    return justPlacedItem;
  }
  public char getShift(){
    return currentShift;
  }
  public boolean getPieceHeld(){
    return pieceHeld;
  }
  public boolean getPieceJustHeld(){
    return pieceJustHeld;
  }
  public boolean getUpdateSquares(){
    return updateSquares;
  }
  public boolean getJustPlacedItem(){
    return justPlacedItem;
  }
  public int getScore(){
    return score;
  }
  public int getSquaresJustCleared(){
    int temp = squaresJustCleared;
    squaresJustCleared = 0;
    return temp;
  }
  public char getHeldPiece(){
    return blockColors[heldPiece.getID()];
  }
  public char getNextPiece(){
    return blockColors[next3Pieces[0].getID()];
  }
  public char getNextNextPiece(){
    return blockColors[next3Pieces[1].getID()];
  }
  public char getNextNextNextPiece(){
    return blockColors[next3Pieces[2].getID()];
  }
  public int getHeldPieceID(){
    return heldPiece.getID();
  }
  public int[] getNextPieces(){
    int[] pieces = {next3Pieces[0].getID(), next3Pieces[1].getID(), next3Pieces[2].getID()};
    return pieces;
  }
  public int getShiftID(){
    switch (currentShift){
      case 's':
        return 0;
      case 'a':
        return 1;
      case 'w':
        return 2;
      case 'd':
        return 3;
      default:
        return 0;
    }
  }
  //The "pretty" string implementation of this board, which turns out to help immensely in debugging!
  @Override
  public String toString(){
    String representation = "\n";
    for (int i = 0; i < board.length; i++){
      for (int j = 0; j < board[0].length; j++){
        representation += board[i][j]; 
      }
      representation += "\n";
    }
    return representation;
  }
}