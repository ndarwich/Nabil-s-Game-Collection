package HillHeroes;

public enum Type{Water, Fire, Grass;
  public boolean canBump(Type x){
    switch(this){
      case Water:
        if (x == Fire)
          return true;
        return false;
      case Fire:
        if (x == Grass)
          return true;
        return false;
      case Grass:
        if (x == Water)
          return true;
      default:
        return false;
    }
  }
}