package io.github.github_repos_fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GithubListingService {

    @Autowired
    private final RestClient restClient;

    public GithubListingService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Repo> getUserRepos(String username){
        List<Repo> repos =  restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{username}/repos")
                        .build(username))
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        (req, res) -> {
                            throw new GithubUserNotFoundException(username);
                        }
                )
                .onStatus(
                        status -> status == HttpStatus.FORBIDDEN,
                        (req, res) -> {
                            throw new GithubRateLimitExceededException();
                        }
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw new GithubInternalError();
                        }
                )
                .body(new ParameterizedTypeReference<List<Repo>>() {});

        return repos.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    List<Branch> branches = restClient.get()
                            .uri("/repos/{username}/{repo}/branches", username, repo.name())
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<Branch>>(){});

                    return new Repo(repo.name(), repo.ownerLogin(), false, branches);
                }).toList();
    }
}
