package Chess;

public class Knight extends Piece{
  public Knight(Board board, char team, int x, int y){
    super(board, team, x, y);
  }
  public boolean move(int a, int b){
    if ((Math.abs(a-x) == 2 && Math.abs(b-y) == 1) || (Math.abs(b-y) == 2 && Math.abs(a-x) == 1))
      return canMove(a,b);
    return false;
  }
  @Override
  public String toString(){
    return "K";
  }
}