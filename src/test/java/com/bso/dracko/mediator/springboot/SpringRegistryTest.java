package com.bso.dracko.mediator.springboot;

import com.bso.dracko.mediator.contract.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({ SpringRegistryTest.SpringRegistryTestConfig.class })
class SpringRegistryTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Registry registry;

    @Configuration
    public static class SpringRegistryTestConfig {
        @Bean
        public CommandHandler<DummyCommand> commandHandler() {
            return new DummyCommandHandler();
        }
        @Bean
        public RequestHandler<DummyRequest, String> requestHandler() {
            return new DummyRequestHandler();
        }
        @Bean
        public EventHandler<DummyEvent> eventHandler() {
            return new DummyEventHandler();
        }
    }

    private static class DummyCommand implements Command {}
    private static class DummyRequest implements Request<String> {}
    private static class DummyEvent implements Event {}

    private static class DummyRequestHandler implements RequestHandler<DummyRequest, String> {
        @Override
        public String handle(DummyRequest dummyRequest) { return "xpto"; }
    }
    private static class DummyCommandHandler implements CommandHandler<DummyCommand> {
        @Override
        public void handle(DummyCommand dummyCommand) { /* do nothing */ }
    }
    private static class DummyEventHandler implements EventHandler<DummyEvent> {
        @Override
        public void handle(Event event) { /* do nothing */ }
    }

    @Test
    void contextLoadRegistryBeanTest() {
        Registry registryBean = applicationContext.getBean(Registry.class);
        assertNotNull(registryBean);
        assertTrue(registryBean instanceof SpringRegistry);
    }

    @Test
    void getCommandHandlerTest() {
        DummyCommand cmd = new DummyCommand();
        CommandHandler<DummyCommand> commandHandler = registry.getCommandHandler(cmd);
        assertNotNull(commandHandler);
    }

    @Test
    void getEventHandlerTest() {
        var event = new DummyEvent();
        List<EventHandler<DummyEvent>> eventHandlers = registry.getEventHandler(event);
        assertNotNull(eventHandlers);
        assertEquals(1, eventHandlers.size());
    }

    @Test
    void getRequestHandlerTest() {
        Request<String> req = new DummyRequest();
        RequestHandler<Request<String>, String> requestHandler = registry.getRequestHandler(req);
        assertNotNull(requestHandler);
        String result = requestHandler.handle(req);
        assertEquals("xpto", result);
    }
}