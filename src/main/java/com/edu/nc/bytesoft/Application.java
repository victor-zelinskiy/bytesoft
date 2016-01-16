package com.edu.nc.bytesoft;

import com.edu.nc.bytesoft.config.RegisterBeanFactoryInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class)
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).initializers(new RegisterBeanFactoryInitializer()).run(args);

    }
}
