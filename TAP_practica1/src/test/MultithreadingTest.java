package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.FaaS.Controller;

import java.util.function.Function;
import java.util.*;
import java.util.concurrent.Future;


public class MultithreadingTest {

    Controller controller;

    @BeforeEach
    public void setUp() {
        // Creación del controlador
        controller = new Controller(4, 1024);
       
        // Creación de funciones
        Function<Integer, String> sleep = s -> {
            try {
                Thread.sleep(s);
                return "Done!";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Function<Map<String, Integer>, Integer> addAction = x -> x.get("x") + x.get("y");
        Function<Integer, Integer> increment = x -> x + 1;


        // Registro de acciones
        controller.registerAction("addAction", addAction, 256);
        controller.registerAction("sleepAction", sleep, 256);
        controller.registerAction("incrementAction", increment, 256);

    }


   // En este Test comprobamos que el tiempo de ejecución es mayor o igual a 15 ms
    @Test
    public void testInvokeSeq(){
        // Comprobar que el tiempo de ejecución es 15 y no 5 como sería de manera concurrente
        long start = System.currentTimeMillis();
        controller.invoke("sleepAction", 5);
        controller.invoke("sleepAction", 5);
        controller.invoke("sleepAction", 5);
        long end = System.currentTimeMillis();
        assertTrue(end - start >= 15);
    }


    // En este Test comprobamos que el tiempo de ejecución es menor a 15 ms (concurrencia)
    @Test
    // Test invoke_async
    public void testInvokeAsync() {       
        // Comprobar que el tiempo de ejecución es 5 y no 15 como sería de manera secuencial
        long start = System.currentTimeMillis();
        Future<Integer> fut1 = controller.invoke_async("sleepAction", 5);
        Future<Integer> fut2 = controller.invoke_async("sleepAction", 5);
        Future<Integer> fut3 = controller.invoke_async("sleepAction", 5);
        
        try {
            fut1.get();
            fut2.get();
            fut3.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();
        assertTrue(end - start < 15);   // Comprobamos que tarda menos de 15 ms (concurrencia)
    }


    // Comprobamos que la suma se realiza correctamente (concurrencia)
    @Test
    // Test invoke_async
    public void testInvokeAsyncSum() {
        // Comprobar que la suma se realiza correctamente
        Future<Integer> fut1 = controller.invoke_async("addAction", Map.of("x", 5, "y", 5));
        Future<Integer> fut2 = controller.invoke_async("addAction", Map.of("x", 8, "y", 9));
        Future<Integer> fut3 = controller.invoke_async("addAction", Map.of("x", 15, "y", 12));

        try {
            assertEquals(10, fut1.get());
            assertEquals(17, fut2.get());      
            assertEquals(27, fut3.get());  
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


        @Test
        public void testInvokeAsyncGroup() {
            Future<List<Integer>> fut = controller.invoke_async("incrementAction", List.of(1, 2, 3, 4, 5));
            Future<List<Integer>> fut2 = controller.invoke_async("incrementAction", List.of(3, 4, 5, 6, 7));
            Future<List<Integer>> fut3 = controller.invoke_async("incrementAction", List.of(5, 6, 7, 8, 9));
            Future<List<Integer>> fut4 = null;

            // La última llamada grupal no se podrán ejecutar
            // Ya que las tres anteriores invocaciones grupales ocupan 256*15 = 3840 bytes
            // De manera que quedaran 4096 - 3840 = 256 bytes
            try{
                fut4 = controller.invoke_async("incrementAction", List.of(7, 8, 9, 10, 11));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                assertEquals(e.getMessage(), "Not enough memory to execute group action: incrementAction");
            }
            


            try {
                assertEquals(List.of(2, 3, 4, 5, 6), fut.get());
                assertEquals(List.of(4, 5, 6, 7, 8), fut2.get());
                assertEquals(List.of(6, 7, 8, 9, 10), fut3.get());
                // Existe una pequeña posibilidad de que la última llamada grupal se ejecute
                // Si cuando se ejecute su llamada, se han ejecutado suficientes acciones de las anteriores invocaciones
                // Como para haber liberado la memoria suficiente (1024 bytes = 4 acciones)
                if (fut4 != null)
                    assertEquals(List.of(8, 9, 10, 11, 12), fut4.get());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            
            

        }
        

}
