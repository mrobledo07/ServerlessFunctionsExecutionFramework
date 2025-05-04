package main.Tests.test_policy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import main.FaaS.Controller;
import main.Strategy.GreedyGroup;

public class TestGreedyGroup {
     public static void main(String[] args) {
        Controller controller = new Controller(4, 2048);
        controller.setPolicy(new GreedyGroup());
        // Suponiendo que addAction es una acción que suma dos enteros
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");
        controller.registerAction("addAction512",f, 512);
        controller.registerAction("addAction1024", f, 1024);


        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8));

        try {
            List<Integer> result = controller.invoke("addAction1024", input);
            System.out.println(result.toString());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8),
                Map.of("x", 5, "y", 2),
                Map.of("x", 12, "y", 66),
                Map.of("x", 15, "y", 8));

        try {
            List<Integer> result = controller.invoke("addAction512", input);
            System.out.println(result.toString());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }
}
