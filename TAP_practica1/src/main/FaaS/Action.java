package main.FaaS;

import java.util.function.Function;

/**
 * The Action class represents an action in a Function-as-a-Service (FaaS) architecture.
 * It encapsulates a function, its memory requirement, and its name.
 */
public class Action {
    private Function<?, ?> function;
    private int memorySize;
    private String actionName;

    /**
     * Constructs a new Action with the specified function, memory size, and name.
     *
     * @param function   The function to be executed as part of this action.
     * @param memorySize The memory size required for executing the function.
     * @param actionName The name of the action.
     */
    public Action(Function<?, ?> function, int memorySize, String actionName) {
        this.function = function;
        this.memorySize = memorySize;
        this.actionName = actionName;
    }

    /**
     * Gets the memory size required for this action.
     *
     * @return The memory size in units appropriate for the context (e.g., MB).
     */
    public int getMemorySize() {
        return memorySize;
    }

    /**
     * Gets the name of this action.
     *
     * @return The name of the action.
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * Gets the function associated with this action.
     *
     * @return The function to be executed as part of this action.
     */
    public Function<?, ?> getFunction() {
        return function;
    }
}
