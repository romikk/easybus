package at.nextdoor.easybus;

import net.openhft.compiler.CachedCompiler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isPrivate;
import static java.util.stream.Collectors.toList;

/**
 * Created by romikk on 06/03/15.
 */
public class InvocationResolverService {
    private static final Map<Class<? extends Annotation>,InvocationResolverService> instances = Collections.synchronizedMap(new HashMap<>());
    private static final String invocationClassName = Invocation.class.getCanonicalName();
    private static final String resolverClassName = Resolver.class.getCanonicalName();
    private final Class<? extends Annotation> annotationClass;
    private final Map<Class<?>,Resolver> resolvers = new ConcurrentHashMap<>();
    private final String resolverClassPrefix;
    private final AtomicInteger resolverCounter = new AtomicInteger();
    private final CachedCompiler compiler = new CachedCompiler(null,null);

    private InvocationResolverService(final Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
        this.resolverClassPrefix = "Resolver" + Integer.toHexString(System.identityHashCode(annotationClass));
    }

    public static InvocationResolverService instance(final Class<? extends Annotation> annotationClass) {
        return instances.computeIfAbsent(annotationClass, InvocationResolverService::new);
    }

    public static interface Resolver {
        default <E> Invocation<E> create(Object l, ThrowableConsumer<E> c, Class<E> p){
            return new Invocation<>(l,c,p);
        }

        Stream<Invocation<?>> resolve(Object handler);
    }

    public Stream<Invocation<?>> resolveInvocations(Object handler) {
        Class<?> handlerClass = handler.getClass();
        Resolver resolver = resolvers.computeIfAbsent(handlerClass, this::createResolver);
        return resolver.resolve(handler);
    }

    private Resolver createResolver(final Class handlerClass) {
        String handlerPackage = handlerClass.getPackage().getName();
        String generatedClassName = resolverClassPrefix + resolverCounter.incrementAndGet();
        final StringBuilder sb = new StringBuilder();
        sb.append("package ").append(handlerPackage);
        sb.append("; import java.util.stream.Stream; import ").append(invocationClassName);
        sb.append("; public final class ").append(generatedClassName).append(" implements ").append(resolverClassName);
        sb.append("{ public Stream<Invocation<?>> resolve(Object handler){ return Stream.of( ");
        handlerMethods(handlerClass).forEach(method -> {
            String methodDeclaringClassName = method.getDeclaringClass().getCanonicalName();
            String parameterClassName = method.getParameterTypes()[0].getCanonicalName();
            sb.append("\ncreate(handler,(e)->((").append(methodDeclaringClassName).append(")handler).")
                    .append(method.getName()).append("((").append(parameterClassName).append(")e),")
                    .append(parameterClassName).append(".class),");
        });
        // clear last comma
        sb.setCharAt(sb.length()-1,' ');
        sb.append(");}}");

        try {
            Class generatedClass = compiler.loadFromJava(handlerClass.getClassLoader(), generatedClassName, sb.toString());
            return (Resolver) generatedClass.newInstance();
        } catch (Exception e) {
            throw new CompilationFailedException("Failed to compile generated resolver class for " + handlerClass, e);
        }
    }

    private Collection<Method> handlerMethods(Class<?> handlerClass) {
        if(isPrivate(handlerClass.getModifiers())) {
//            Arrays.stream(handlerClass.getDeclaredMethods())
//                  .filter(m -> m.getAnnotation(annotationClass) != null)
//            Arrays.stream(handlerClass.getInterfaces()).
//            handlerClass.getSuperclass()
            return null;
        } else {
            return Arrays.stream(handlerClass.getDeclaredMethods())
                    .filter(m -> m.getAnnotation(annotationClass) != null)
                    .peek(m -> {
                        if (isPrivate(m.getModifiers())) {
                            throw new InvalidHandlerException("Handler method cannot be private: " + m);
                        }
                        if (m.getParameterCount() != 1) {
                            throw new InvalidHandlerException("Handler method must have one and only one parameter: " + m);
                        }
                    }).collect(toList());
        }
    }


}
