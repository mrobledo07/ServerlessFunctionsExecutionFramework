package main.Observer;

/**
 * The Metric class represents a metric recorded during the execution of an action.
 */
public class Metric {
    private final String actionName;
    private final long executionTime; // in nanoseconds (more precision)
    private final int invokerId;
    private final int memoryUsed;

    /**
     * Constructs a Metric object with the specified parameters.
     *
     * @param actionName    the name of the action
     * @param executionTime the execution time in nanoseconds
     * @param invokerId     the ID of the invoker
     * @param memoryUsed    the memory used during execution
     */
    public Metric(String actionName, long executionTime, int invokerId, int memoryUsed) {
        this.actionName = actionName;
        this.executionTime = executionTime;
        this.invokerId = invokerId;
        this.memoryUsed = memoryUsed;
    }

    /**
     * Gets the action name.
     *
     * @return the action name
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * Gets the execution time.
     *
     * @return the execution time in nanoseconds
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Gets the invoker ID.
     *
     * @return the invoker ID
     */
    public int getInvokerId() {
        return invokerId;
    }

    /**
     * Gets the memory used.
     *
     * @return the memory used
     */
    public int getMemoryUsed() {
        return memoryUsed;
    }
}
