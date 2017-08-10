package Rocketman;

class RectangleObstacle extends Obstacle{
  private int x1;
  private int x3;
  private int y1;
  private int y3;
  private final double speed;
  private final boolean movingClockwise;
  //constructor, a starting position, determinant corners are top-left & bottom right (1 & 3), direction, and speed
  protected RectangleObstacle(int startingX, int startingY, int x1, int x3, int y1, int y3, boolean clockWise, double time){
    super();
    movingClockwise = clockWise;
    this.x1 = x1;
    this.x3 = x3;
    this.y1 = y1;
    this.y3 = y3;
    x = startingX;
    y = startingY;
    double perimeter = Math.abs(x3-x1)*2+Math.abs(y3-y1)*2;
    speed = perimeter/(time*62);
  }
  @Override
  protected void move(){
    if (movingClockwise){
      if (y <= y1 && x < x3){ //moving right (top side), considers overreaching the corner from the previous move
        x += speed;
      }
      else if(y >= y3 && x > x1){ //moving left (bottom side)
        x -= speed;
      }
      else if(x >= x3 && y < y3){ //moving down (right side)
        y += speed;
      }
      else if(x <= x1 && y > y1){ //moving up (left side)
        y -= speed;
      }
    }
    else{
      if (y >= y3 && x < x3){ //moving right (bottom side)
        x += speed;
      }
      else if(y <= y1 && x > x1){ //moving left (top side)
        x -= speed;
      }
      else if(x <= x1 && y < y3){ //moving down (left side)
        y += speed;
      }
      else if(x >= x3  && y > y1){ //moving up (right side)
        y -= speed;
      }
    }
    setBounds((int)x, (int)y, 14, 14);
    revalidate();
    repaint();
  }
}