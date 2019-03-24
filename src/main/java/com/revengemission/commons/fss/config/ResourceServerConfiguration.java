package com.revengemission.commons.fss.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class ResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .mvcMatchers("/upload/**").hasAnyAuthority("SCOPE_read")
                .mvcMatchers("/protected/**").hasAnyAuthority("SCOPE_read")
                .mvcMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().bearerTokenResolver(new CustomTokenResolver())
                .jwt();
    }

}
