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

public class TestWordCount {
    public static void main(String[] args) throws FileNotFoundException {


        Controller controller = new Controller(10, 1024);
        Function<List<String>, Map<String, Integer>> func = list -> WordCount.wordCount(list);
        controller.registerAction("wordCount", func, 512);
        

        List<List<String>> textCollections = FilesReader.readFiles();


        Future<List<Map<String, Integer>>> wordCountResults = controller.invoke_async("wordCount", textCollections);

        
        try {
            List<Map<String, Integer>> finalWordCountResult = wordCountResults.get();
            saveWordCountsToFile(finalWordCountResult, "src/main/Tests/test_mapreduce/results/wordCountResults.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        controller.shutdown();
    }

    private static void saveWordCountsToFile(List<Map<String, Integer>> list, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map<String, Integer> map : list){
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    writer.write(entry.getKey() + ": " + entry.getValue());
                    writer.newLine();
                }
                writer.write("---- Map Separation ----");
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}
