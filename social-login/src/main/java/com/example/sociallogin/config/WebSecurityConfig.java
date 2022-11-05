package com.example.sociallogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final OAuthLoginSuccessHandler authLoginSuccessHandler;


    public WebSecurityConfig(DefaultOAuth2UserService defaultOAuth2UserService, UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, CustomAuthenticationFailureHandler customAuthenticationFailureHandler, OAuthLoginSuccessHandler authLoginSuccessHandler) {
        this.defaultOAuth2UserService = defaultOAuth2UserService;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.authLoginSuccessHandler = authLoginSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().permitAll()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .passwordParameter("pass")
                    .failureHandler(customAuthenticationFailureHandler)
                .and()
                .oauth2Login()
                    .loginPage("/login")
                    .userInfoEndpoint()
                    .userService(defaultOAuth2UserService)
                    .and()
                    .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
                    .and()
                    .successHandler(authLoginSuccessHandler)
                .and()
                .logout().logoutSuccessUrl("/").permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
