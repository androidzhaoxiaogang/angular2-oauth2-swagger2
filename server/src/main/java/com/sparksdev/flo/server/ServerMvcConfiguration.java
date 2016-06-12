/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.sparksdev.flo.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author bengill
 *
 * If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc.
 * If you want to keep Spring Boot MVC features, and you just want to add additional MVC configuration
 * (interceptors, formatters, view controllers etc.) you can add your own @Bean of type WebMvcConfigurerAdapter, but without @EnableWebMvc.
 */
@Configuration
public class ServerMvcConfiguration extends WebMvcConfigurerAdapter {


    // We should not have to do this...  this should happen automatically for us!
   /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }*/
}
