package com.ffssr.website.config;

import com.ffssr.website.models.repo.AdmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private AdmRepository admRepository;

/*    public ArrayList getPas(){
        Iterable<Adm> admins = admRepository.findAll();
        ArrayList<Adm> list = new ArrayList<Adm>();
        Iterator<Adm> iterator = admins.iterator();
        while (iterator.hasNext()) {
            Adm ad = iterator.next();
            list.add(ad);
        }
        return list;
    }*/

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        //Adm ilya = (Adm) getPas().get(0);

        auth.inMemoryAuthentication()
                .withUser("ilya")
                .password("ilya")
                .authorities("ROLE_ADMIN")
                .and()
                .withUser("Kirill")
                .password("Kirillpass")
                .authorities("ROLE_ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/blog/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/blog/add/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/blog/**/remove").hasAuthority("ROLE_ADMIN")
                .antMatchers("/blog/*/edit/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/blog/addDocument").hasAuthority("ROLE_ADMIN")
                .antMatchers("/documentAdmin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/contactsAdmin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/competition/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/competition/add/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/competition/**/remove").hasAuthority("ROLE_ADMIN")
                .antMatchers("/competition/*/edit/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/documentAdmin/**/remove").hasAuthority("ROLE_ADMIN")
                .antMatchers("/**").permitAll()
                .and()
                .formLogin().permitAll()
                .loginPage("/login")
                .loginProcessingUrl("/perform-login")
                .defaultSuccessUrl("/blog/admin")
                .and().csrf().disable();
    }

    @Bean
    public PasswordEncoder encoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
