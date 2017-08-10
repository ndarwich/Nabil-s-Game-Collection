package Rocketman;

import javax.swing.JPanel;
import java.awt.Color;

class Safespot extends JPanel {
  protected final int x;
  protected final int y;
  protected Safespot(int x, int y){
    this.x = x;
    this.y = y;
    setBounds(x,y,20,20);
    setBackground(new Color(180,240,150));
  }
}