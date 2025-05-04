package Reflection;

import FaaS.Controller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * The DynamicProxy class implements the InvocationHandler interface to provide dynamic proxy behavior.
 */
public class DynamicProxy implements InvocationHandler {
    private final Controller controller;
    private final Object target;

    /**
     * Constructs a DynamicProxy object with the specified controller and target.
     *
     * @param controller the controller used for managing actions
     * @param target     the target object to proxy
     */
    public DynamicProxy(Controller controller, Object target) {
        this.controller = controller;
        this.target = target;
    }

    /**
     * Invokes the specified method on the target object.
     *
     * @param proxy  the proxy object
     * @param method the method to invoke
     * @param args   the arguments to pass to the method
     * @return the result of the method invocation
     * @throws Throwable if an error occurs during method invocation
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Function<Object[], Object> function = x -> {
            try {
                return method.invoke(target, x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        if (!controller.isActionRegistered(method.getName())) 
            controller.registerAction(method.getName(), function, 32);
        
        return controller.invoke(method.getName(), args);
    }
}
