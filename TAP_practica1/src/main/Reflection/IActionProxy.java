package main.Reflection;

/**
 * The IActionProxy interface represents an action proxy.
 */
public interface IActionProxy {
    /**
     * Calculates the factorial of the given integer.
     *
     * @param a the integer to calculate the factorial of
     * @return the factorial of the given integer
     */
    Integer calculateFactorial(Integer a);

    /**
     * Sums two integers.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the sum of the two integers
     */
    Integer sumNumbers(Integer a, Integer b);
}
