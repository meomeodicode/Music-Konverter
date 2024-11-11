package com.minh.konverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import io.github.cdimascio.dotenv.Dotenv;
import me.paulschwarz.springdotenv.DotenvPropertySource;


import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        try {
            SpringApplication application = new SpringApplication(App.class);
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            
            // Add DotenvPropertySource to the Spring environment
            DotenvPropertySource.addToEnvironment(applicationContext.getEnvironment());
            
            // Register configuration class
            applicationContext.register(ConfigEnv.class);
            applicationContext.refresh();
            
            // Run the Spring Boot application
            application.run(args); 
        } catch (Exception e) {  // Catch any exceptions that may occur
            e.printStackTrace(); 
          }  // Print the stack trace of the exception
        }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}