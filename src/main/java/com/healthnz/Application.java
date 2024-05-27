package com.healthnz;

import com.healthnz.service.DroolsProcessor;
import com.healthnz.service.RuleMonitor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            RuleMonitor ruleMonitor = ctx.getBean(RuleMonitor.class);
            ruleMonitor.start();
        };
    }
}