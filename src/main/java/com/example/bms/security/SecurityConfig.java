package com.example.bms.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/resources/**", "/").permitAll()
                .mvcMatchers("/register/**", "/login/**").anonymous()
                .mvcMatchers(HttpMethod.GET, "/user/edit/{id}/**").access("@guard.checkUser(authentication, #id)")
                .mvcMatchers(HttpMethod.PUT, "/user/{id}/**").access("@guard.checkUser(authentication, #id)")
                .mvcMatchers("/user/**", "/role/**", "/admin/**").hasAuthority("ROLE_ADMIN")
                .mvcMatchers(HttpMethod.POST, "/shop/**").hasAuthority("ROLE_USER")
                .mvcMatchers(HttpMethod.PUT, "/shop/{id}/**").access("@guard.checkShop(authentication, #id)")
                .mvcMatchers(HttpMethod.DELETE, "/shop/{id}/**").access("@guard.checkShop(authentication, #id)")
                .mvcMatchers(HttpMethod.POST, "/service/shop/{id}/**").access("@guard.checkShop(authentication, #id)")
                .mvcMatchers(HttpMethod.PUT, "/service/{id}/**").access("@guard.checkAppservice(authentication, #id)")
                .mvcMatchers(HttpMethod.DELETE, "/service/{id}/**").access("@guard.checkAppservice(authentication, #id)")
                .mvcMatchers(HttpMethod.GET, "/appointment/{id}/**").access("@guard.checkAppointment(authentication, #id, false)")
                .mvcMatchers(HttpMethod.PUT, "/appointment/{id}/**").access("@guard.checkAppointment(authentication, #id, false)")
                .mvcMatchers(HttpMethod.DELETE, "/appointment/{id}/**").access("@guard.checkAppointment(authentication, #id, true)")
                .anyRequest().hasAuthority("ROLE_USER")
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/profile")
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
