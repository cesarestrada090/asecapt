package com.fitech.app;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan
@Configuration
@EnableJpaRepositories
class FitechAppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}