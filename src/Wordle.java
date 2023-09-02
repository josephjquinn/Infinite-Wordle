// Joseph Quinn
// July 18, 2023:

// this class sets up the wordle frame and panel GUI, and the wordle logic for playing the game

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.*;

public class Wordle implements ActionListener
{
    WordGenerator keys = new WordGenerator(); // creates instance of the wordGenerator class

    String key = keys.getRandomString(); // utilizes method from WordGenerator.java to generate random answer for each game

    JFrame frame = new JFrame();// creating GUI elements
    JPanel game = new JPanel(new GridLayout(5, 5, 10, 10));
    JPanel display = new JPanel();
    JPanel buttonField = new JPanel();
    JButton reset = new JButton();
    JButton log = new JButton();
    JButton reveal = new JButton();
    JLabel info = new JLabel();
    JLabel turnLabel = new JLabel();
    JLabel[][] words = new JLabel[5][5];

    int turn = 0;// instantiating game variables
    String[] lastGuess = new String[5];
    boolean win;
    boolean running;
    boolean usedReveal;
    String[] pastGuesses = new String[5];
    int letter = 0;
    boolean canContinue = true;


    Wordle()
    {
        for (int i = 0; i < 5; i++) // creating wordle letter slots
        {
            for (int j = 0; j < 5; j++)
            {
                words[i][j] = new JLabel("_", SwingConstants.CENTER);
                words[i][j].setFont((new Font("Ink Free", Font.BOLD, 50)));
                game.add(words[i][j]);
                words[i][j].setEnabled(false);
            }
        }


        display.setPreferredSize(new Dimension(600, 75)); // display GUI
        display.setLayout(new BorderLayout());

        info.setFont(new Font("Ink Free", Font.BOLD, 30));// font settings
        info.setHorizontalAlignment(SwingConstants.CENTER);
        display.add(info, BorderLayout.CENTER);

        turnLabel.setForeground(Color.black); // font settings
        turnLabel.setFont(new Font("Ink Free", Font.BOLD, 25));
        turnLabel.setText("5 Turns Remaining");
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        display.add(turnLabel, BorderLayout.SOUTH);


        buttonField.setLayout(new GridLayout(1, 3)); // creating game buttons
        buttonField.setPreferredSize(new Dimension(600, 80));
        log.addActionListener(this);
        reset.addActionListener(this);
        reveal.addActionListener(this);
        log.setText("Log");
        reset.setText("Reset");
        reveal.setText("Reveal");
        log.setFont(new Font("Ink Free", Font.BOLD, 15));
        reveal.setFont(new Font("Ink Free", Font.BOLD, 15));
        reset.setFont(new Font("Ink Free", Font.BOLD, 15));
        buttonField.add(log);
        buttonField.add(reset);
        buttonField.add(reveal);


        frame.setLocationRelativeTo(null); //frame setup
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setLayout(new BorderLayout(0, 25));
        frame.add(game);
        frame.add(display, BorderLayout.NORTH);
        frame.add(buttonField, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.addKeyListener(new MyKeyAdapter());

        running = true; // starts game

    }

    public int[][] checkWord(String word) // method to check if inputted word is a correct
    {
        int pos = -1; // setting up method variables and arrays
        char[] guess = toCharacterArray(word);
        char[] ans = toCharacterArray(key);
        int[] green = new int[5];
        int[] yellow = new int[5];
        int[] white = new int[5];



        /* sets each position in green array to a '1' if that particular letter is in the key AND in the correct
           position in the key. Then removes said letter from the key array by replacing it with
           a '#', this is so it's not counted multiple times.

           Ex. key: "stone"  guess: "silly" ->>> green = [1,0,0,0,0] ans = [#tone]
        */
        for(int i = 0;i<5;i++)
        {
            if(guess[i] == ans[i])
            {
                green[i] = 1;
                ans[i] = '#';
            }
        }

         /* sets each position in yellow array to a '1' if that particular letter is in the correct
           position in the key but NOT in the correct position. Then it removes said letter from the
           key array by replacing it with a '#', this is so it's not counted multiple times.

           Ex. key: "stone"  guess: "catch" ->>> yellow = [0,0,1,0,0] ans = [s#one]
        */

        for(int i = 0; i<5; i++)
        {
            for(int j = 0; j<5; j++)
            {
                if(guess[i] == ans[j] && green[i] != 1)
                {
                    yellow[i] = 1;
                    ans[j] = '#';
                }
            }

        }

        /* sets each position in white array to a '1' if that particular letter is NOT in
           the key at all. It does this by series of elimination with the yellow and green arrays.
           Then it removes said letter from the key array by replacing it with a '#', this is so
           it's not counted multiple times.

           Ex. key: "stone"  guess: "crisp" ->>> white = [1,1,1,1,1] ans = [#####]
        */

        for(int i = 0; i<5; i++)
        {
            if(yellow[i] == green[i])
            {
                white[i] = 1;
            }
        }

        int[][] colorArray = {green, yellow, white}; //combines the color arrays into a 2step array
        return colorArray;

    }


    public void writeColor(int[][] array, String word) // method that outputs console guess to the screen and changes it to
                                                       // the appropriate color based on its position in the colorArray
    {
        char[] guess = toCharacterArray(word);

        for(int i=0; i<5;i++)
        {
            if(array[0][i] == 1) // color change to green
            {
                //System.out.println("g");
                words[turn][i].setForeground(Color.green);
                words[turn][i].setFont(new Font("Ink Free", Font.BOLD, 50));
                words[turn][i].setText(String.valueOf(guess[i]));
            }
            if(array[1][i] == 1) // color change to yellow
            {
                //System.out.println("y");
                words[turn][i].setForeground(Color.yellow);
                words[turn][i].setFont(new Font("Ink Free", Font.BOLD, 50));
                words[turn][i].setText(String.valueOf(guess[i]));
            }
            if(array[2][i] == 1) // color change to white(black)
            {
                //System.out.println("w");
                words[turn][i].setForeground(Color.black);
                words[turn][i].setFont(new Font("Ink Free", Font.BOLD, 50));
                words[turn][i].setText(String.valueOf(guess[i]));
            }
        }
    }

    // This function checks if the green array matches the key
    // if so it triggers the gameWin method.
    // It also checks the turn the player is on, if its been 5 turns
    // it triggers the gameOver method
    public void checkWin(int[][] array, String word)
    {
        int[] test = array[0];
        int[] solution = {1,1,1,1,1};

        if(Arrays.equals(test, solution))
        {
            gameWin();
        }

        if (turn>4)
        {
            gameOver();
        }
    }

    // game over display change method
    public void gameOver()
    {
        running = false;
        win = false;

        System.out.println("#### GAME OVER ####");
        info.setForeground(Color.red);
        info.setText(("Game Over"));
        turnLabel.setText("0 Turns Remaining");
        turnLabel.setForeground(Color.blue);
        turnLabel.setText("Solution: " + key);
        //log();
    }

    // game win display change method
    public void gameWin()
    {
        running = false;
        win = true;
        info.setForeground(Color.blue);
        info.setText(("WINNER"));
        System.out.println("#### GAME WIN ####");
    }

    // logs session data to log.txt file(src/resources/log.txt)
    public void log()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String timeStamp = dateFormat.format(cal.getTime());
        File log = new File("src/resources/log.txt");

        try{

            FileWriter fileWriter = new FileWriter(log, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            if(win)
            {
                bufferedWriter.write(dateFormat.format(cal.getTime()));
                bufferedWriter.newLine();
                bufferedWriter.write("Win in " + (turn) + " turns");
                bufferedWriter.newLine();
                if(usedReveal)
                {
                    bufferedWriter.write("----Used Reveal----");
                    bufferedWriter.newLine();
                }
                for(int i = 0; i<5; i++)
                {
                    if(pastGuesses[i] != null)
                    {
                        bufferedWriter.write("Guess " + (i+1) + " --> ");
                        bufferedWriter.write(pastGuesses[i]);
                        bufferedWriter.newLine();
                    }
                }
                bufferedWriter.write("Solution: " + key);
            }
            else
            {
                bufferedWriter.write(dateFormat.format(cal.getTime()));
                bufferedWriter.newLine();
                bufferedWriter.write("Game Loss");
                bufferedWriter.newLine();
                for(int i = 0; i<5; i++)
                {
                    bufferedWriter.write("Guess " + (i+1) + " --> ");
                    bufferedWriter.write(pastGuesses[i]);
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("Solution: " + key);
            }
            bufferedWriter.newLine();
            bufferedWriter.write("");
            bufferedWriter.newLine();
            bufferedWriter.write("");
            bufferedWriter.close();

            System.out.println("Done");
        }
        catch(IOException e)
        {
            System.out.println("COULD NOT LOG!!");
        }
    }

    // method to convert string to a char array
    public char[] toCharacterArray( String s )
    {
        if ( s == null )
        {
            return null;
        }

        int len = s.length();
        char[] array = new char[len];
        for (int i = 0; i < len ; i++)
        {
            array[i] = s.charAt(i);
        }

        return array;
    }

    // store past guess data
    public void storeGuess(String guess, int turn)
    {
        pastGuesses[turn] = guess;
    }

    @Override // method for button operation
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == reset) // reset button
        {
            frame.dispose();
            new Wordle();
            turn = 0;
            System.out.println("--#### RESET ####--");
        }
        if(e.getSource() == reveal) // reveal button
        {
            usedReveal = true;
            info.setText("Solution: " + key);
            reveal.setFocusable(false);
            System.out.println("--#### REVEAL ####--");
        }
        if(e.getSource() == log) // log button
        {
            if(!running)
            {
                log();
            }

        }
    }
    public class MyKeyAdapter extends KeyAdapter // method to interpret keypress data
    {
        @Override
        public void keyPressed(KeyEvent e)
        {

            int code = e.getKeyCode();
            if(code>=65 && code<=90) // checks to see if keypress is a usable letter
            {
                if(canContinue && running)
                {
                    if(letter<5)
                    {
                        System.out.println(Character.toLowerCase((char)code));
                        words[turn][letter].setText(String.valueOf((char)code).toLowerCase());
                        letter++;
                    }
                    else
                        canContinue=false;
                }
            }
            else if(code==10) // interprets 'enter' button and submits guess
            {
                if (letter == 5)
                {

                    String word = "";
                    for (int i = 0; i < 5; i++)
                    {
                        word = word + words[turn][i].getText();
                        word = word.toLowerCase();
                    }

                    if (new WordGenerator().isWord(word)) // if submission is an english word it applies checkWord/Win method
                    {
                        if (!(Arrays.asList(lastGuess).contains(word)))
                        {
                            System.out.println("Guess " + (turn+1) +  " -> " + word);
                            writeColor(checkWord(word), word);
                            checkWin(checkWord(word), word);
                            storeGuess(word, turn);
                            lastGuess[turn] = word;

                            letter = 0;
                            turn++;
                            canContinue = true;
                            turnLabel.setText((5 - turn) + " Turns Remaining"); // display
                        }

                        else
                        {
                            info.setForeground(Color.RED); // else cases for display
                            info.setText("Repeat Word");
                        }

                    }

                    else
                    {
                        info.setForeground(Color.RED); // else cases for display
                        info.setText("Word Doesn't Exist!");
                    }
                }

                else
                {
                    info.setForeground(Color.RED); // else cases for display
                    info.setText("Fill Board");
                }

            }
            else if(code==8) // deletes letter of guess, clears display of letter
            {
                if(letter>0)
                {
                    System.out.println("del");
                    letter--;
                    canContinue=true;
                    words[turn][letter].setText("_");
                }
            }
        }
    }
}
