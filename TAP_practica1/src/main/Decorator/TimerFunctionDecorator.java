/**
 * This class represents a decorator for a function that measures the execution time of the wrapped function.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
package main.Decorator;

import java.util.function.Function;

public class TimerFunctionDecorator<T, R> implements Function<T, R> {
    private final Function<T, R> function;

    /**
     * Constructs a TimerFunctionDecorator with the specified function.
     *
     * @param function the function to be decorated
     */
    public TimerFunctionDecorator(Function<T, R> function) {
        this.function = function;
    }

    /**
     * Applies this function to the given argument and measures the execution time.
     *
     * @param args the function argument
     * @return the result of applying the function
     */
    @Override
    public R apply(T args) {
        long startTime = System.nanoTime();
        R result = function.apply(args);
        long endTime = System.nanoTime();

        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ns");
        return result;
    }
}
