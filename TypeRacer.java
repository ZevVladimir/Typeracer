/*TypeRacer               Ze'ev Vladimir
 * 
 * GUI for TypeRacer
 */

import javax.swing.*;
import javax.swing.JFrame;

public class TypeRacer
{
  public static void main (String[] args)
  {
    try {
      UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    JFrame frame = new JFrame ("TypeRacer");
    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    
    TypeRacerPanel panel = new TypeRacerPanel();
    frame.getContentPane().add(panel);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.pack();
    frame.setVisible(true);
  }
}