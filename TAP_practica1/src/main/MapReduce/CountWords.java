package main.MapReduce;

import java.util.List;

/**
 * The CountWords class provides a method to count the number of words in a list of strings.
 */
public class CountWords {

    /**
     * Counts the number of words in the given list of strings.
     *
     * @param text the list of strings
     * @return the number of words
     */
    public static int countWords(List<String> text) {
        int nWords = 0;

        for(String word : text) {
            word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");

            // If it was a word, it is added to the dictionary
            if (!word.isEmpty()) {
                nWords++; 
            }
        }

        return nWords;
    }
}
