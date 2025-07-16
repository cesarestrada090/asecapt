package com.fitech.app;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude={
	DataSourceAutoConfiguration.class,
	org.springframework.boot.autoconfigure.aop.AopAutoConfiguration.class
})
@EnableAutoConfiguration
@ConfigurationPropertiesScan
public class FitechApp {
	public static void main(String[] args) {
		SpringApplication.run(FitechApp.class, args);
	}
}
