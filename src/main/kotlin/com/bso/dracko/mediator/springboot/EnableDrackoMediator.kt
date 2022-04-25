package com.bso.dracko.mediator.springboot

import org.springframework.context.annotation.ComponentScan

@ComponentScan(basePackages = ["com.bso.dracko.mediator.springboot"])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class EnableDrackoMediator
