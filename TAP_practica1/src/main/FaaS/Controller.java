package main.FaaS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.lang.reflect.Proxy;

import main.Observer.Metric;
import main.Observer.MetricsSummary;
import main.Observer.Observer;
import main.Reflection.ActionProxyImpl;
import main.Reflection.DynamicProxy;
import main.Reflection.IActionProxy;
import main.Strategy.PolicyManager;
import main.Strategy.ResourcePolicy;
import main.Strategy.RoundRobin;

/**
 * The Controller class manages the invocation of actions in a Function-as-a-Service (FaaS) environment.
 * It implements the Observer interface to receive metrics updates.
 */
public class Controller implements Observer{
    private List<Invoker> invokers;
    private Map<String, Action> actions;
    private int availableMemory;
    private PolicyManager policyManager;
    private ExecutorService executorService;
    private List<Metric> metrics;

    /**
     * Constructs a Controller object with the specified number of invokers and memory per invoker.
     *
     * @param invokerCount     the number of invokers
     * @param memoryPerInvoker the memory per invoker
     */
    public Controller(int invokerCount, int memoryPerInvoker) {
        if (invokerCount <= 0 || memoryPerInvoker <= 0) {
            throw new RuntimeException("Invalid invokerCount or memoryPerInvoker");
        }
        actions = new HashMap<>();
        invokers = new ArrayList<>();
        availableMemory = invokerCount * memoryPerInvoker;
        policyManager = new PolicyManager(new RoundRobin()); // Round Robin by default
        executorService = Executors.newFixedThreadPool(invokerCount * 32);  // Initializes the ExecutorService with an appropriate number of threads
        metrics = new ArrayList<>();
        for (int i = 0; i < invokerCount; i++) {
            Invoker invoker = new Invoker(memoryPerInvoker, i);
            invoker.setObserver(this);
            invokers.add(invoker);
        }
    }

    /**
     * Registers a new action with the given name, function, and memory size.
     *
     * @param actionName the name of the action
     * @param function   the function to be executed
     * @param memorySize the memory size allocated for the action
     * @param <T>        the type of the input to the function
     * @param <R>        the type of the result of the function
     */
    public <T, R> void registerAction(String actionName, Function<T, R> function, int memorySize) {
        if (isActionRegistered(actionName)) return;
        Action action = new Action(function, memorySize, actionName);
        actions.put(actionName, action);
    }

    /**
     * Invokes the action with the given name and argument.
     *
     * @param actionName the name of the action
     * @param args       the argument for the action
     * @param <T>        the type of the argument
     * @param <R>        the type of the result
     * @return the result of invoking the action
     */
    public <T, R> R invoke(String actionName, T args) {
        Action action = actions.get(actionName);
        if (action == null) {
            throw new RuntimeException("Action not registered: " + actionName);
        }

        if (action.getMemorySize() > availableMemory) {
            throw new RuntimeException("Not enough memory to execute action: " + actionName);
        }

        availableMemory -= action.getMemorySize(); // Reduces the available memory for the action.

        Invoker invoker = policyManager.selectInvoker(invokers, action);
        R result = invoker.execute(action, args);

        availableMemory += action.getMemorySize(); // Restores the available memory after the action.

        return result;
    }

    /**
     * Invokes the action with the given name and list of parameters.
     *
     * @param actionName    the name of the action
     * @param listOfParams  the list of parameters for the action
     * @param <T>           the type of the parameters
     * @param <R>           the type of the results
     * @return a list of results of invoking the action with each parameter
     */
    public <T, R> List<R> invoke(String actionName, List<T> listOfParams) {
        Action action = actions.get(actionName);
        if (action == null) {
            throw new RuntimeException("Action not registered: " + actionName);
        }

        if (availableMemory < action.getMemorySize() * listOfParams.size()) {
            throw new RuntimeException("Not enough memory to execute group action: " + actionName);
        }

        // Reserves memory for this group invocation
        availableMemory -= action.getMemorySize() * listOfParams.size();

        policyManager.getPolicy().reset(); // Resets the invoker selection policy.

        List<R> results = new ArrayList<>();
        for (T params : listOfParams) {
            Invoker invoker = policyManager.selectInvoker(invokers, action);
            results.add(invoker.execute(action, params));
            availableMemory += action.getMemorySize(); // Restores the available memory after the action.
        }
        return results;
    }

