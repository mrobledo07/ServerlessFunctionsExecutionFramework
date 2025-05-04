package main.Tests.test_proxy;

import main.FaaS.Controller;
import main.Reflection.IActionProxy;

import java.util.Map;
import java.util.function.Function;

public class TestProxy {
    public static void main(String[] args) {

        // Código para ejecutar acciones con proxy
        Controller controller = new Controller(4, 1024);

        IActionProxy proxy = controller.createProxy();

        Integer result = proxy.sumNumbers(2,3);
        System.out.println(result);

        result = proxy.calculateFactorial(5);
        System.out.println(result);


        // Código para ejecutar acciones con funciones MANUALMENTE
        Controller controller1 = new Controller(4, 1024);
        Function<Map<String, Integer> , Integer> sumNumbers = x -> x.get("a") + x.get("b"); 
        Function<Integer, Integer> calculateFactorial = x -> {
            int factorial = 1;
            for (int i = 1; i <= x; i++) {
                factorial *= i;
            }
            return factorial;
        };

        controller1.registerAction("sumNumbers", sumNumbers, 512);
        controller1.registerAction("calculateFactorial", calculateFactorial, 512);

        Integer result1 = controller1.invoke("sumNumbers", Map.of("a", 2, "b", 3));
        System.out.println(result1);

        result1 = controller1.invoke("calculateFactorial", 5);
        System.out.println(result1);

    }
}
