package com.example.zoo.config;

import com.example.zoo.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    // Конфигурация Spring Security
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // указываем на какой url адрес фильтр Spring Security будет отправлять не аутентифицированного  пользователь при заходе на защищенную страницу
        httpSecurity
 //               .antMatcher("/css/*")
                // указываем что все страницы будут защищены процессом аутентификации
                .authorizeRequests()
                // указываем что страница /admin доступна пользователю с ролью ADMIN
                .antMatchers("/admin").hasRole("ADMIN")
                // Указыаем что данные страницы доступна все пользователям
                .antMatchers("/", "/css/**", "/authentication/logout", "/authentication/login", "/authentication/myLogin", "/authentication/registration", "/error", "/product", "/static/img/logomini.png", "/img/*", "/product/info/{id}", "/product/search").permitAll()
                // Указываем что все остальные страницы доступны пользователям с ролью user и admin
                .anyRequest().hasAnyRole("USER", "ADMIN")
//                // Указываем что для всех остальных страниц необходимо вызывать метод authenticated(), который открывает форму аутентификации
//                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/authentication/login")
                // указываем на какой url будут отправляться данные с формы аутентификации
                .loginProcessingUrl("/process_login")
                // Указываем на какой url необходимо направить пользователя после успешной аутентификации
                .defaultSuccessUrl("/index", true)
                // Указываем куда необходимо перейти при неверный аутентификации
                .failureUrl("/authentication/login?error")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/authentication/login");
    }


    // Данный метод позволяет настроить аутентификацию
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web){
        web.ignoring().antMatchers("/js/**", "/css/**", "img/**","/fonts/**", "/resources/**", "/static/**");
    }
}
