package com.bso.dracko.mediator.springboot;

import com.bso.dracko.mediator.contract.Command;
import com.bso.dracko.mediator.contract.CommandHandler;
import com.bso.dracko.mediator.contract.Event;
import com.bso.dracko.mediator.contract.EventHandler;
import com.bso.dracko.mediator.contract.Registry;
import com.bso.dracko.mediator.contract.Request;
import com.bso.dracko.mediator.contract.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringRegistry implements Registry {

    private static final Logger log = LoggerFactory.getLogger(SpringRegistry.class);

    private final ApplicationContext ctx;
    private final Map<Class<?>, CommandHandler<? extends Command>> commandHandlers = new HashMap<>();
    private final Map<Class<?>, RequestHandler<? extends Request<?>, ?>> requestHandlers = new HashMap<>();
    private final Map<Class<?>, List<EventHandler<? extends Event>>> eventHandlers = new HashMap<>();
    private boolean initialized = false;

    public SpringRegistry(ApplicationContext ctx) {
        this.ctx = ctx;
        initialize();
    }

    private synchronized void initialize() {
        if (initialized) {
            return;
        }
        log.info("Initializing springRegistry");

        String[] names = ctx.getBeanNamesForType(RequestHandler.class);
        log.info("Found {} request handlers", names.length);
        Stream.of(names).forEach(this::registerRequestHandler);

        names = ctx.getBeanNamesForType(CommandHandler.class);
        log.info("Found {} command handlers", names.length);
        Stream.of(names).forEach(this::registerCommandHandler);

        names = ctx.getBeanNamesForType(EventHandler.class);
        log.info("Found {} event handlers", names.length);
        Stream.of(names).forEach(this::registerEventHandler);

        initialized = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Command> CommandHandler<T> getCommandHandler(T command) {
        return (CommandHandler<T>) commandHandlers.get(command.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Event> List<EventHandler<E>> getEventHandler(E event) {
        var handlers = eventHandlers.get(event.getClass());
        if (handlers == null) {
             return null;
        }

        return handlers.stream().map(h -> (EventHandler<E>) h).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Q extends Request<R>, R> RequestHandler<Q, R> getRequestHandler(Q request) {
        return (RequestHandler<Q,R>) requestHandlers.get(request.getClass());
    }

    private void registerRequestHandler(String beanName) {
        var handler = (RequestHandler<?, ?>)ctx.getBean(beanName);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handler.getClass(), RequestHandler.class);
        if (generics != null) {
            Class<?> requestType = generics[0];
            log.info("Registering request handler '{}'", handler.getClass().getSimpleName());
            requestHandlers.putIfAbsent(requestType, handler);
        }
    }

    private void registerCommandHandler(String beanName) {
        var handler = (CommandHandler<?>)ctx.getBean(beanName);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handler.getClass(), CommandHandler.class);
        if (generics != null) {
            Class<?> requestType = generics[0];
            log.info("Registering command handler '{}'", handler.getClass().getSimpleName());
            commandHandlers.putIfAbsent(requestType, handler);
        }
    }

    private void registerEventHandler(String beanName) {
        var handler = (EventHandler<?>)ctx.getBean(beanName);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handler.getClass(), EventHandler.class);
        if (generics != null) {
            Class<?> eventType = generics[0];
            log.info("Registering event handler '{}'", handler.getClass().getSimpleName());
            List<EventHandler<? extends Event>> handlers = this.eventHandlers.get(eventType);
            if (handlers == null) {
                handlers = new ArrayList<>();
                eventHandlers.put(eventType, handlers);
            }
            handlers.add(handler);
        }
    }
}
