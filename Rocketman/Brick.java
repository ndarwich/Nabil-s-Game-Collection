package Rocketman;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import java.awt.Color;

class Brick extends JPanel {
  protected final int x;
  protected final int y;
  protected final int width;
  protected final int height;
  protected Brick(int x, int y, int width, int height){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    setLayout(new GridLayout(height/20, width/20));
    setBounds(x,y,width,height);
    for (int i = 0; i < (height/20)*(width/20); i++){
      add(makeTile());
    }
  }
  private JPanel makeTile(){
    JPanel tile = new JPanel();
    tile.setBackground(new Color(240,240,240));
    tile.setBorder(new BevelBorder(BevelBorder.RAISED));
    return tile;
  }
}