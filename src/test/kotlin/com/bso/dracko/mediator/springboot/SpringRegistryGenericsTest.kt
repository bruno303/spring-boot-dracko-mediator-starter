package com.bso.dracko.mediator.springboot

import com.bso.dracko.mediator.contract.Command
import com.bso.dracko.mediator.contract.CommandHandler
import com.bso.dracko.mediator.contract.Event
import com.bso.dracko.mediator.contract.EventHandler
import com.bso.dracko.mediator.contract.Registry
import com.bso.dracko.mediator.contract.Request
import com.bso.dracko.mediator.contract.RequestHandler
import com.bso.dracko.mediator.springboot.SpringRegistryGenericsTest.TestConfig
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
@Import(TestConfig::class)
internal class SpringRegistryGenericsTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var registry: Registry

    class BaseCommandHandler<T : Command>(private val clazz: Class<T>) : CommandHandler<T> {
        override fun handle(command: T) {
            //
        }

        override fun getType(): Class<T> {
            return clazz
        }
    }

    class BaseEventHandler<T : Event>(private val clazz: Class<T>) : EventHandler<T> {
        override fun handle(event: T) {
            //
        }

        override fun getType(): Class<T> {
            return clazz
        }
    }

    @Configuration
    class TestConfig {
        @Bean
        fun commandHandler1(): CommandHandler<DummyCommand> {
            return BaseCommandHandler(DummyCommand::class.java)
        }

        @Bean
        fun requestHandler1(): RequestHandler<DummyRequest, String> {
            return object : RequestHandler<DummyRequest, String> {
                override fun getType(): Class<DummyRequest> = DummyRequest::class.java
                override fun handle(request: DummyRequest): String = "xpto"
            }
        }

        @Bean
        fun eventHandler1(): EventHandler<DummyEvent> {
            return object : EventHandler<DummyEvent> {
                override fun handle(event: DummyEvent) { }
                override fun getType(): Class<DummyEvent> = DummyEvent::class.java
            }
        }

        @Bean
        fun eventHandler2(): EventHandler<DummyEvent> {
            return BaseEventHandler(DummyEvent::class.java)
        }
    }

    class DummyCommand : Command
    class DummyRequest : Request<String>
    class DummyEvent : Event

    @Test
    fun contextLoadRegistryBeanTest() {
        val registryBean = applicationContext.getBean(Registry::class.java)
        Assertions.assertNotNull(registryBean)
        Assertions.assertTrue(registryBean is SpringRegistry)
    }

    @Test
    fun commandHandlerTest() {
        val cmd = DummyCommand()
        val commandHandler = registry.getCommandHandler(cmd)
        Assertions.assertNotNull(commandHandler)
    }

    @Test
    fun eventHandlerTest() {
        val event = DummyEvent()
        val eventHandlers = registry.getEventHandler(event)
        Assertions.assertNotNull(eventHandlers)
        Assertions.assertEquals(2, eventHandlers.size)
    }

    @Test
    fun requestHandlerTest() {
        val req: Request<String> = DummyRequest()
        val requestHandler = registry.getRequestHandler(req)
        Assertions.assertNotNull(requestHandler)
        val result = requestHandler!!.handle(req)
        Assertions.assertEquals("xpto", result)
    }
}
