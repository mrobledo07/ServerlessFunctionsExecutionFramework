package main.Decorator;

import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;

/**
 * The MemoizationDecorator class provides a way to memoize the results of a function.
 * It caches the results of the function calls, allowing for faster subsequent calls with the same arguments.
 *
 * @param <T> The type of the input to the function.
 * @param <R> The type of the result of the function.
 */
public class MemoizationDecorator<T, R> implements Function<T, R> {
    private final Function<T, R> function;
    private final Map<T, R> cache;

    /**
     * Constructs a new MemoizationDecorator with the specified function.
     *
     * @param function The function to be memoized.
     */
    public MemoizationDecorator(Function<T, R> function) {
        this.function = function;
        this.cache = new HashMap<>();
    }

    
    /**
     * Applies this function to the given arguments, with memoization.
     *
     * @param args The function arguments.
     * @return The function result, either retrieved from cache or computed.
     */
    @Override
    public R apply(T args) {
        if (cache.containsKey(args)) {
            System.out.println("Cache hit!");
            return cache.get(args);
        }

        R result = function.apply(args);
        cache.put(args, result);
        return result;
    }
}

