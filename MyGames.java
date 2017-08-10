import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import HillHeroes.*; //necessary to import games' relative packages in order to use the .java files in them
import Snake.*;
import BattleSnake.*;
import TetrisWorld.*;
import Chess.*;
import Rocketman.*;

//Decided to make a class that will hold all games I made + will make! This is basically a menu that can launch all
public class MyGames extends JFrame{
  private JFrame frame, window;
  private final Border thinBorder = new LineBorder(Color.BLACK, 5);
  public MyGames(){
    //GUI basics, a size, name, background, etc..
    setTitle("Nabil's Games");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200,690);
    setLocationRelativeTo(null);
    setBackground(new Color(215,230,215));
    //title and subtitle JLabels are created with a nice font called "Nabil"
    JLabel title = new JLabel("Welcome to Nabil's Game Collection", SwingConstants.CENTER);
    title.setFont(new Font("Nabil", Font.PLAIN, 24)); //I get to name my own font in Java, how cool is that
    JLabel subTitle = new JLabel("Pick a game below and have fun!", SwingConstants.CENTER);
    subTitle.setFont(Font.decode("Nabil")); //by default a font is size 12, which is fitting as 24/2 == 12
    //using gridBagLayout, as I did in Snake's game over screen is fitting here as buttons and JLabels turn out great
    GridBagLayout buttons = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints(); //the best thing on Earth, basically allows formatting the layout
    setLayout(buttons);
    JButton hh = makeButton("Images/HillHeroes.png");
    JButton s = makeButton("Images/Snake.png");
    JButton bs = makeButton("Images/BattleSnake.png");
    JButton tw = makeButton("Images/TetrisWorld.png");
    JButton ch = makeButton("Images/Chess.png");
    JButton rm = makeButton("Images/Rocketman.png");
    hh.addActionListener(new HH());
    s.addActionListener(new S()); //will avoid name conflicts by naming the actionListener class S
    bs.addActionListener(new BS());
    tw.addActionListener(new TW());
    ch.addActionListener(new C());
    rm.addActionListener(new RM());
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(2, 2, 2, 2); //spacing between the two lines of text
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 2;
    add(title, c); //whenever a JThing is added to the layout, the constraints specify where they are
    c.gridy = 1;
    add(subTitle, c);
    c.insets.set(10,10,10,10); //spacing between the JButtons
    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 2;
    add(rm, c);
    c.gridx = 1;
    add(ch, c);
    c.gridx = 2;
    add(tw,c);
    c.gridx = 3;
    add(bs, c);
    c.gridy = 3;
    c.gridx = 0;
    add(s, c);
    c.gridy = 3;
    c.gridx = 1;
    add(hh, c);
    setVisible(true); //we don't want an invisible JFrame
  }
  
  private JButton makeButton(String imageName){
    JButton button = new JButton(new ImageIcon(imageName));
    button.setSize(250,250); //huge square buttons are cool
    button.setBorder(thinBorder); //gets rid of the automatic button border
    return button;
  }
  
  public static void main(String[] args){
    new MyGames();
  }

  private class S implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
      new Snake(); //launches the snake gae;
    }
  }
  
  
  private class HH implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
      new HillHeroes();
    }
  }
  
  private class BS implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
      new BattleSnake();
    }
  }
  
  private class TW implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
      new TetrisWorld();
    }
  }
  
  private class C implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
      new Chess();
    }
  }
  
  private class RM implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
      new Rocketman();
    }
  }
}