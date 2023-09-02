// Joseph Quinn
// July 18, 2023:

//This class is utilized to generate 5 letter english words for our game to use


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;
class WordGenerator
{

    String word;

    WordGenerator()
    {
        int n = 1 + (int)(Math.random() * ((2314) + 1)); // generates random number to search word
        String line = null;
        try (Stream<String> lines = Files.lines(Paths.get("src/resources/wordlist.txt"))) //where wordlist dictionary is stored
        {
            line = lines.skip(n).findFirst().get();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        this.word = line;

    }

    public String getRandomString()
    {
     return word;
    }

    public boolean isWord(String word) // checks if string parameter is in our word dictionary
    {
        String line;
        File words = new File("src/resources/wordlist.txt");

        try {
            FileReader fr = new FileReader(words);
            BufferedReader br = new BufferedReader(fr);

            while((line= br.readLine())!=null)
            {
                if(line.equals(word))
                    return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
