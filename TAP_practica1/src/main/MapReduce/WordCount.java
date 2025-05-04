package main.MapReduce;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * The WordCount class provides a method for counting the occurrences of words in a list of strings.
 */
public class WordCount {
    
    /**
     * Counts the occurrences of words in the given list of strings.
     *
     * @param text the list of strings
     * @return a map containing the word counts
     */
    public static Map<String,Integer> wordCount(List<String> text) {
        Map<String,Integer> wordCountMap = new HashMap<>();

        for(String word : text) {
            word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");

            // If it was a word, it is added to the dictionary
            if (!word.isEmpty()) {
                wordCountMap.merge(word, 1, Integer::sum); 
            }
        }

        return wordCountMap;
    }
}
