package com.bso.dracko.mediator.springboot

import com.bso.dracko.mediator.MediatorImpl
import com.bso.dracko.mediator.contract.Mediator
import com.bso.dracko.mediator.contract.Registry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DrackoMediatorSpringBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Registry::class)
    fun springRegistry(ctx: ApplicationContext) = SpringRegistry(ctx)

    @Bean
    @ConditionalOnMissingBean(Mediator::class)
    fun mediatorImpl(registry: Registry) = MediatorImpl(registry)
}
