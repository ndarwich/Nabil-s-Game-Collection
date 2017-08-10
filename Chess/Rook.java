package Chess;

public class Rook extends Piece{
  public Rook(Board board, char team, int x, int y){
    super(board, team, x, y);
  }
  public boolean move(int a, int b){ //done kind of
    if ((a == x && b != y) || (a != x && b == y))
      return canMove(a,b);
    return false;
  }
  @Override
  public String toString(){
    return "R";
  }
}

//FEELS GOOD MAN