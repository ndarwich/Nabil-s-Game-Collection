package Chess;

public class Board{
  public Piece[] pieces;
  private char currentTurn;
  public Piece currentPiece;
  public int currentIndex;
  public boolean gameOver;
  public boolean newQueen;
  public Board(){
    this("RKBQZBKR\nPPPPPPPP\n........\n........\n........\n........\nPPPPPPPP\nRKBQZBKR");
  }
  /* Build a board given a string for its setup
   RKBQZBKR
   PPPPPPPP
   ........
   ........
   ........
   ........
   PPPPPPPP
   RKBQZBKR
   */
  public Board(String setup){
    pieces = new Piece[32];
    currentTurn = 'w';
    String[] lines = setup.split("\n");
    char color = 'b';
    short pawnCount = 0;
    short piecesIndex = 0;
    newQueen = false;
    gameOver = false;
    for (int i = 0; i < lines.length; i++){
      for (int j = 0; j < lines[i].length(); j++){
        Piece p = null;
        switch (lines[i].charAt(j)){
          case 'R':
            p = new Rook(this, color, j, i);
            break;
          case 'K':
            p = new Knight(this, color, j, i);
            break;
          case 'B':
            p = new Bishop(this, color, j, i);
            break;
          case 'Q':
            p = new Queen(this, color, j, i);
            break;
          case 'Z':
            p = new King(this, color, j, i);
            break;
          case 'P':
            p = new Pawn(this, color, j, i);
            pawnCount++;
            break;
          default:
            break;
        }
        if (pawnCount == 8) //when all 8 pawns for black have been drawn, its white's turn
          color = 'w';
        if (p != null)
          pieces[piecesIndex++] = p;
      }
    }
  }
  public void selectPiece(int x, int y){
    int i = 0;
    for (Piece piece: pieces){
      if (piece == null)
        continue;
      if (piece.x == x && piece.y == y && piece.team == currentTurn){
        currentPiece = piece;
        currentIndex = i;
        return;
      }
      i++;
    }
  }
  public boolean movePiece(int x, int y){
    if (currentPiece == null)
      return false;
    if (! currentPiece.move(x,y))
    {
      currentPiece = null;
      return false;
    }
    int counter = 0;
    for (Piece piece: pieces){
      counter++;
      if (piece == null)
        continue;
      if (piece.x == x && piece.y == y){
        if (piece.team == currentTurn){
          currentPiece = null;
          return false;
        }
        //when the king dies, the game is over
        if (pieces[counter-1] instanceof King){
          gameOver(pieces[counter-1].team);
          return false;
        }
        pieces[counter-1] = null;
      }
    }
    if (currentPiece instanceof Pawn){
      if (currentPiece.team == 'w' && y == 0){
        pieces[currentIndex] = new Queen(this, 'w', x, y);
        currentPiece = pieces[currentIndex];
        newQueen = true;
      }
      else if (y == 7){
        pieces[currentIndex] = new Queen(this, 'b', x, y);
        currentPiece = pieces[currentIndex];
        newQueen = true;
      }
    }
    currentPiece.x = x;
    currentPiece.y = y;
    nextTurn();
    currentPiece = null;
    return true;
  }
  public void nextTurn(){
    currentTurn = currentTurn == 'b' ? 'w' : 'b';
  }
  public void gameOver(char loser){
    gameOver = true;
    if (loser == 'w')
      System.out.println("Game Over, Black Wins!");
    else
      System.out.println("Game Over, White Wins!");
  }
}