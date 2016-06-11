package com.sparksdev.flo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
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
@EnableOAuth2Client
//@EnableOAuth2Sso
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Resource
    private AuthApi authApi;

    @Autowired
    private UserApi userApi;


    @Configuration
    public class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {

            http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);

            http
                    .requestMatcher(new AntPathRequestMatcher("/oauth/**"))
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userApi);
        }

        @Override
        public AuthenticationManager authenticationManager() throws Exception {
            return authApi;
        }

        /**
         * We override this method to ignore certain requests.
         *
         * @param webSecurity
         * @throws Exception
         */
        @Override
        public void configure(final WebSecurity webSecurity) throws Exception {
            // This seems to be the most reliable method of letting requests through
            webSecurity
                    .ignoring()
                    .antMatchers("/swagger-ui**", "/swagger-ui/**", "/swagger-editor**", "/webjars/**", "/configuration/**", "/swagger-resources/**", "/v2/**");


        }
    }

  /*  @Configuration
    @Order(1)
    public class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    // We will let these through and let oauth handle these...
                    .antMatchers("/users**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().and().csrf().disable();
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









    @EnableAuthorizationServer
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

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
