package main.Tests.test_observer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

import main.FaaS.Controller;
import main.Observer.MetricsSummary;

public class TestObserver {
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
                Map.of("x", 15, "y", 8),
                Map.of("x", 15, "y", 8));

        System.out.println("--------Testing Observer with RoundRobin policy--------");
        Future<List<Integer>> results = controller.invoke_async("addAction", input);

        try {
            System.out.println(results.get().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        MetricsSummary summary = controller.calculateMetricsSummary();
        if (summary != null) {
            System.out.println(summary);
        } else {
            System.out.println("No hay métricas disponibles.");
        }

        controller.shutdown();
    }
}
