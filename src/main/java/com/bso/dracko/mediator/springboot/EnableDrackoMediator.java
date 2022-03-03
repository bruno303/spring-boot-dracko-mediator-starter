package com.bso.dracko.mediator.springboot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.bso.dracko.mediator.springboot")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableDrackoMediator {

}