    /**
     * Asynchronously invokes the action with the given name and argument.
     *
     * @param actionName the name of the action
     * @param args       the argument for the action
     * @param <T>        the type of the argument
     * @param <R>        the type of the result
     * @return a Future representing the result of the asynchronous invocation
     */
    public <T, R> Future<R> invoke_async(String actionName, T args) {
        return executorService.submit(() -> {
            Action action = actions.get(actionName);
            if (action == null) {
                throw new RuntimeException("Action not registered: " + actionName);
            }
            
            synchronized (this) {
                if (action.getMemorySize() > availableMemory) {
                    throw new RuntimeException("Not enough memory to execute action: " + actionName);
                }
                availableMemory -= action.getMemorySize(); // Reserves memory.
            }
    
            Invoker invoker;
            synchronized (policyManager) {
                try{
                    invoker = policyManager.selectInvoker(invokers, action);
                } catch (Exception e) { // Important in case invoker selection fails, to release memory.
                    synchronized (this) {
                        availableMemory += action.getMemorySize(); // Releases memory.
                    }
                    throw new RuntimeException(e.getMessage());
                }
            }
    
            try {
                return invoker.execute(action, args);       // Executes the action on the selected invoker.
            } finally {
                synchronized (this) {
                    availableMemory += action.getMemorySize(); // Releases memory.
                }
            }
        });
    }

    /**
     * Asynchronously invokes the action with the given name and list of parameters.
     *
     * @param actionName   the name of the action
     * @param listOfParams the list of parameters for the action
     * @param <T>          the type of the parameters
     * @param <R>          the type of the results
     * @return a CompletableFuture representing the list of results of the asynchronous invocations
     */
    public <T, R> CompletableFuture<List<R>> invoke_async(String actionName, List<T> listOfParams) {
        Action action = actions.get(actionName);
        if (action == null) {
            throw new RuntimeException("Action not registered: " + actionName);
        }

        // Checks if there is enough memory for all actions
        synchronized (this) {
            if (action.getMemorySize() * listOfParams.size() > availableMemory) {
                throw new RuntimeException("Not enough memory to execute group action: " + actionName);
            }
        }

        List<CompletableFuture<R>> futureResults = listOfParams.stream()
            .map(params -> CompletableFuture.supplyAsync(() -> {
                try {
                    Future<R> future = invoke_async(actionName, params);
                    return future.get();
                } catch (Exception e) {
                    throw new RuntimeException(e.getCause().getMessage());
                }
            }, executorService))
            .collect(Collectors.toList());

        // Combines all futures into a single CompletableFuture containing a list of results
        return CompletableFuture.allOf(futureResults.toArray(new CompletableFuture[0]))
            .thenApply(v -> futureResults.stream()
                                         .map(CompletableFuture::join)
                                         .collect(Collectors.toList()));
    }

    @Override
    public void update(Metric metric) {
        synchronized (metrics) {
            metrics.add(metric);
        }
    }

    /**
     * Calculates a summary of metrics.
     *
     * @return a summary of metrics
     */
    public MetricsSummary calculateMetricsSummary() {
        MetricsSummary summary = new MetricsSummary();

        Map<String, LongSummaryStatistics> executionStatsPerAction = metrics.stream()
            .collect(Collectors.groupingBy(
                Metric::getActionName,
                Collectors.summarizingLong(Metric::getExecutionTime)
            ));

        summary.setMaxExecutionTimePerAction(
            executionStatsPerAction.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getMax()
                ))
        );

        summary.setMinExecutionTimePerAction(
            executionStatsPerAction.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getMin()
                ))
        );

        summary.setAverageExecutionTimePerAction(
            executionStatsPerAction.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getAverage()
                ))
        );

        summary.setTotalExecutionTimePerAction(
            executionStatsPerAction.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getSum()
                ))
        );

        summary.setMemoryUsagePerInvoker(
            metrics.stream()
                .collect(Collectors.groupingBy(
                    Metric::getInvokerId,
                    Collectors.summingInt(Metric::getMemoryUsed)
                ))
        );

        return summary;
    }

    /**
     * Creates a dynamic proxy for actions.
     *
     * @return a dynamic proxy for actions
     */
    public IActionProxy createProxy() {
        IActionProxy proxy = (IActionProxy) Proxy.newProxyInstance(
            IActionProxy.class.getClassLoader(),
            new Class<?>[]{IActionProxy.class},
            new DynamicProxy(this, new ActionProxyImpl())
        );
        return proxy;
    }

    /**
     * Checks if an action is registered with the given name.
     *
     * @param actionName the name of the action
     * @return true if the action is registered, false otherwise
     */
    public boolean isActionRegistered(String actionName){
        return actions.containsKey(actionName);
    }

    /**
     * Gets the available memory.
     *
     * @return the available memory
     */
    public int getAvailableMemory() {
        return availableMemory;
    }

    /**
     * Sets the available memory.
     *
     * @param availableMemory the available memory to set
     */
    public void setAvailableMemory(int availableMemory) {
        this.availableMemory = availableMemory;
    }

    /**
     * Sets the policy manager.
     *
     * @param policyManager the policy manager to set
     */
    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    /**
     * Sets the policy.
     *
     * @param resourcePolicy the resource policy to set
     */
    public void setPolicy(ResourcePolicy resourcePolicy) {
        policyManager.setPolicy(resourcePolicy);
    }

    /**
     * Shuts down the controller.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
