package main.Strategy;

import java.util.List;

import main.FaaS.Action;
import main.FaaS.Invoker;

/**
 * The RoundRobin class represents a resource policy for selecting invokers in a round-robin fashion.
 */
public class RoundRobin implements ResourcePolicy {
    private int currentIndex = 0;

    /**
     * Selects an invoker from the provided list of invokers to execute the given action in a round-robin fashion.
     *
     * @param invokers the list of available invokers
     * @param action   the action to be executed
     * @return the selected invoker
     * @throws RuntimeException if no invoker has enough memory to execute the action
     */
    @Override
    public Invoker selectInvoker(List<Invoker> invokers, Action action) {

        for (int i = currentIndex; i < invokers.size(); i++) {
            if (invokers.get(i).getAvailableMemory() >= action.getMemorySize()) {
                System.out.println("Invoker selected: " + i);
                currentIndex = (i + 1) % invokers.size();
                return invokers.get(i);
            }
        }

        throw new RuntimeException("No invoker has enough memory to execute the action");
    }

    /**
     * Resets the state of the resource policy.
     */
    @Override
    public void reset() {
        currentIndex = 0;
    }
}
