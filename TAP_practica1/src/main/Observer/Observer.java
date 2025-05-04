package main.Observer;

/**
 * The Observer interface represents an observer that receives updates from subjects.
 */
public interface Observer {
    /**
     * Updates the observer with a metric.
     *
     * @param metric the metric to update the observer with
     */
    void update(Metric metric);
}
