/* TypeRacerPanel               Ze'evVladimir
 * 
 * Type racer (I hope)
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;  
import java.io.FileNotFoundException;  
import java.util.Scanner; 
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import java.time.LocalTime;
import java.text.DecimalFormat;
import java.io.*;
import java.io.BufferedReader;

public class TypeRacerPanel extends JPanel
{
  private JButton start;
  private JTextArea typing, text;
  private JLabel wpm;
  private int counter, number, startTime, endTime, enteredCounter, code, startTimer, fontSize, errors, dontRepeat;
  private double characters, wordsPerMinute, minutes, totalWrong, topScore, accuracy;
  private String adder, pass, passSub, enterString, wordsPerMinuteString, fontSizeString, accuracyDisplay;
  private char enterChar;
  private List<String> passage, correct, entered;
  private LocalTime clock;
  private boolean remove;
  private Color wrongRed, black;
  private Font fontUse;
  private DecimalFormat df;
  private Reader reader;
  
  public TypeRacerPanel()
  {
    try{    
      //instantiate lists needed
      passage = new ArrayList<String>();
      correct = new ArrayList<String>();
      entered = new ArrayList<String>();
      
      df = new DecimalFormat ("###.##");
      
      //instantiate variables and clock
      clock = LocalTime.now();
      dontRepeat = -1;
      enteredCounter = startTimer = errors = 0;
      characters = totalWrong = topScore = accuracy = 0.0;
      remove = true;
      wrongRed = new Color (255,25,25);
      black = new Color (0, 0, 0);
      
      //Access file with typing passages on them
      File myFile = new File("doc.txt");  
      BufferedReader reader = new BufferedReader (new FileReader(myFile));
      
      counter = 0;
      //Separate the passages from the doc and each one to a list
      try{
        while (reader.ready() == true)
        {
          adder = reader.readLine();
          passage.add(adder);
          counter++; //How many passages are in the doc
        }
      } catch(java.io.IOException a){
        a.printStackTrace();
      }
      //Create start button
      start = new JButton ("Start");
      Color startGreen = new Color (85,255,0);
      start.setBackground (startGreen);
      start.setForeground (Color.black);
      start.addActionListener (new StartListener());
      
      //Create text area where text appears but can't be edited
      text = new JTextArea ("Text will appear here");
      text.setEditable(false);
      text.setLineWrap(true);
      text.setBorder(BorderFactory.createLineBorder(black));
      
      //Create text area where player types
      typing = new JTextArea ("You will type here");
      typing.addKeyListener (new TypingListener());
      typing.setEditable(false);
      typing.setLineWrap(true);
      typing.setBorder(BorderFactory.createLineBorder(black));
      
      //Create label that displays all details
      wpm = new JLabel ("WPM: " + "Top Score: " + " Accuracy: " + " Total Errors: ");
      
      //Set font size for all elements
      Font font = text.getFont();
      fontUse = new Font(font.getFontName(), font.getStyle(), 16);
      text.setFont (fontUse);
      typing.setFont (fontUse);
      wpm.setFont(fontUse);
      start.setFont(fontUse);
      //Create grid layout and add all parts
      GridLayout layout = new GridLayout(4,1);
      setLayout(layout);
      add(text);
      add(typing);
      add(wpm);
      add(start);
      
      setPreferredSize (new Dimension (1000,500));
      setBackground (Color.white);
      
    }catch (FileNotFoundException e){
      e.printStackTrace();
    }
  }
  
  //when the start button is pressed
  private class StartListener implements ActionListener
  {
    public void actionPerformed (ActionEvent event)
    {
      //Get a random passage and set that as the one in the text textarea
      Random rand = new Random();
      number = rand.nextInt(counter + 1);
      while(number == dontRepeat){number = rand.nextInt(counter); System.out.println(number);} //Keep looking for passage that wasnt the last one
      pass = passage.get(number);
      text.setText (pass);
      start.setVisible(false);
      dontRepeat = number;
      
      int endSub = 1;
//set correct list to chosen passage
      for (int z = 0; z < pass.length(); z++)
      {
        passSub = pass.substring (z, endSub);
        correct.add(passSub); 
        endSub++;
      }
      typing.setEditable(true);
      typing.setText("");
    }
  }
  
  //Get information whenever something is typed
  private class TypingListener implements KeyListener
  {
    public void keyTyped (KeyEvent e)
    {
      enterChar = e.getKeyChar();
      //When first key is pressed get start time
      startTimer++; 
      clock = LocalTime.now();
      if (startTimer == 1){startTime = clock.toSecondOfDay();}
      
      //As long as user is typing within the amount of characters in correct prompt
      if (enteredCounter < correct.size())
      {
        //If the user didn't delete on their key press
        if (remove == false)
        {
          enterString = String.valueOf(enterChar);
          entered.add(enterString);
          //If the user made a mistake
          if (entered.get(enteredCounter).equals(correct.get(enteredCounter)) == false){errors++;}
          
          characters++; //Track how many characters user has typed
          totalWrong = findWrong(entered, correct); //calculate how much is wrong at this point
          enteredCounter++;
          
          //When the user finishes reset everything and display what the user got
          if (enteredCounter == correct.size() && totalWrong == 0)
          {
            typing.setEditable(false);
            text.setText("Text will appear here");
            typing.setText("You will type here");
            start.setVisible(true);
            wpm.setText("WPM: " + "Top Score: " + " Accuracy: " + " Total Errors: ");
            entered.clear();
            correct.clear();
            //Display final information in a pop up
            if(wordsPerMinute > topScore){topScore = (int)wordsPerMinute;}
            JOptionPane.showMessageDialog (null,"Nice job! Your WPM was: " + wordsPerMinuteString + "\n" + "Your accuracy was: " + accuracyDisplay + "%" + "\n" + 
                                           "Your top score is: " + topScore + "\n" + "Total Errors: " + errors);
            accuracyDisplay = "0";
            enteredCounter = errors = startTimer = 0;
            accuracy = characters = 0.0;
          }
        }
      }
      else if (enteredCounter >= correct.size() && remove == false) //If the user is typing extra characters
      {
        enteredCounter++;
        enterString = String.valueOf(enterChar);
        entered.add(enterString);
      }
      //Update user's info
      wpm.setText("WPM: " + wordsPerMinute(startTime, endTime, characters, totalWrong) + " Top Score: " + topScore +
                  " Accuracy: " + accuracy(characters, errors) + "%" + " Total Errors: " + errors);
      totalWrong = 0;
    }
    
    public void keyPressed (KeyEvent e) 
    {
      code = e.getKeyCode();
      if (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) //If user deletes remove from list what they deleted and subtract from counter of where user is
      {
        enteredCounter--;
        if (enteredCounter >= 0)
        {
          entered.remove(enteredCounter);
          totalWrong = findWrong(entered, correct); //Find total wrong
        }
        else if (enteredCounter < 0) {enteredCounter = 0;}
        remove = true; //If the user removed
      }
      else if (code != KeyEvent.VK_BACK_SPACE && code != KeyEvent.VK_DELETE){remove = false;} //If the user didn't delete
    }
    public void keyReleased (KeyEvent e) {}
    
    //Calculate user's words per minute
    public String wordsPerMinute (int startTime, int endTime, double characters, double totalWrong)
    {
      clock = LocalTime.now();
      endTime = clock.toSecondOfDay();
      minutes = (endTime - startTime)/60.0; //Get time in minutes
      wordsPerMinute = ((entered.size()/5) - totalWrong)/minutes; //Formula for WPM
      if (wordsPerMinute < 0){wordsPerMinute = 0;}
      wordsPerMinuteString = Double.toString((int)wordsPerMinute); //Convert to string
      return wordsPerMinuteString;
    }
    
    //Find if there are incorrect characters
    public double findWrong (List<String> entered, List<String> correct)
    {
      for(int m = 0; m < entered.size(); m++) //Loop through list 
      {
        if (entered.get(m).equals(correct.get(m)) == false) {totalWrong++;} //If there is an incorrect character increase total wrong
      }
      if(totalWrong > 0) {typing.setForeground(wrongRed);} //If there is anything wrong set text to red
      
      else if(totalWrong == 0) {typing.setForeground(black);} //If everything is right set text to black
      return totalWrong;
    }
    
    //Calculate user's accuracy
    public String accuracy (double characters, int errors)
    {
      accuracy = ((characters - errors)/characters) * 100; //Percent error
      accuracyDisplay = df.format(accuracy); //Convert to format
      return accuracyDisplay;
    }
  }
}
//have a list of what buttons are pressed remove them when released can detect if ctr and a are pressed
