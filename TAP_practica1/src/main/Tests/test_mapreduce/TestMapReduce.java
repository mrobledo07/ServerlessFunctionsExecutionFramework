package main.Tests.test_mapreduce;

import main.FaaS.Controller;
import main.MapReduce.*;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

public class TestMapReduce {
    public static void main(String[] args) throws FileNotFoundException {


        Controller controller = new Controller(10, 1024);
        Function<List<String>, Map<String, Integer>> func = list -> WordCount.wordCount(list);
        controller.registerAction("wordCount", func, 512);

        Function<List<String>, Integer> func2 = list -> CountWords.countWords(list);
        controller.registerAction("countWords", func2, 512);
        
        List<List<String>> textCollections = FilesReader.readFiles();
        
        Future<List<Map<String, Integer>>> wordCountResults = controller.invoke_async("wordCount", textCollections);
        Future<List<Integer>> countWordsResults = controller.invoke_async("countWords", textCollections);
        
        try {
            Map<String, Integer> finalWordCountResult = Reduce.reduceWordCount(wordCountResults.get());
            int finalCountWordsResult = Reduce.reduceCountWords(countWordsResults.get());
            System.out.println(countWordsResults.get());
            System.out.println(finalCountWordsResult);
            saveWordCountsToFile(finalWordCountResult, "src/main/Tests/test_mapreduce/results/mapReduceResults.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        controller.shutdown();
    }

    private static void saveWordCountsToFile(Map<String, Integer> map, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}
