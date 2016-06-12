/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.sparksdev.flo.server;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import com.sparksdev.flo.authentication.api.AuthApi;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * Registers the servlets needed for the design and runtime apps.
 *
 * @author bengill
 */
@Configuration
public class ServerServletRegistration extends SpringBootServletInitializer {

    @Inject
    AuthApi authApi;

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ServerServletRegistration.class/*, ComponentConfiguration.class*/);
    }

    private ServletRegistrationBean register(final Servlet servlet, final String... uris) {
        ServletRegistrationBean registration = new ServletRegistrationBean(servlet);
        registration.addUrlMappings(uris);
        return registration;
    }


    private FilterRegistrationBean register(final Filter servlet, final String... uris) {
        FilterRegistrationBean registration = new FilterRegistrationBean(servlet);
        registration.addUrlPatterns(uris);
        return registration;
    }


}
