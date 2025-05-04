package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.FaaS.Controller;

public class ControllerTest {
    Controller controller;  
    
    @BeforeEach
    public void setUp() {
        controller = new Controller(4, 1024);

        Function<Map<String, Integer>, Integer> add = x -> x.get("x") + x.get("y");
        Function<Map<String, String>, String> concat = x -> x.get("x") + x.get("y");
        Function<Integer, Integer> increment = x -> x + 1;
        Function<String, String> toUpperCase = x -> x.toUpperCase();
        Function<Integer, Integer> factorial = x -> {
            int result = 1;
            for (int i = 1; i <= x; i++) {
                result *= i;
            }
            return result;
        };
       
        controller.registerAction("addAction", add, 256);
        controller.registerAction("concatAction", concat, 256);
        controller.registerAction("incrementAction", increment, 256);
        controller.registerAction("toUpperCaseAction", toUpperCase, 256);
        controller.registerAction("factorialAction", factorial, 256);

    }

    @Test
    public void testRegisterAction(){
        String[] actions = {"addAction", "concatAction", "incrementAction", "toUpperCaseAction", "factorialAction"};
        // Comprobamos que las acciones se han registrado correctamente
        for (String action : actions) {
            assertEquals(true, controller.isActionRegistered(action));
        }

        Function<Integer, Integer> decrement = x -> x - 1;
        controller.registerAction("decrementAction", decrement, 256);

        // Comprobamos que la acción se ha registrado correctamente
        assertEquals(true, controller.isActionRegistered("decrementAction"));

        int result = controller.invoke("decrementAction", 5);
        assertEquals(4, result);
    }


    @Test
    public void testActionNonExistent(){
        // Comprobamos que la acción no existe
        assertEquals(false, controller.isActionRegistered("nonExistentAction"));
        Assertions.assertThrows(RuntimeException.class, () -> controller.invoke("nonExistentAction", 1));

    }

    @Test
    public void testInvokeActionIncrement() {
        int result = controller.invoke("incrementAction", 5);
        assertEquals(6, result);
    }

    @Test
    public void testInvokeActionSum() {
        int result = controller.invoke("addAction", Map.of("x", 6, "y", 2));
        assertEquals(8, result);
    }

    @Test
    public void testInvokeActionConcat() {
        String result = controller.invoke("concatAction", Map.of("x", "Miguel ", "y", "Robledo"));
        assertEquals("Miguel Robledo", result);
    }

    @Test
    public void testInvokeActionUpperCase() {
        String result = controller.invoke("toUpperCaseAction", "Miguel Robledo");
        assertEquals("MIGUEL ROBLEDO", result);
    }

    @Test
    public void testMemoryOverflow(){
        Function<Integer, Integer> tooBig = x -> x*1;
        // Recordamos que el Controller en total tiene 4096 MB de memoria (1024 por Invoker)
        controller.registerAction("tooBigAction", tooBig, 8192);
        Assertions.assertThrows(RuntimeException.class, () -> controller.invoke("testIncrease", 1));
    }

    @Test
    public void testMemorySeqOverflow(){
        List<Integer> results = controller.invoke("incrementAction", List.of(1, 2, 3, 4, 5));
        List<Integer> results2 = controller.invoke("incrementAction", List.of(3, 4, 5, 6, 7));
        List<Integer> results3 = controller.invoke("incrementAction", List.of(5, 6, 7, 8, 9));
        // La última invocación en este caso se podrá ejecutar (secuencial)
        List<Integer> results4 = controller.invoke("incrementAction", List.of(5, 6, 7, 8, 9));

        assertEquals(List.of(2, 3, 4, 5, 6), results);
        assertEquals(List.of(4, 5, 6, 7, 8), results2);
        assertEquals(List.of(6, 7, 8, 9, 10), results3);
        assertEquals(List.of(6, 7, 8, 9, 10), results4);
    }

    @Test
    public void testInvokeListMapIntegers() {
        @SuppressWarnings("unchecked")
        List<Map<String, Integer>> input = Arrays.asList(new Map[] {
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8),
        });

        List<Integer> result = controller.invoke("addAction", input);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(input.get(i).get("x") + input.get(i).get("y"), result.get(i));
        }
    }

    @Test
    public void testInvokeListMapStrings() {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> input = Arrays.asList(new Map[] {
                Map.of("x", "Miguel ", "y", "Robledo"),
                Map.of("x", "Miguel ", "y", "Robledo"),
                Map.of("x", "Miguel ", "y", "Robledo"),
        });

        List<String> result = controller.invoke("concatAction", input);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(input.get(i).get("x") + input.get(i).get("y"), result.get(i));
        }
    }

    @Test
    public void testInvokeListIntegers() {
        List<Integer> input = Arrays.asList(1,2,3,4,5,6);
        List<Integer> result = controller.invoke("incrementAction", input);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(input.get(i) + 1, result.get(i));
        }
    }

    @Test
    public void testInvokeListStrings() {
        List<String> input = Arrays.asList("Miguel", "Robledo", "Miguel", "Robledo", "Miguel", "Robledo");
        List<String> result = controller.invoke("toUpperCaseAction", input);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(input.get(i).toUpperCase(), result.get(i));
        }
    }

    private int factorial(int x) {
        int result = 1;
        for (int i = 1; i <= x; i++) {
            result *= i;
        }
        return result;
    }

    @Test
    public void testInvokeListIntegersFactorial() {
        List<Integer> input = Arrays.asList(1,2,3,4,5,6);
        List<Integer> result = controller.invoke("factorialAction", input);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(factorial(input.get(i)), result.get(i));
        }
    }

}
