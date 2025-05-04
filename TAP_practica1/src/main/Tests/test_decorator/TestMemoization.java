package main.Tests.test_decorator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

import main.Decorator.MemoizationDecorator;
import main.FaaS.Controller;

public class TestMemoization {
    public static void main(String[] args){
        Controller controller = new Controller(4, 1024);
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");
        Function<Map<String, Integer>, Integer> memoizationFunction = new MemoizationDecorator<>(f);
        controller.registerAction("addAction", memoizationFunction, 512);

        // Habrán 3 cache hits ya que se repiten 3 veces los mismos argumentos

        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8),
                Map.of("x", 2, "y", 3),
                Map.of("x", 8, "y", 8),
                Map.of("x", 15, "y", 8),
                Map.of("x", 15, "y", 8));

        System.out.println("--------Testing Decorator with RoundRobin policy--------");
        Future<List<Integer>> results = controller.invoke_async("addAction", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        controller.shutdown();
    }
}
