package main.Tests.test_decorator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

import main.Decorator.MemoizationDecorator;
import main.Decorator.TimerFunctionDecorator;
import main.FaaS.Controller;

public class TestDecorator {
    public static void main(String[] args){
        Controller controller = new Controller(4, 1024);
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");

        Function<Map<String, Integer>, Integer> memoizationFunction = new MemoizationDecorator<>(f);
        Function<Map<String, Integer>, Integer> memoizationTimerFunction = new TimerFunctionDecorator<>(memoizationFunction);
        controller.registerAction("addAction", memoizationTimerFunction, 512);

        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8),
                Map.of("x", 5, "y", 2),
                Map.of("x", 2, "y", 3),
                Map.of("x", 15, "y", 8),
                Map.of("x", 15, "y", 8));

        System.out.println("--------Testing Decorator with RoundRobin policy--------");
        Future<List<Integer>> results = controller.invoke_async("addAction", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



        // Cálculo factorial
        System.out.println("------------------FACTORIAL------------------");
        Function<Map<String, Integer>, Integer> factorial = x -> {
            int n = x.get("n");
            int result = 1;
            for (int i = 1; i <= n; i++) {
                result *= i;
            }
            return result;
        };

        Function<Map<String, Integer>, Integer> memoizedFactorial = new MemoizationDecorator<>(factorial);
        Function<Map<String, Integer>, Integer> timedMemoizedFactorial = new TimerFunctionDecorator<>(memoizedFactorial);
        controller.registerAction("factorialAction", timedMemoizedFactorial, 512);
        Future<Integer> result = controller.invoke_async("factorialAction", Map.of("n", 5));


        try {
            System.out.println("Factorial: " + result.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        controller.shutdown();
    }
}
