package Chess;

public class Queen extends Piece{
  public Queen(Board board, char team, int x, int y){
    super(board, team, x, y);
  }
  public boolean move(int a, int b){ //Queen is rook & bishop 
    if (((a == x && b != y) || (a != x && b == y)) || (Math.abs(a - x) == Math.abs(b - y) && a-x != 0))
      return canMove(a,b);
    return false;
  }
  @Override
  public String toString(){
    return "Q";
  }
}