package main.Tests.test_async;


import main.FaaS.Controller;
import main.Strategy.BigGroup;
import main.Strategy.GreedyGroup;
import main.Strategy.UniformGroup;

import java.util.Map;
import java.util.function.Function;
import java.util.concurrent.Future;

public class TestUniqueAsync {
    public static void main(String[] args) {
        Controller controller = new Controller(4, 1024);
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");
        controller.registerAction("addAction", f, 512);

        // Se ejecutan 1 acción por invoker
        System.out.println("--------Testing unique async invoke with RoundRobin policy--------");
        Future<Integer> fut1 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut2 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut3 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut10 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut11 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut12 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));

        try {
            System.out.println(fut1.get() + fut2.get() + fut3.get() + fut10.get() + fut11.get() + fut12.get());
        }catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }

        // Se ejecutan 2 acciones por invoker (lo máximo por invoker)
        System.out.println("--------Testing unique async invoke with GreedyGroup policy--------");
        controller.setPolicy(new GreedyGroup());
        Future<Integer> fut4 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut5 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut6 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut7 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut8 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut9 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut13 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut14 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut15 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));

        try {
            System.out.println(fut4.get() + fut5.get() + fut6.get() + fut7.get() + fut8.get() + fut9.get() + fut13.get() + fut14.get() + fut15.get());
        }catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }

        // Se ejecutan 4 acciones por invoker

        System.out.println("--------Testing unique async invoke with UniformGroup policy--------");
        controller.setPolicy(new UniformGroup(4));
        controller.registerAction("addAction", f, 256);
        Future<Integer> fut16 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut17 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut18 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut19 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut20 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut21 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut22 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut23 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));

        try {
            System.out.println(fut16.get() + fut17.get() + fut18.get() + fut19.get() + fut20.get() + fut21.get() + fut22.get() + fut23.get());
        }catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }

        // Se empaquetan los grupos de 2 en 2 en cada invoker

        System.out.println("--------Testing unique async invoke with BigGroup policy--------");
        controller.setPolicy(new BigGroup(2));

        Future<Integer> fut24 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut25 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut26 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut27 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut28 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut29 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut30 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));
        Future<Integer> fut31 = controller.invoke_async("addAction", Map.of("x", 6, "y", 2));

        try {
            System.out.println(fut24.get() + fut25.get() + fut26.get() + fut27.get() + fut28.get() + fut29.get() + fut30.get() + fut31.get());
        }catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }
        
        
        controller.shutdown();
    }
}
