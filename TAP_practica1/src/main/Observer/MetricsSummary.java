package main.Observer;

import java.util.Map;

/**
 * The MetricsSummary class represents a summary of metrics collected during the
 * execution of actions.
 */
public class MetricsSummary {
    private Map<String, Long> maxExecutionTimePerAction;
    private Map<String, Long> minExecutionTimePerAction;
    private Map<String, Double> averageExecutionTimePerAction;
    private Map<String, Long> totalExecutionTimePerAction;
    private Map<Integer, Integer> memoryUsagePerInvoker;

    /**
     * Gets the maximum execution time per action.
     *
     * @return a map containing the maximum execution time per action
     */
    public Map<String, Long> getMaxExecutionTimePerAction() {
        return maxExecutionTimePerAction;
    }

    /**
     * Sets the maximum execution time per action.
     *
     * @param maxExecutionTimePerAction a map containing the maximum execution time
     *                                  per action
     */
    public void setMaxExecutionTimePerAction(Map<String, Long> maxExecutionTimePerAction) {
        this.maxExecutionTimePerAction = maxExecutionTimePerAction;
    }

    /**
     * Gets the minimum execution time per action.
     *
     * @return a map containing the minimum execution time per action
     */
    public Map<String, Long> getMinExecutionTimePerAction() {
        return minExecutionTimePerAction;
    }

    /**
     * Sets the minimum execution time per action.
     *
     * @param minExecutionTimePerAction a map containing the minimum execution time
     *                                  per action
     */
    public void setMinExecutionTimePerAction(Map<String, Long> minExecutionTimePerAction) {
        this.minExecutionTimePerAction = minExecutionTimePerAction;
    }

    /**
     * Gets the average execution time per action.
     *
     * @return a map containing the average execution time per action
     */
    public Map<String, Double> getAverageExecutionTimePerAction() {
        return averageExecutionTimePerAction;
    }

    /**
     * Sets the average execution time per action.
     *
     * @param averageExecutionTimePerAction a map containing the average execution
     *                                      time per action
     */
    public void setAverageExecutionTimePerAction(Map<String, Double> averageExecutionTimePerAction) {
        this.averageExecutionTimePerAction = averageExecutionTimePerAction;
    }

    /**
     * Gets the total execution time per action.
     *
     * @return a map containing the total execution time per action
     */
    public Map<String, Long> getTotalExecutionTimePerAction() {
        return totalExecutionTimePerAction;
    }

    /**
     * Sets the total execution time per action.
     *
     * @param totalExecutionTimePerAction a map containing the total execution time
     *                                    per action
     */
    public void setTotalExecutionTimePerAction(Map<String, Long> totalExecutionTimePerAction) {
        this.totalExecutionTimePerAction = totalExecutionTimePerAction;
    }

    /**
     * Gets the memory usage per invoker.
     *
     * @return a map containing the memory usage per invoker
     */
    public Map<Integer, Integer> getMemoryUsagePerInvoker() {
        return memoryUsagePerInvoker;
    }

    /**
     * Sets the memory usage per invoker.
     *
     * @param memoryUsagePerInvoker a map containing the memory usage per invoker
     */
    public void setMemoryUsagePerInvoker(Map<Integer, Integer> memoryUsagePerInvoker) {
        this.memoryUsagePerInvoker = memoryUsagePerInvoker;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Metrics Summary:\n");

        sb.append("Max Execution Time per Action:\n");
        maxExecutionTimePerAction
                .forEach((action, time) -> sb.append(" - ").append(action).append(": ").append(time).append(" ns\n"));

        sb.append("Min Execution Time per Action:\n");
        minExecutionTimePerAction
                .forEach((action, time) -> sb.append(" - ").append(action).append(": ").append(time).append(" ns\n"));

        sb.append("Average Execution Time per Action:\n");
        averageExecutionTimePerAction.forEach((action, time) -> sb.append(" - ").append(action).append(": ")
                .append(String.format("%.2f ns\n", time)));

        sb.append("Total Execution Time per Action:\n");
        totalExecutionTimePerAction
                .forEach((action, time) -> sb.append(" - ").append(action).append(": ").append(time).append(" ns\n"));

        sb.append("Memory Usage per Invoker:\n");
        memoryUsagePerInvoker.forEach((invokerId, memory) -> sb.append(" - Invoker ").append(invokerId).append(": ")
                .append(memory).append(" bytes\n"));

        return sb.toString();
    }

}
