package TetrisWorld;

import java.util.Random;

public class Piece{
  private int row, col, components;
  private Random seed;
  private int id; //each piece has an id to determine its color
  public char[][] layout;
  //powerhouse constructor for next piece generation, the pieces are not hardcoded but are made through math :)
  protected Piece(int row, int col, int components, int id){
    this.row = row;
    this.col = col;
    this.components = components;
    this.id = id;
    seed = new Random();
    layout = makeEmptyLayout(); //makes a layout of just spaces
    int[] currentComponent = generateRandomCoordinates();
    int[] nextComponent;
    for (int i = 0; i < components; i++){
      layout[currentComponent[0]][currentComponent[1]] = '.'; //the components of a piece will be dots in the 2d array
      nextComponent = generateNextRandomCoordinates(currentComponent);
      currentComponent = nextComponent.clone();
    }
    //the piece is centered twice
    centerPiece();
    centerPiece(); //the second time is just to ensure consistency among rotations
  }
  //constructor when already given a layout (useful when pieces split during the shift
  protected Piece(int row, int col, char[][] layout, int id){
    components = layout.length;
    this.row = row;
    this.col = col;
    this.layout = layout;
    this.id = id;
    centerPiece();
  }
  //constructor when provided a string for a layout (number of comp must be given too to determine splitting the str)
  protected Piece(int row, int col, String layout, int components, int id){
    this.row = row;
    this.col = col;
    this.components = components;
    this.id = id;
    //transforms the string into a 2d character array
    char[][] charLayout = makeEmptyLayout();
    for (int i = 0; i < layout.length()/components; i++){
      for (int j = 0; j < components; j++){
        charLayout[i][j] = layout.charAt(i*components + j);
      }
    }
    Piece newPiece = new Piece(row, col, charLayout, id);
    //and retrieves the new layout
    this.layout = newPiece.layout;
  }
  //moving a piece in a direction will change its row/column values
  public void move(char direction){
    switch (direction){
      case 'd':
        col++;
        return;
      case 'a':
        col--;
        return;
      case 's':
        row++;
        return;
      case 'w':
        row--;
        return;
      default:
        return;
    }
  }
  //clockwise rotation of a piece
  public void rotateCW(){
    char[][] newLayout = makeEmptyLayout();
    for (int i = 0; i < components; i++){
      for (int j = 0; j < components; j++){
        //cartesian coordinates come in useful, a clockwise rotation is (x, y) -> (y, -x) through heuristics
        newLayout[j][(components-1)-i] = layout[i][j];
      }
    }
    layout = newLayout;
    //the piece is centered again, ensuring that the rotations stay the same
    centerPiece();
    centerPiece();
  }
  public void rotateCCW(){
    char[][] newLayout = makeEmptyLayout();
    for (int i = 0; i < components; i++){
      for (int j = 0; j < components; j++){
        newLayout[(components-1)-j][i] = layout[i][j];
      }
    }
    layout = newLayout;
    centerPiece();
    centerPiece();
  }
  //getters
  public int getRow(){
    return row;
  }
  public int getCol(){
    return col;
  }
  //makes a deep copy of the piece
  public Piece duplicate(){
    char[][] layoutCopy = makeEmptyLayout();
    String[] cL = componentLocations();
    for (int i = 0; i < cL.length; i++){
      layoutCopy[Integer.parseInt("" + cL[i].charAt(0))][Integer.parseInt("" + cL[i].charAt(1))] = '.';
    }
    return new Piece(row, col, layoutCopy, id);
  }
  //used for debugging and testing out rotations, a simple toString from the piece's layout
  @Override
  public String toString(){
    String representation = "\n";
    for (int i = 0; i < components; i++){
      for (int j = 0; j < components; j++){
       representation += layout[i][j]; 
      }
      representation += "\n";
    }
    return representation;
  }
  ///////////////////////////////////////////////HELPER METHODS//////////////////////////////////////////////
  //generates two random coordinates for the component of the piece
  private int[] generateRandomCoordinates(){
    int row1 = seed.nextInt(components), col1 = seed.nextInt(components);
    int[] coords = {row1, col1};
    return coords;
  }
  //the next random coordinate determines the next component, which should be sticking to the current one
  private int[] generateNextRandomCoordinates(int[] current){
    int row1 = seed.nextInt(4), col1 = seed.nextInt(4);
    //either the row is the same or the column is the same to be the next component, and it should not be filled in
    while (Math.abs(row1-current[0])+Math.abs(col1-current[1]) >= 2 || layout[row1][col1] == '.'){
      row1 = seed.nextInt(components);
      col1 = seed.nextInt(components);
    }
    int[] coords = {row1, col1};
    return coords;
  }
  private void centerPiece(){
    int topPieces=countRowPieces(0), bottomPieces=countRowPieces(components-1);
    int leftPieces=countColPieces(0), rightPieces=countColPieces(components-1);
    if (topPieces >= 2){
      pushVertical(1);
    }
    else if (bottomPieces >= 2){
      pushVertical(-1);
    }
    if (leftPieces >= 2){
      pushHorizontal(1);
    }
    else if (rightPieces >= 2){
      pushHorizontal(-1);
    }
  }
  //counts components in a specific row based on the current layout
  private int countRowPieces(int row){
    int totalComponents = 0;
    for (int i = 0; i < components; i++){
      if (layout[row][i] == '.')
        totalComponents++;
    }
    return totalComponents;
  }
  //counts components in a specific column based on the current layout
  private int countColPieces(int col){
    int totalComponents = 0;
    for (int i = 0; i < components; i++){
      if (layout[i][col] == '.')
        totalComponents++;
    }
    return totalComponents;
  }
  //pushes the whole piece vertically a shift number of times (useful when centering and exception handling)
  protected void pushVertical(int shift){
    char[][] newLayout = makeEmptyLayout();
    for (int i = 0; i < components; i++){
      for (int j = 0; j < components; j++){
        if (layout[i][j] == '.')
          newLayout[i+shift][j] = '.';
      }
    }
    layout = newLayout;
  }
  //pushes the whole piece horizontally a shift number of times (useful when centering)
  protected void pushHorizontal(int shift){
    char[][] newLayout = makeEmptyLayout();
    for (int i = 0; i < components; i++){
      for (int j = 0; j < components; j++){
        if (layout[i][j] == '.')
          newLayout[i][j+shift] = '.';
      }
    }
    layout = newLayout;
  }
  //makes a components x components 2D character array of spaces
  private char[][] makeEmptyLayout(){
    char[][] emptyLayout = new char[components][components];
    for (int i = 0; i < components; i++){
      for (int j = 0; j < components; j++){
        emptyLayout[i][j] = ' ';
      }
    }
    return emptyLayout;
  }
  //when a piece spawns in the board, its row and column would make it appear in the center
  protected void reset(){
    row = 13;
    col = 13;
  }
  protected int getID(){
    return id;
  }
  protected void setID(int i){
    id = i;
  }
  //retrieves the locations of all <= 4 components in a string
  protected String[] componentLocations(){
    String[] locations = new String[components];
    int currentComponent = 0;
    for (int i = 0; i < components; i++){
       for (int j = 0; j < components; j++){
         if (layout[i][j] == '.')
           locations[currentComponent++] = "" + i + j; //retrieves the row and column of the component in the layout
       }
    }
    return locations;
  }
}