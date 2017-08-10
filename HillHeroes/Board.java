package HillHeroes;

public class Board{
  private char[][] layout; //the layout of all empty spots/walls in the board (will consist of .'s and |'s)
  protected Piece af1; //Team Red's pieces
  protected Piece af2;
  protected Piece aw1;
  protected Piece aw2;
  protected Piece ag1;
  protected Piece ag2;
  protected Piece bf1; //Team Blue's pieces
  protected Piece bf2;
  protected Piece bw1;
  protected Piece bw2;
  protected Piece bg1;
  protected Piece bg2;
  protected final Piece[] inboundPieces; //array to hold all pieces
  protected final int goalCount; //**THIS WILL BE THE REQUIRED PIECES IN THE MIDDLE FROM A TEAM TO WIN THE GAME**
  protected GameState state = GameState.Ongoing;
  //constructor, initializes all pieces and the array, along with stating the state of the game state state state
  public Board(){
    goalCount = 5; //5 pieces in the hill to win the game
    af1 = new Piece(Type.Fire, Piece.Team.Blue, 0, 3);
    af2 = new Piece(Type.Fire, Piece.Team.Blue, 0, 5);
    aw1 = new Piece(Type.Water, Piece.Team.Blue, 1, 3);
    aw2 = new Piece(Type.Water, Piece.Team.Blue, 1, 5);
    ag1 = new Piece(Type.Grass, Piece.Team.Blue, 0, 4);
    ag2 = new Piece(Type.Grass, Piece.Team.Blue, 1, 4);
    bf1 = new Piece(Type.Fire, Piece.Team.Red, 8, 3);
    bf2 = new Piece(Type.Fire, Piece.Team.Red, 8, 5);
    bw1 = new Piece(Type.Water, Piece.Team.Red, 7, 3);
    bw2 = new Piece(Type.Water, Piece.Team.Red, 7, 5);
    bg1 = new Piece(Type.Grass, Piece.Team.Red, 7, 4);
    bg2 = new Piece(Type.Grass, Piece.Team.Red, 8, 4);
    state = GameState.Ongoing;
    inboundPieces = new Piece[]{af1, af2, aw1, aw2, ag1, ag2, bf1, bf2, bw1, bw2, bg1, bg2};
    updateBoard();
  }
  //updates the board's character representation accordingly to all piece positions
  public void updateBoard(){
    layout = new char[][]{
      {'|','|','|','.','.','.','|','|','|'},
      {'|','|','|','.','.','.','|','|','|'},
      {'|','|','.','.','.','.','.','|','|'},
      {'.','.','.','.','.','.','.','.','.'},
      {'.','.','.','.','.','.','.','.','.'},
      {'.','.','.','.','.','.','.','.','.'},
      {'|','|','.','.','.','.','.','|','|'},
      {'|','|','|','.','.','.','|','|','|'},
      {'|','|','|','.','.','.','|','|','|'}}; //layout is created, |'s are inaccessible tiles
    for (Piece piece : inboundPieces)
      layout[piece.getRow()][piece.getCol()] = piece.toChar(); //places the pieces in their appropriate row/column
  }
  //determines if a piece is able to move to a location
  public boolean canMove(Piece piece, int row, int col){
    if (row > 8 || col > 8 || row < 0 || col < 0) //math math math math
      return false;
    else if ((piece.getRow() - 1 == row || piece.getRow() + 1 == row || piece.getRow() == row) && (piece.getCol() - 1 == col || piece.getCol() + 1 == col || piece.getCol() == col)){
      if (piece.getRow() == row && piece.getCol() == col)
        return false; //moving to the exact same place is an invalid move
      else if (layout[row][col] == '.')
        return true;
      else if (layout[row][col] == '|')
        return false;
      return (piece.canBump(layout[row][col]));
    }
    else
      return false;
  }
  //moves a given piece one step in the selected direction
  public void move(Piece piece, Direction direction){
    int newRow = piece.getRow();
    int newCol = piece.getCol();
    switch(direction){
      case N:
        newRow -= 1;
        break;
      case S:
        newRow += 1;
        break;
      case E:
        newCol += 1;
        break;
      case W:
        newCol -= 1;
        break;
      case NE:
        newRow -= 1;
        newCol += 1;
        break;
      case NW:
        newRow -= 1;
        newCol -= 1;
        break;
      case SE:
        newRow += 1;
        newCol += 1;
        break;
      case SW:
        newRow += 1;
        newCol -= 1;
        break;
      default:
        break;
    }
    if (canMove(piece, newRow, newCol)){
      if (layout[newRow][newCol] != '.')
        this.bump(getPiece(newRow, newCol), direction); //if a piece is on its way, it's bumped in the direction
      piece.setRow(newRow);
      piece.setCol(newCol);
    }
    else
      throw new InvalidMove("Cannot move here");
    updateBoard();
  }
  //moves a piece given new coordinates, helpful in the grid layout
  public void move(Piece piece, int newRow, int newCol){
    move(piece, directionFromCoords(piece.getRow(), piece.getCol(), newRow, newCol));
  }
  //action to bump another piece, in which it checks if there is a piece behind the piece about to be bumped
  public void bump(Piece piece, Direction direction){
    int newRow = piece.getRow();
    int newCol = piece.getCol();
    switch(direction){
      case N:
        newRow -= 1;
        break;
      case S:
        newRow += 1;
        break;
      case E:
        newCol += 1;
        break;
      case W:
        newCol -= 1;
        break;
      case NE:
        newRow -= 1;
        newCol += 1;
        break;
      case NW:
        newRow -= 1;
        newCol -= 1;
        break;
      case SE:
        newRow += 1;
        newCol += 1;
        break;
      case SW:
        newRow += 1;
        newCol -= 1;
        break;
      default:
        break;
    }
    if (canMove(piece, newRow, newCol)){
      if (layout[newRow][newCol] != '.')
        throw new InvalidMove("Cannot bump the piece as there is another piece on its way");
      move(piece, newRow, newCol);
    }
    else
      throw new InvalidMove("Cannot bump the piece to an invalid space");
  }
  //determines the direction moved given old coords and new ones, again helpful in the grid layout
  public Direction directionFromCoords(int lastRow, int lastCol, int newRow, int newCol){
    int rowDiff = newRow - lastRow;
    int colDiff = newCol - lastCol;
    if (colDiff == 1){
      if (rowDiff == 1)
        return Direction.SE;
      else if (rowDiff == -1)
        return Direction.NE;
      return Direction.E;
    }
    else if (colDiff == -1){
      if (rowDiff == 1)
        return Direction.SW;
      else if (rowDiff == -1)
        return Direction.NW;
      return Direction.W;
    }
    else if (colDiff == 0){
      if (rowDiff == -1)
        return Direction.N;
      else if (rowDiff == 1)
        return Direction.S;
    }
    throw new InvalidMove("Must move exactly one tile in any direction");
  }
  //continuously checks if someone placed the goal amount of pieces in the hill
  public void checkWinner(){
    int redCount = 0;
    int blueCount = 0;
    for (Piece piece: inboundPieces){
      if (piece.getRow() < 6 && piece.getRow() > 2 && piece.getCol() < 6 && piece.getCol() > 2){
        if (piece.getTeam() == Piece.Team.Red)
          redCount++;
        if (piece.getTeam() == Piece.Team.Blue)
          blueCount++;
      }
    }
    if (blueCount == goalCount){
      state = GameState.Over;
      throw new GameOver("Blue wins! Click anywhere to start a new game.");
    }
    else if (redCount == goalCount){
      state = GameState.Over;
      throw new GameOver("Red wins! Click anywhere to start a new game.");
    }
  }
  //self explanatory
  public char[][] getLayout(){
    return layout;
  }
  //gets the piece at a given location based on the current layout of the board
  public Piece getPiece(int a, int b){
    for (Piece piece: inboundPieces){
      if (piece.getRow() == a && piece.getCol() == b)
        return piece;
    }
    throw new InvalidPieceSelected("There is no piece here!");
  }
  //returns the string representation of the board
  @Override public String toString(){
    String returnedString = "";
    for (char[] a : layout){
      for (char b : a){
        returnedString += b;
      }
      returnedString += '\n';
    }
    return returnedString;
  }
  protected enum GameState{Ongoing, Over};
  private enum Direction{N, S, E, W, NE, NW, SE, SW};
}