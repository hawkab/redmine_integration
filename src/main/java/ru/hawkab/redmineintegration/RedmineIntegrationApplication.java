package ru.hawkab.redmineintegration;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static ru.hawkab.redmineintegration.Constants.*;

@SpringBootApplication
@Configuration
@EnableScheduling
public class RedmineIntegrationApplication {


    public static void main(String[] args) {
        SpringApplication.run(RedmineIntegrationApplication.class, args);
    }

    @Bean
    public RedmineManager getRedmineApiManager() {
        return RedmineManagerFactory.createWithApiKey(
                REDMINE_URL,
                REDMINE_API_ACCESS_TOKEN
        );
    }

}
