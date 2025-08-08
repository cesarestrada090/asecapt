package com.asecapt.app;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ComponentScan
@Configuration
@EnableJpaRepositories
class AsecaptAppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}