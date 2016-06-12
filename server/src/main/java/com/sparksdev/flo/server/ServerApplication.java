/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.sparksdev.flo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sparksdev.flo.startup.service.SeedService;

import java.security.Principal;

/**
 *  @SpringBootApplication is a convenience annotation that adds all of the following:
 *
 *  @Configuration tags the class as a source of bean definitions for the application context.
 *  @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
 *  Normally you would add @EnableWebMvc for a Spring MVC app, but Spring Boot adds it automatically when it sees spring-webmvc on the classpath.
 *  This flags the application as a web application and activates key behaviors such as setting up a DispatcherServlet.
 *  @ComponentScan tells Spring to look for other components, configurations, and services in the the hello package, allowing it to find the HelloController.
 *
 *  Spring Boot by default will serve static content from a folder called /static (or /public or or /resources or /META-INF/resources)
 *  in the classpath or from the root of the ServletContext.
 */
@SpringBootApplication
@RestController
@ComponentScan(basePackages = "com.sparksdev.flo" )
@PropertySource("classpath:application.properties")
public class ServerApplication {


    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ServerApplication.class, args);
        SeedService seedService = ctx.getBean(SeedService.class);
        seedService.seed();
    }




}
