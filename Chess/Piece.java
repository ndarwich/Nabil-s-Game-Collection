package Chess;
import java.lang.Math;

public abstract class Piece{ /////soooooooooooooooooo much shit
  Board board;
  char team;
  int x, y;
  public Piece(Board board, char team, int x, int y){
    this.board = board;
    this.team = team;
    this.x = x;
    this.y = y;
  }
  public abstract boolean move(int a, int b); //different pieces move differently
  public boolean canMove(int a, int b){ //general move for avoiding obstacles
    if (a < 0 || b < 0 || a > 7 || b > 7)
      return false;
    //checking for if there are any obstacles
    for (Piece p: board.pieces){
      if (p == null)
        continue;
      if (p.x == a && p.y == b && p.team == team){
        return false;
      }
      if (this instanceof Bishop || this instanceof Queen){
        if (Math.abs(a - x) == Math.abs(b - y) && a-x != 0){
          if (Math.abs(p.x - x) == Math.abs(p.y - y) && p.x-x != 0){
            //checking between the old delta x/delta y and new ones all 4 diagonal directions
            if (a - x < 0 && b - y < 0 && p.x - x < 0 && p.y - y < 0 && p.x-a > 0)
              return false;
            if (a - x > 0 && b - y < 0 && p.x - x > 0 && p.y - y < 0 && a-p.x > 0)
              return false;
            if (a - x < 0 && b - y > 0 && p.x - x < 0 && p.y - y > 0 && p.x-a > 0)
              return false;
            if (a - x > 0 && b - y > 0 && p.x - x > 0 && p.y - y > 0 && a-p.x > 0)
              return false;
          }
          continue; //mainly for queen, avoids checking rook sides as conditions for diagonal have been satisfied
        }
      }
      if (this instanceof Rook || this instanceof Queen || this instanceof Pawn){
        if (((x < p.x && p.x < a) || (x > p.x && p.x > a)) && p.y == y){ //checking between the old x and new x (a) HORIZONAL
          return false;
        }
        if (((y < p.y && p.y < b) || (y > p.y && p.y > b)) && p.x == x){ //checking between the old y and new y (b) VERTICAL
          return false;
        }
      }
    }
    return true;
  }
}