package io.github.github_repos_fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GithubListingController {

    @Autowired
    private final GithubListingService githubListingService;

    public GithubListingController(GithubListingService githubListingService) {
        this.githubListingService = githubListingService;
    }

    @GetMapping("/{username}/repos")
    public ResponseEntity<?> getUserRepositories(
            @PathVariable(value = "username") String username
    ) {
        List<Repo> repos = githubListingService.getUserRepos(username);
        repos = githubListingService.excludeForkedRepos(repos);
        repos = githubListingService.addBranchesToRepo(repos, username);

        return ResponseEntity.ok(repos);
    }

}
