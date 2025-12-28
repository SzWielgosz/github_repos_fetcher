package io.github.github_repos_fetcher;

public class GithubUserNotFoundException extends RuntimeException {
    public GithubUserNotFoundException(String username) {
        super("Github user '" + username + "' not found");
    }
}
