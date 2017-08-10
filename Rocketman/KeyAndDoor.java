package Rocketman;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.border.LineBorder;

class KeyAndDoor{
  protected final int keyX;
  protected final int keyY;
  protected final int doorX;
  protected final int doorY;
  protected final int doorWidth;
  protected final int doorHeight;
  protected final JLabel key;
  protected final JPanel door;
  protected KeyAndDoor(int keyX, int keyY, int doorX, int doorY, int doorWidth, int doorHeight){
    this.keyX = keyX;
    this.keyY = keyY;
    this.doorX = doorX;
    this.doorY = doorY;
    this.doorWidth = doorWidth;
    this.doorHeight = doorHeight;
    key = new JLabel(new ImageIcon("Rocketman/key.png"));
    key.setBounds(keyX, keyY, 20, 20);
    door = new JPanel();
    door.setBounds(doorX, doorY, doorWidth, doorHeight);
    door.setBackground(Color.ORANGE);
    door.setBorder(new LineBorder(Color.RED));
  }
}