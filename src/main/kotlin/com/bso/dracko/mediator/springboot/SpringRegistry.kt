package com.bso.dracko.mediator.springboot

import com.bso.dracko.mediator.contract.Command
import com.bso.dracko.mediator.contract.CommandHandler
import com.bso.dracko.mediator.contract.Event
import com.bso.dracko.mediator.contract.EventHandler
import com.bso.dracko.mediator.contract.Registry
import com.bso.dracko.mediator.contract.Request
import com.bso.dracko.mediator.contract.RequestHandler
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver
import java.util.stream.Collectors
import java.util.stream.Stream

class SpringRegistry(
    private val ctx: ApplicationContext
) : Registry {
    private val log = LoggerFactory.getLogger(SpringRegistry::class.java)
    private val commandHandlers: MutableMap<Class<*>, CommandHandler<out Command>> = HashMap()
    private val requestHandlers: MutableMap<Class<*>, RequestHandler<out Request<*>, *>> = HashMap()
    private val eventHandlers: MutableMap<Class<*>, MutableList<EventHandler<out Event>>> = HashMap()
    private var initialized = false

    init {
        initialize()
    }

    @Synchronized
    private fun initialize() {
        if (initialized) {
            log.warn("SpringRegistry was already initialized")
            return
        }
        log.info("Initializing springRegistry")

        var names = ctx.getBeanNamesForType(RequestHandler::class.java)
        log.info("Found {} request handlers", names.size)
        Stream.of(*names).forEach { beanName -> registerRequestHandler(beanName) }

        names = ctx.getBeanNamesForType(CommandHandler::class.java)
        log.info("Found {} command handlers", names.size)
        Stream.of(*names).forEach { beanName -> registerCommandHandler(beanName) }

        names = ctx.getBeanNamesForType(EventHandler::class.java)
        log.info("Found {} event handlers", names.size)
        Stream.of(*names).forEach { beanName -> registerEventHandler(beanName) }

        initialized = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Command> getCommandHandler(command: T): CommandHandler<T> {
        return commandHandlers[command.javaClass] as CommandHandler<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : Event> getEventHandler(event: E): List<EventHandler<E>> {
        val handlers = eventHandlers[event.javaClass] ?: return emptyList()
        return handlers.stream().map { h: EventHandler<out Event> -> h as EventHandler<E> }
            .collect(Collectors.toList())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Q : Request<R>, R> getRequestHandler(request: Q): RequestHandler<Q, R>? {
        return requestHandlers[request.javaClass] as RequestHandler<Q, R>?
    }

    private fun registerRequestHandler(beanName: String) {
        val handler = ctx.getBean(beanName) as RequestHandler<*, *>
        log.debug("Registering request handler '{}'", getName(handler))
        requestHandlers.putIfAbsent(handler.getType(), handler)
    }

    private fun registerCommandHandler(beanName: String) {
        val handler = ctx.getBean(beanName) as CommandHandler<*>
        log.debug("Registering command handler '{}'", getName(handler))
        commandHandlers.putIfAbsent(handler.getType(), handler)
    }

    private fun registerEventHandler(beanName: String) {
        val handler = ctx.getBean(beanName) as EventHandler<*>
        val type: Class<*> = handler.getType()
        log.debug("Registering event handler '{}'", getName(handler))
        var handlers = eventHandlers[type]
        if (handlers == null) {
            handlers = mutableListOf()
            eventHandlers[type] = handlers
        }
        handlers.add(handler)
    }

    private fun getName(handler: Any): String {
        val clazz = handler.javaClass
        return if (clazz.simpleName == "") clazz.name else clazz.simpleName
    }
}
