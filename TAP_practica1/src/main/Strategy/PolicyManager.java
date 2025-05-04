package main.Strategy;

import java.util.List;

import main.FaaS.Action;
import main.FaaS.Invoker;

/**
 * The PolicyManager class manages resource policies for selecting invokers.
 */
public class PolicyManager {
    private ResourcePolicy resourcePolicy;

    /**
     * Constructs a PolicyManager with the specified resource policy.
     *
     * @param resourcePolicy the resource policy to be managed
     */
    public PolicyManager(ResourcePolicy resourcePolicy) {
        this.resourcePolicy = resourcePolicy;
    }

    /**
     * Sets the resource policy.
     *
     * @param resourcePolicy the resource policy to be set
     */
    public void setPolicy(ResourcePolicy resourcePolicy) {
        this.resourcePolicy = resourcePolicy;
    }

    /**
     * Gets the current resource policy.
     *
     * @return the current resource policy
     */
    public ResourcePolicy getPolicy() {
        return resourcePolicy;
    }

    /**
     * Selects an invoker based on the current resource policy.
     *
     * @param invokers the list of available invokers
     * @param action   the action to be executed
     * @return the selected invoker
     */
    public Invoker selectInvoker(List<Invoker> invokers, Action action) {
        return resourcePolicy.selectInvoker(invokers, action);
    }
}
