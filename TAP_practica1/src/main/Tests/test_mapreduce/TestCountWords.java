package main.Tests.test_mapreduce;

import main.FaaS.Controller;
import main.MapReduce.CountWords;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;




public class TestCountWords {
    public static void main(String[] args) {
        Controller controller = new Controller(10, 1024);

        Function<List<String>, Integer> func = list -> CountWords.countWords(list);
        controller.registerAction("countWords", func, 512);


        List<List<String>> textCollections = FilesReader.readFiles();

        Future<List<Integer>> countWordsResults = controller.invoke_async("countWords", textCollections);
        
        try {
            List<Integer> finalCountWordsResult = countWordsResults.get();
            System.out.println(finalCountWordsResult.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        controller.shutdown();

    }


}
