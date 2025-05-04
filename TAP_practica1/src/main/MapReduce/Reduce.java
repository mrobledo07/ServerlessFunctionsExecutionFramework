package main.MapReduce;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * The Reduce class provides methods for reducing word counts and total word counts.
 */
public class Reduce {
    
    /**
     * Reduces a list of word count maps into a single word count map.
     *
     * @param wordCountList the list of word count maps
     * @return a map containing the reduced word counts
     */
    public static Map<String, Integer> reduceWordCount(List<Map<String, Integer>> wordCountList) {
        Map<String, Integer> resultMap = new HashMap<>();

        for (Map<String, Integer> wordCount : wordCountList) {
            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                String word = entry.getKey();
                Integer count = entry.getValue();
                resultMap.put(word, resultMap.getOrDefault(word, 0) + count);
            }
        }

        return resultMap;
    }

    /**
     * Reduces a list of word counts into a total count.
     *
     * @param countWordsList the list of word counts
     * @return the total count of words
     */
    public static int reduceCountWords(List<Integer> countWordsList){
        int total = 0;
        for (int count : countWordsList) {
            total += count;
        }
        return total;
    }
}
