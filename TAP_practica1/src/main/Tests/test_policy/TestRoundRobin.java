package main.Tests.test_policy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import main.FaaS.Controller;

public class TestRoundRobin {
     public static void main(String[] args) {
        Controller controller = new Controller(4, 1024);
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");
        controller.registerAction("addAction", f, 256);

        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8));

        try {
            List<Integer> result = controller.invoke("addAction", input);
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
            List<Integer> result = controller.invoke("addAction", input);
            System.out.println(result.toString());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }
}
