package Strategy;

import java.util.List;

import FaaS.Action;
import FaaS.Invoker;

/**
 * The GreedyGroup class represents a resource policy for selecting invokers to execute actions in a greedy manner.
 */
public class GreedyGroup implements ResourcePolicy {

    private Invoker currentInvoker = null;
    private int actionsInvoker = 0;
    private int currentIndex = 0;

    /**
     * Selects an invoker from the provided list of invokers to execute the given action in a greedy manner.
     *
     * @param invokers the list of invokers
     * @param action   the action to be executed
     * @return the selected invoker
     * @throws RuntimeException if no invoker has enough memory to execute the action
     */
    @Override
    public Invoker selectInvoker(List<Invoker> invokers, Action action) {
        
        int startIdx = currentIndex;
        do {
            if (currentInvoker == null && invokers.get(currentIndex).getAvailableMemory() >= action.getMemorySize()){
                currentInvoker = invokers.get(currentIndex);
                actionsInvoker++;
                System.out.println("Invoker selected " + currentIndex);
                return currentInvoker;
            }
            else if (currentInvoker == invokers.get(currentIndex)){
                if (actionsInvoker < currentInvoker.getAvailableMemory() / action.getMemorySize()){
                    actionsInvoker++;
                    System.out.println("Invoker selected " + currentIndex);
                    return currentInvoker;
                }
            }

            currentInvoker = null;
            actionsInvoker = 0;
            currentIndex = (currentIndex + 1) % invokers.size();

        } while(currentIndex != startIdx);

        throw new RuntimeException("No invoker has enough memory to execute the action");
    }

    /**
     * Resets the state of the resource policy.
     */
    @Override
    public void reset(){
        currentInvoker = null;
        actionsInvoker = 0;
        currentIndex = 0;
    }
}
