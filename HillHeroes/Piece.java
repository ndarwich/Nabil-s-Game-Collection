package HillHeroes;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Piece{
  private final Type type;
  public final Team team;
  private int row;
  private int col;
  public Piece(Type type, Team team, int row, int col){
    this.type =  type;
    this.team = team;
    this.row = row;
    this.col = col;
  }
  public char toChar(){
    switch(type){
      case Fire:
        if (team == Team.Blue)
        return 'L';  //team blue will be lava, snow, dirt lol.. i gotta represent the teams differently as characters
        return 'F';
      case Water:
        if (team == Team.Blue)
        return 'S';
        return 'W';
      default:
        if (team == Team.Blue)
        return 'D';
        return 'G';
    }
  }
  public static Piece fromChar(char c, int row, int col){
    switch (c){
      case 'L':
        return new Piece(Type.Fire, Team.Blue, row, col);
      case 'F':
        return new Piece(Type.Fire, Team.Red, row, col);
      case 'S':
        return new Piece(Type.Water, Team.Blue, row, col);
      case 'W':
        return new Piece(Type.Water, Team.Red, row, col);
      case 'D':
        return new Piece(Type.Grass, Team.Blue, row, col);
      case 'G':
        return new Piece(Type.Grass, Team.Red, row, col);
      default:
        throw new InvalidPieceSelected("No piece selected");
    }
  }
  //pieces can only bump other pieces from the opponent team, thus the different character representations
  public boolean canBump(char other){
    char charRepr = this.toChar();
    switch(charRepr){
      case 'L':
        if (other == 'G')
        return true;
        return false;
      case 'S':
        if (other == 'F')
        return true;
        return false;
      case 'D':
        if (other == 'W')
        return true;
        return false;
      case 'F':
        if (other == 'D')
        return true;
        return false;
      case 'W':
        if (other == 'L')
        return true;
        return false;
      case 'G':
        if (other == 'S')
        return true;
        return false;
      default:
        return false;
    }
  }
  @Override public String toString(){
    switch(toChar()){
      case 'L':
        return "Blue Fire";
      case 'S':
        return "Blue Water";
      case 'D':
        return "Blue Grass";
      case 'F':
        return "Red Fire";
      case 'W':
        return "Red Water";
      case 'G':
        return "Red Grass";
      default:
        return null;
    }
  }
  public int getRow(){
    return row;
  }
  public int getCol(){
    return col;
  }
  public void setRow(int row){
    this.row = row;
  }
  public void setCol(int col){
    this.col = col;
  }
  public Team getTeam(){
    return team;
  }
  enum Team{Red, Blue};
}