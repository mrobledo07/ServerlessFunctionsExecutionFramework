package main.Tests.test_invoke;

import java.util.Map;

import main.FaaS.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TestGroupInvoke {
    public static void main(String[] args) {
        Controller controller = new Controller(4, 1024);
        // Suponiendo que addAction es una acción que suma dos enteros
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


        
        List<Integer> input1 = Arrays.asList(1,2,3,4,5);
        Function<Integer, Integer> f1 = x -> x + 1;
        controller.registerAction("increment", f1, 256);
        try {
            List<Integer> result = controller.invoke("increment", input1);
            System.out.println(result.toString());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }


    }

}