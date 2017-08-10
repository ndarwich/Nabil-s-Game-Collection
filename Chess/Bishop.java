package Chess;

public class Bishop extends Piece{
  public Bishop(Board board, char team, int x, int y){
    super(board, team, x, y);
  }
  public boolean move(int a, int b){
    if (Math.abs(a - x) == Math.abs(b - y) && a-x != 0)
      return canMove(a,b);
    return false;
  }
  @Override
  public String toString(){
    return "B";
  }
}