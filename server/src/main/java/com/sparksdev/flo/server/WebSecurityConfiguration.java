package com.sparksdev.flo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.sparksdev.flo.authentication.api.AuthApi;
import com.sparksdev.flo.user.api.user.UserApi;

import javax.annotation.Resource;

/**
 * @author bengill
 */

@Configuration
//@EnableAuthorizationServer
//@EnableResourceServer
@EnableOAuth2Client // I would have though this would setup OAuth2ClientAuthenticationProcessingFilter and
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Resource
    private AuthApi authApi;

    @Autowired
    private UserApi userApi;


    @Order
    @Configuration
    public class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {

            http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);

            // We need to capture this URL, so our authentication manager handles the /oauth/authorize basic authentication filter
            http
                    .requestMatcher(new AntPathRequestMatcher("/oauth/**"))
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


           /* http
                    .requestMatcher(new AntPathRequestMatcher("/users**"))
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);*/



            http.authorizeRequests().anyRequest().permitAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userApi);
        }

        @Override
        public AuthenticationManager authenticationManager() throws Exception {
            return authApi;
        }

        @Override
        public void configure(final WebSecurity webSecurity) throws Exception {
            // This seems to be the most reliable method of letting requests through
            webSecurity
                    .ignoring()
                    .antMatchers("/swagger-ui**", "/swagger-ui/**", "/swagger-editor**", "/webjars/**", "/configuration/**", "/swagger-resources/**", "/v2/**");
        }
    }

   /* @Configuration
    @Order(1)
    public class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);

            http
                    .authorizeRequests()
                            .anyRequest().permitAll()
                            .and()
                            .formLogin();
        }

        @Override
        public void configure(final WebSecurity webSecurity) throws Exception {
            // This seems to be the most reliable method of letting requests through

            webSecurity
                    .ignoring()
                    .antMatchers("/oauth**", "/users**");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userApi);
        }

        @Override
        public AuthenticationManager authenticationManager() throws Exception {
            return authApi;
        }
    }

*/
    // This only needs to cover the REST endpoints
    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/users/**").authorizeRequests().anyRequest().authenticated();
        }
    }





    @EnableAuthorizationServer
    protected static class OAuthConfig extends AuthorizationServerConfigurerAdapter {

        @Resource
        private AuthApi authApi;

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.allowFormAuthenticationForClients();
        }


        @Override // [2]
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authApi);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {


            clients.inMemory()
                    .withClient("foo")
                    .secret("bar")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                    .scopes("read", "write", "trust", "update", "global", "openid");

        }
    }
}
