package main.FaaS;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import main.Observer.Metric;
import main.Observer.Observer;


/**
 * The Invoker class represents an invoker in the Function-as-a-Service (FaaS) environment.
 */
public class Invoker {
    private int availableMemory; // Attribute for available memory.
    private final Lock lock = new ReentrantLock();
    private Observer observer; // Reference to the Controller or another observer.
    private int id;

    /**
     * Constructs an Invoker with the specified memory size and ID.
     *
     * @param memorySize the memory size of the invoker
     * @param id         the ID of the invoker
     */
    public Invoker(int memorySize, int id) {
        this.availableMemory = memorySize; // Attribute for available memory.
        this.id = id;
    }

    /**
     * Executes the given action with the provided arguments.
     *
     * @param action the action to execute
     * @param args   the arguments for the action
     * @param <T>    the type of the arguments
     * @param <R>    the type of the result
     * @return the result of executing the action
     */
    public <T, R> R execute(Action action, T args) {
        long startTime = System.nanoTime(); // Starts the timer

        lock.lock();
        try {
            if (action.getMemorySize() > availableMemory) {
                throw new RuntimeException("Invoker does not have enough memory to execute the action");
            }
            availableMemory -= action.getMemorySize(); // Reduces the available memory for the action.
        } finally {
            lock.unlock();
        }

        R result;
        @SuppressWarnings("unchecked")
        Function<T, R> function = (Function<T, R>) action.getFunction();

        try {
            result = function.apply(args); // Executes the action.
        } finally {
            lock.lock();
            try {
                availableMemory += action.getMemorySize(); // Restores the available memory after the action.
            } finally {
                lock.unlock();
                long endTime = System.nanoTime(); // Stops the timer

                if (observer != null) {
                    Metric metric = new Metric(action.getActionName(), endTime - startTime, this.id, action.getMemorySize());
                    observer.update(metric); // Notifies the observer
                }
            }
        }
        return result;
    }

    /**
     * Sets the available memory.
     *
     * @param availableMemory the available memory to set
     */
    public void setAvailableMemory(int availableMemory) {
        this.availableMemory = availableMemory; // Setter for available memory.
    }

    /**
     * Gets the available memory.
     *
     * @return the available memory
     */
    public int getAvailableMemory() {
        return availableMemory; // Getter for available memory.
    }

    /**
     * Sets the observer.
     *
     * @param observer the observer to set
     */
    public void setObserver(Observer observer) {
        this.observer = observer; // Setter for the observer.
    }

    /**
     * Gets the ID.
     *
     * @return the ID
     */
    public int getId() {
        return id; // Getter for the ID.
    }

    /**
     * Sets the ID.
     *
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id; // Setter for the ID.
    }
}
