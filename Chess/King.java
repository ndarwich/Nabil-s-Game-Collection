package Chess;

public class King extends Piece{
  public King(Board board, char team, int x, int y){
    super(board, team, x, y);
  }
  public boolean move(int a, int b){ //looks right
    if ((a-x != 0 || b - x != 0) && (Math.abs(a-x) <= 1 && Math.abs(b-y) <= 1))
      return canMove(a,b);
    return false;
  }
  @Override
  public String toString(){
    return "Z";
  }
}