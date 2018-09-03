package com.app.MBox.config;

import com.app.MBox.aditional.rolesEnum;
import com.app.MBox.services.userDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class securityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    userDetailsServiceImpl userDetailsServiceImpl;

    public void configure (WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/jquery/**/","/bootstrap/**/","/css/**","/images/**","/js/**");


    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("jquery/**/","bootstrap/**/","css/**","images/**","js/**").permitAll()
                .antMatchers("/registration","/home/homepage").permitAll().antMatchers("/successRegister","/forgotPassword","/confirm").permitAll()
                .antMatchers("/successfullConfirm","/unSuccessfullConfirm","/resetPassword").permitAll()
                .antMatchers("/admin").hasAnyRole(rolesEnum.ADMIN.toString()).anyRequest().authenticated()
                .antMatchers("/changePassword").hasAnyRole(rolesEnum.ADMIN.toString(),rolesEnum.LISTENER.toString(),rolesEnum.ARTIST.toString(),rolesEnum.RECORDLABEL.toString()).anyRequest().authenticated()
                .and().formLogin().loginPage("/login").loginProcessingUrl("/app-login").usernameParameter("app_username").passwordParameter("app_password").defaultSuccessUrl("/home/homepage").permitAll()
                .and().logout().logoutUrl("/app-logout").logoutSuccessUrl("/login")
                .and().exceptionHandling().accessDeniedPage("/error");

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder);

    }


}