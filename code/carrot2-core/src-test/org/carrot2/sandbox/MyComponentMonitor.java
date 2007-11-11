package org.carrot2.sandbox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * 
 */
public class MyComponentMonitor implements ComponentMonitor
{
    @Override
    public void instantiated(PicoContainer arg0, ComponentAdapter arg1, Constructor arg2,
        Object arg3, Object [] arg4, long arg5)
    {
    }

    @Override
    public Constructor instantiating(PicoContainer container, ComponentAdapter arg1,
        Constructor constructor)
    {
        Class clazz = constructor.getDeclaringClass();
        ((DefaultPicoContainer) container).addComponent(ExampleTokenizer.class);
        
        return constructor;
    }

    @Override
    public void instantiationFailed(PicoContainer arg0, ComponentAdapter arg1,
        Constructor arg2, Exception arg3)
    {
    }

    @Override
    public void invocationFailed(Member arg0, Object arg1, Exception arg2)
    {
    }

    @Override
    public void invoked(PicoContainer arg0, ComponentAdapter arg1, Method arg2,
        Object arg3, long arg4)
    {
    }

    @Override
    public void invoking(PicoContainer arg0, ComponentAdapter arg1, Member arg2,
        Object arg3)
    {
    }

    @Override
    public void lifecycleInvocationFailed(MutablePicoContainer arg0,
        ComponentAdapter arg1, Method arg2, Object arg3, RuntimeException arg4)
    {
    }

    @Override
    public Object noComponentFound(MutablePicoContainer arg0, Object arg1)
    {
        return null;
    }
}
