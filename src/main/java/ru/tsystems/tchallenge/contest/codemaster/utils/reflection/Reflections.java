package ru.tsystems.tchallenge.contest.codemaster.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Utility methods to work with object types dynamically.
 *
 * @author Ilia Gubarev
 */
public final class Reflections {

    /**
     * Casts a specified object to a target type.
     *
     * @param object the object to be casted.
     * @param <T> the target type.
     * @return the casted object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object object) {
        return (T) object;
    }

    /**
     * Creates a new instance of specified target type.
     *
     * @param type the target type's class.
     * @param parameters parameters to be passed into a constructor.
     * @param <T> the target type.
     * @return new created instance.
     */
    public static <T> T instantiate(final Class<T> type, final Object... parameters) {
        try {
            final Class<?>[] types = deriveTypes(parameters);
            final Constructor<T> constructor = type.getConstructor(types);
            return constructor.newInstance(parameters);
        } catch (final Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Invokes a specified method.
     *
     * @param methodName method's name.
     * @param target the target object for method invocation.
     * @param parameters parameters to be passed into a constructor.
     * @param <T> the target type.
     * @return method invocation result.
     */
    public static <T> T invoke(final String methodName, final Object target, final Object... parameters) {
        try {
            final Class<?>[] types = deriveTypes(parameters);
            final Method method = target.getClass().getMethod(methodName, types);
            final Object result = method.invoke(target, parameters);
            return cast(result);
        } catch (final Exception e) {
            throw new ReflectionException(e);
        }
    }

    private static Class<?>[] deriveTypes(final Object... parameters) {
        final Class<?>[] result = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Object parameter = parameters[i];
            result[i] = parameter != null ? parameters[i].getClass() : Object.class;
        }
        return result;
    }

    private Reflections() {

    }
}

