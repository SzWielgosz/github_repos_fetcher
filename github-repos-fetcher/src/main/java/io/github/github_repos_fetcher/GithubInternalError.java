package io.github.github_repos_fetcher;

public class GithubInternalError extends RuntimeException {
    public GithubInternalError() {
        super("Github internal error");
    }
}
