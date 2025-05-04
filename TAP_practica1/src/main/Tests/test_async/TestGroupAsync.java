package main.Tests.test_async;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

import main.FaaS.Controller;
import main.Strategy.BigGroup;
import main.Strategy.GreedyGroup;
import main.Strategy.UniformGroup;

public class TestGroupAsync {
    public static void main(String[] args) {
        Controller controller = new Controller(4, 1024);
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");
        controller.registerAction("addAction", f, 512);

        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8),
                Map.of("x", 5, "y", 2),
                Map.of("x", 12, "y", 66),
                Map.of("x", 15, "y", 8));



                
        System.out.println("--------Testing group async invoke with RoundRobin policy--------");
        Future<List<Integer>> results = controller.invoke_async("addAction", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }




        System.out.println("--------Testing group async invoke with GreedyGroup policy--------");
        controller.setPolicy(new GreedyGroup());
        results = controller.invoke_async("addAction", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }




        // Esta invocación dará error ya que no se pueden asignar 3 acciones a un mismo invoker (no tiene suficiente espacio en memoria)
        System.out.println("--------Testing group async invoke with UniformGroup policy ERROR--------");
        controller.setPolicy(new UniformGroup(3));
        results = controller.invoke_async("addAction", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }




        // Esta invocación no dará error
        System.out.println("--------Testing group async invoke with UniformGroup policy GOOD--------");
        controller.registerAction("addAction256", f, 256);
        results = controller.invoke_async("addAction256", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }




        System.out.println("--------Testing group async invoke with BigGroup policy--------");
        controller.setPolicy(new BigGroup(2));
        results = controller.invoke_async("addAction256", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        controller.shutdown();
    }
}
