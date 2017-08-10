package Rocketman;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

abstract class Obstacle extends JLabel{
  protected double x;
  protected double y;
  protected Obstacle(){
    super(new ImageIcon("Rocketman/fireball.png"));
  }
  protected abstract void move();
}