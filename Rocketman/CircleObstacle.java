package Rocketman;

import java.lang.Math;

class CircleObstacle extends Obstacle{
  private int radius;
  private int centerX;
  private int centerY;
  private double speed;
  private boolean movingClockwise = false;
  //constructor, accepts a center x and y, radius, direction, and speed 
  protected CircleObstacle(int centerX, int centerY, int radius, int position, boolean clockwise, double time){
    super();
    movingClockwise = clockwise;
    this.centerX = centerX;
    this.centerY = centerY;
    this.radius = radius;
    switch (position){
      case 0: //top
        x= centerX;
        y = centerY-radius;
        break;
      case 1: //right
        x= centerX+radius;
        y = centerY;
        break;
      case 2: //down
        x= centerX;
        y = centerY+radius;
        break;
      case 3: //left
        x= centerX-radius;
        y = centerY;
        break;
      default:
        throw new RuntimeException("invalid position");
    }
    double circumference = 2*Math.PI*radius;
    this.speed = circumference/(time*62);
  }
  @Override
  protected void move(){
    //depending on the quadrant, x and y either increase or decrease for the circle (inspired by the unit circle
    if (movingClockwise){
      if (Math.abs(y - centerY) <= 0.5*radius){
        //left quadrants (y goes up, and to maintain the radius, makes sure x^2 + y^2 = r^2)
        if (x-centerX < 0){
          y-=speed;
          x=-Math.sqrt(Math.abs(radius*radius-(y-centerY)*(y-centerY)))+centerX;;//USED TO HAVE LARGE BUG
        }
        //right quadrants
        else{
          y+=speed;
          x=Math.sqrt(radius*radius-(y-centerY)*(y-centerY))+centerX;
        }
      }
      else{
        //top quadrants (x goes right, and to maintain the radius, makes sure x^2 + y^2 = r^2)
        if (y-centerY < 0){
          x+=speed;
          y=-Math.sqrt(radius*radius-(x-centerX)*(x-centerX))+centerY;
        }
        //bottom quadrants
        else{
          x-=speed;
          y=Math.sqrt(radius*radius-(x-centerX)*(x-centerX))+centerY;
        }
      }
    }
    else{
      //same logic, except displacement is opposite
      if (Math.abs(y - centerY) <= 0.5*radius){
        //left quadrants (y goes down, and to maintain the radius, makes sure x^2 + y^2 = r^2)
        if (x-centerX < 0){
          y+=speed;
          x=-Math.sqrt(Math.abs(radius*radius-(y-centerY)*(y-centerY)))+centerX;;//USED TO HAVE LARGE BUG
        }
        //right quadrants
        else{
          y-=speed;
          x=Math.sqrt(radius*radius-(y-centerY)*(y-centerY))+centerX;
        }
      }
      else{
        //top quadrants (x goes left, and to maintain the radius, makes sure x^2 + y^2 = r^2)
        if (y-centerY < 0){
          x-=speed;
          y=-Math.sqrt(radius*radius-(x-centerX)*(x-centerX))+centerY;
        }
        //bottom quadrants
        else{
          x+=speed;
          y=Math.sqrt(radius*radius-(x-centerX)*(x-centerX))+centerY;
        }
      }
    }
    setBounds((int)x, (int)y, 14, 14);
    revalidate();
    repaint();
  }
}