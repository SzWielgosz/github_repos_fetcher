package io.github.github_repos_fetcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${github.api-url}")
    private String githubApiUrl;

    @Bean
    RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl(this.githubApiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
