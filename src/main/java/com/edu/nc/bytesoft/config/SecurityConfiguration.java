package com.edu.nc.bytesoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.vaadin.spring.security.annotation.EnableVaadinManagedSecurity;
import org.vaadin.spring.security.config.AuthenticationManagerConfigurer;

@Configuration
@EnableVaadinManagedSecurity
public class SecurityConfiguration implements AuthenticationManagerConfigurer {

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.inMemoryAuthentication()
                .withUser("customer").password("c").roles("CUSTOMER")
                .and()
                .withUser("admin").password("a").roles("ADMIN")
                .and()
                .withUser("developer").password("d").roles("DEVELOPER")
                .and()
                .withUser("dev_admin").password("da").roles("ADMIN", "DEVELOPER");
    }
}
