package com.friday.client.conf.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-06-10:10:42
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity security) throws Exception {
//        security
//                //不拦截请求
//                .authorizeRequests()
////                .antMatchers("/resources/**").permitAll()
//                .antMatchers("/friday-im/login", "friday-im/register").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                //登录请求
//                .formLogin()
//                .loginPage("/friday-im/login")
//                .successHandler(new AuthSuccessHandler())
//                .failureHandler(new AuthFailHandler())
//                .permitAll()
//                .and()
//                //登出
//                .logout()
//                .logoutUrl("/friday-im/logout")
//                .and()
//                //session保留时间
//                .rememberMe()
//                .tokenValiditySeconds(1800)
//                .and()
//                //session 管理
//                .sessionManagement()
//                .invalidSessionUrl("/friday-im/login")
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(true)
//                .expiredSessionStrategy(new SessionExpiredStrategy())
//        ;
        super.configure(security);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
