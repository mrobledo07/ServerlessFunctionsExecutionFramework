package main.Strategy;

import java.util.List;

import main.FaaS.Action;
import main.FaaS.Invoker;

/**
 * The ResourcePolicy interface represents a policy for selecting invokers to execute actions.
 */
public interface ResourcePolicy {
    /**
     * Selects an invoker from the provided list of invokers to execute the given action.
     *
     * @param invokers the list of invokers
     * @param action   the action to be executed
     * @return the selected invoker
     */
    Invoker selectInvoker(List<Invoker> invokers, Action action);

    /**
     * Resets the state of the resource policy.
     */
    void reset();
}
