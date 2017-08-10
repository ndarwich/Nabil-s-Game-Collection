package Chess;

public class Pawn extends Piece{ //HARDER THAN IT LOOKS BECAUSE OF SPECIAL FUCKING MOVE
  public Pawn(Board board, char team, int x, int y){
    super(board, team, x, y);
  }
  public boolean move(int a, int b){ //done except for eating the other fucking pieces
    if (team == 'w'){ //white pawn can go in north diagonals
      if (a == x && b == y-1){
        for (Piece p: board.pieces){
          if (p == null)
            continue;
          if (p.x == a && p.y == b && p.team != team)
            return false;
        }
        return canMove(a,b);
      }
      else if (y == 6 && a == x && b == y-2)
        return canMove(a,b);
      else if (Math.abs(a-x) == 1 && (b-y == -1)){//eating, still need to check if piece on other side is black WILL DO IN BOARD CLASS
        for (Piece p: board.pieces){
          if (p == null)
            continue;
          if (p.x == a && p.y == b && p.team != team)
            return canMove(a,b);
        }
      }
    }
    else{ //black pawn
      if (a == x && b == y+1){
        for (Piece p: board.pieces){
          if (p == null)
            continue;
          if (p.x == a && p.y == b && p.team != team)
            return false;
        }
        return canMove(a,b);
      }
       else if (y == 1 && a == x && b == y+2)
        return canMove(a,b);
      else if (Math.abs(a-x) == 1 && (b-y == 1)){ //eating
        for (Piece p: board.pieces){
          if (p == null)
            continue;
          if (p.x == a && p.y == b && p.team != team)
            return canMove(a,b);
        }
      }
    }
    return false;
  }
  @Override
  public String toString(){
    return "P";
  }
}