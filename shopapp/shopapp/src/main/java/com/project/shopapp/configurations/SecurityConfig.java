package com.project.shopapp.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.project.shopapp.repositorys.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    // user detail object
    @Bean
    public UserDetailsService userDetailsService (){
        return phoneNumber ->
             userRepository
            .findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy số điện thoại: "+phoneNumber));

        

        
    }

    // mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider aProvider = new DaoAuthenticationProvider();
        aProvider.setUserDetailsService(userDetailsService());
        aProvider.setPasswordEncoder(passwordEncoder());
        return aProvider;

    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();

    }

    

    
}
