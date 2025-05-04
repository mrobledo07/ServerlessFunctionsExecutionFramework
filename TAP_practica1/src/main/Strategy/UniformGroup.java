package main.Strategy;

import java.util.List;
import main.FaaS.Action;
import main.FaaS.Invoker;

/**
 * The UniformGroup class represents a resource policy for selecting invokers in a uniform group manner.
 */
public class UniformGroup implements ResourcePolicy {
    private final int groupSize;
    private int currentIndex = 0;
    private int actionsInvoker = 0;
    private Invoker currentInvoker = null;

    /**
     * Constructs a UniformGroup with the specified group size.
     *
     * @param groupSize the size of the uniform group
     * @throws RuntimeException if the group size is invalid (less than or equal to zero)
     */
    public UniformGroup(int groupSize) {
        if (groupSize <= 0) throw new RuntimeException("Invalid group size for Uniform Group");
        this.groupSize = groupSize;
    }

    /**
     * Selects an invoker from the provided list of invokers to execute the given action in a uniform group manner.
     *
     * @param invokers the list of available invokers
     * @param action   the action to be executed
     * @return the selected invoker
     * @throws RuntimeException if no invoker has enough memory to execute the action
     */
    @Override
    public Invoker selectInvoker(List<Invoker> invokers, Action action) {
        int i = 0;
        for (Invoker invoker : invokers) {
            if (invoker.getAvailableMemory() < action.getMemorySize() * groupSize) i++;
        }

        if (i >= invokers.size()) throw new RuntimeException("No invoker has enough memory to execute the group");

        int startIdx = currentIndex;
        do {
            if (currentInvoker == null && invokers.get(currentIndex).getAvailableMemory() >= action.getMemorySize()) {
                currentInvoker = invokers.get(currentIndex);
                actionsInvoker++;
                System.out.println("Invoker selected " + currentIndex);
                return currentInvoker;
            } else if (currentInvoker == invokers.get(currentIndex)) {
                if (actionsInvoker < groupSize) {
                    actionsInvoker++;
                    System.out.println("Invoker selected " + currentIndex);
                    return currentInvoker;
                }
            }

            currentInvoker = null;
            actionsInvoker = 0;
            currentIndex = (currentIndex + 1) % invokers.size();

        } while (currentIndex != startIdx);

        throw new RuntimeException("No invoker has enough memory to execute the action");
    }

    /**
     * Resets the state of the resource policy.
     */
    @Override
    public void reset() {
        currentIndex = 0;
        actionsInvoker = 0;
        currentInvoker = null;
    }
}
