package main.Strategy;

import java.util.List;
import main.FaaS.Action;
import main.FaaS.Invoker;

/**
 * The BigGroup class represents a resource policy for selecting invokers to execute actions in big groups.
 */
public class BigGroup implements ResourcePolicy {
    
    private final int groupSize;
    private int currentIndex = 0;
    private int actionsInvoker = 0;
    private int actionsGroup = 1;
    private Invoker currentInvoker = null;

    /**
     * Constructs a BigGroup object with the specified group size.
     *
     * @param groupSize the size of the group
     */
    public BigGroup(int groupSize){
        if (groupSize <= 0) throw new RuntimeException("Invalid group size for Uniform Group");
        this.groupSize = groupSize;
    }

    /**
     * Selects an invoker from the provided list of invokers to execute the given action.
     *
     * @param invokers the list of invokers
     * @param action   the action to be executed
     * @return the selected invoker
     */
    @Override
    public Invoker selectInvoker(List<Invoker> invokers, Action action) {
        // Verificar si algún Invoker puede soportar el tamaño de grupo
        int i = 0;
        for (Invoker invoker : invokers) {
            if (invoker.getAvailableMemory() < action.getMemorySize() * groupSize) i++;
        }

        if (i >= invokers.size()) throw new RuntimeException("No invoker has enough memory to execute the group");
        
      
        int startIdx = currentIndex;
        do {
            if (currentInvoker == null && invokers.get(currentIndex).getAvailableMemory() >= action.getMemorySize()){
                currentInvoker = invokers.get(currentIndex);
                actionsInvoker++;
                System.out.println("Invoker selected " + currentIndex);
                return currentInvoker;
            }
            else if (currentInvoker == invokers.get(currentIndex)){
                if (actionsInvoker < groupSize){
                    actionsInvoker++;
                    System.out.println("Invoker selected " + currentIndex);
                    return currentInvoker;
                }
                else {
                    actionsGroup++;
                    if (currentInvoker.getAvailableMemory() >= (action.getMemorySize() * groupSize) * actionsGroup){ // si el invoker tiene memoria para otro grupo
                        actionsInvoker = 1;
                        System.out.println("Invoker selected " + currentIndex);
                        return currentInvoker;
                    }
                    else 
                        actionsGroup = 1;
                    
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
    public void reset() {
        currentIndex = 0;
        actionsInvoker = 0;
        currentInvoker = null;
        actionsGroup = 1;
    }
}
