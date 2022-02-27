package com.bso.dracko.mediator.springboot;

import com.bso.dracko.mediator.contract.Registry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DrackoMediatorSpringBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({ Registry.class })
    public Registry springRegistry(ApplicationContext ctx) {
        return new SpringRegistry(ctx);
    }
}
