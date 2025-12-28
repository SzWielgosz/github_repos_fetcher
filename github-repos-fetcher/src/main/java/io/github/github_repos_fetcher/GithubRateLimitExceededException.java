package io.github.github_repos_fetcher;

public class GithubRateLimitExceededException extends RuntimeException {
    public GithubRateLimitExceededException() {
        super("Github rate limit exceeded");
    }
}
