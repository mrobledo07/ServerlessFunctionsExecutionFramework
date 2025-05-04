package main.Tests.test_invoke;

import java.util.Map;
import java.util.function.Function;

import main.FaaS.Controller;

public class TestUniqueInvoke {

    public static void main(String[] args) {
        Controller controller = new Controller(4, 1024);
        Function<Map<String, Integer>, Integer> f = x -> x.get("x") + x.get("y");
        controller.registerAction("addAction", f, 256);

       
        try {
            int res = controller.invoke("addAction", Map.of("x", 6, "y", 2));
            res = controller.invoke("addAction", Map.of("x", 6, "y", 2));
            res = controller.invoke("addAction", Map.of("x", 6, "y", 2));
            res = controller.invoke("addAction", Map.of("x", 6, "y", 2));
            res = controller.invoke("addAction", Map.of("x", 6, "y", 2));
            System.out.println(res);
        } catch (RuntimeException e) {
            System.out.println(e.getCause().getMessage());
        }


        Function<Map<String, String>, String> f1 = x -> x.get("x") + x.get("y");
        controller.registerAction("concatAction", f1, 256);


        try {
            String res = controller.invoke("concatAction", Map.of("x", "Hola ", "y", "Mundo"));
            System.out.println(res);
        } catch (RuntimeException e) {
            System.out.println(e.getCause().getMessage());
        }
    }

}
