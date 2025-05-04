package Reflection;

/**
 * The ActionProxyImpl class provides implementations of methods defined in the IActionProxy interface.
 */
public class ActionProxyImpl implements IActionProxy {
    

    /**
     * Calculates the factorial of the given integer.
     *
     * @param a the integer to calculate the factorial of
     * @return the factorial of the given integer
     */
    @Override
    public Integer calculateFactorial(Integer a) {
        int factorial = 1;
        for (int i = 1; i <= a; i++) {
            factorial *= i;
        }

        return factorial;
    }

    /**
     * Sums two integers.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the sum of the two integers
     */
    @Override
    public Integer sumNumbers(Integer a, Integer b) {
        return a+b;   
    }
}
