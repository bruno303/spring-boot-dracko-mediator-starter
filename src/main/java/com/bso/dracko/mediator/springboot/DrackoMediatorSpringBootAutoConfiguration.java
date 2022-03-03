package com.bso.dracko.mediator.springboot;

import com.bso.dracko.mediator.MediatorImpl;
import com.bso.dracko.mediator.contract.Mediator;
import com.bso.dracko.mediator.contract.Registry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.bso.dracko.mediator.springboot")
public class DrackoMediatorSpringBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({ Registry.class })
    public Registry springRegistry(ApplicationContext ctx) {
        return new SpringRegistry(ctx);
    }

    @Bean
    @ConditionalOnMissingBean({ Mediator.class })
    public Mediator mediatorImpl(Registry registry) {
        return new MediatorImpl(registry);
    }
}
