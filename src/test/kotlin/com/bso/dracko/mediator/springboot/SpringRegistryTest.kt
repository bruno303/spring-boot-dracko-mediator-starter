package com.bso.dracko.mediator.springboot

import com.bso.dracko.mediator.contract.Command
import com.bso.dracko.mediator.contract.CommandHandler
import com.bso.dracko.mediator.contract.Event
import com.bso.dracko.mediator.contract.EventHandler
import com.bso.dracko.mediator.contract.Registry
import com.bso.dracko.mediator.contract.Request
import com.bso.dracko.mediator.contract.RequestHandler
import com.bso.dracko.mediator.springboot.config.SpringBootIntegrationTestConfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@SpringBootTest(classes = [SpringBootIntegrationTestConfiguration::class], webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(SpringRegistryTest.SpringRegistryTestConfig::class)
class SpringRegistryTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var registry: Registry

    class DummyCommand : Command
    class DummyRequest : Request<String>
    class DummyEvent : Event

    class DummyCommandHandler : CommandHandler<DummyCommand> {
        override fun handle(command: DummyCommand) { }
        override fun getType(): Class<DummyCommand> = DummyCommand::class.java
    }

    class DummyRequestHandler : RequestHandler<DummyRequest, String> {
        override fun handle(request: DummyRequest) = "xpto"
        override fun getType(): Class<DummyRequest> = DummyRequest::class.java
    }

    class DummyEventHandler : EventHandler<DummyEvent> {
        override fun handle(event: DummyEvent) { }
        override fun getType(): Class<DummyEvent> = DummyEvent::class.java
    }

    @Configuration
    class SpringRegistryTestConfig {
        @Bean
        fun commandHandler(): CommandHandler<DummyCommand> {
            return DummyCommandHandler()
        }

        @Bean
        fun requestHandler(): RequestHandler<DummyRequest, String> {
            return DummyRequestHandler()
        }

        @Bean
        fun eventHandler(): EventHandler<DummyEvent> {
            return DummyEventHandler()
        }
    }

    @Test
    fun contextLoadRegistryBeanTest() {
        val registryBean = applicationContext.getBean(Registry::class.java)
        Assertions.assertNotNull(registryBean)
        Assertions.assertTrue(registryBean is SpringRegistry)
    }

    @Test
    fun getCommandHandlerTest() {
        val cmd = DummyCommand()
        val commandHandler = registry.getCommandHandler(cmd)
        Assertions.assertNotNull(commandHandler)
    }

    @Test
    fun getEventHandlerTest() {
        val event = DummyEvent()
        val eventHandlers = registry.getEventHandler(event)
        Assertions.assertNotNull(eventHandlers)
        Assertions.assertEquals(1, eventHandlers.size)
    }

    @Test
    fun getRequestHandlerTest() {
        val req: Request<String> = DummyRequest()
        val requestHandler = registry.getRequestHandler(req)
        Assertions.assertNotNull(requestHandler)
        val result = requestHandler!!.handle(req)
        Assertions.assertEquals("xpto", result)
    }
}
