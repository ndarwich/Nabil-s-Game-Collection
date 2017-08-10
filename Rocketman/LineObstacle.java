package Rocketman;

import java.lang.Math;

class LineObstacle extends Obstacle{
  private final int startX;
  private final int endX;
  private final int startY;
  private final int endY;
  private double speed;
  private boolean movingLeft = false, movingRight = false, movingUp = false, movingDown = false;
  //constructor, accepts start positions and end positions and the time it takes to get from start to end & backwards
  protected LineObstacle(int x1, int y1, int x2, int y2, double time){
    super();
    startX = x1;
    endX = x2;
    startY = y1;
    endY = y2;
    x = startX;
    y = startY;
    setBounds((int)x, (int)y, 14, 14);
    if (x < endX){
      movingRight = true;
    }
    else if (x > endX){
      movingLeft = true;
    }
    if (y < endY){
      movingDown = true;
    }
    else if (y > endY){
      movingUp = true;
    }
    double distance = Math.sqrt((endX-startX)*(endX-startX)+(endY-startY)*(endY-startY));
    speed = distance/(time*62); //time is multiplied to adjust for in-game timer refreshing
  }
  @Override
  protected void move(){
    if (movingRight) {
      x += speed;
      if ((x >= endX && startX < endX) || (x >= startX && startX > endX)){
        movingLeft = true;
        movingRight = false;
      }
    }
    else if (movingLeft){
      x -= speed;
      if ((x <= startX && startX < endX) || (x <= endX && startX > endX)){
        movingRight = true;
        movingLeft = false;
      }
      
    }
    if (movingDown){
      y += speed;
      if ((y >= endY && startY < endY) || (y >= startY && startY > endY)){
        movingUp = true;
        movingDown = false;
      }
    }
    else if (movingUp){
       y -= speed;
      if ((y <= startY && startY < endY) || (y <= endY && startY > endY)){
        movingDown = true;
        movingUp = false;
      }
    }
    setBounds((int)x, (int)y, 14, 14);
    revalidate();
    repaint();
  }
}