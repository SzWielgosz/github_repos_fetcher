package io.github.github_repos_fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
public record Repo(String name, String ownerLogin, boolean fork, List<Branch> branches) {
    public Repo(String name, @JsonProperty("owner") Map<String, String> owner, boolean fork, List<Branch> branches){
        this(name, owner.get("login"), fork, branches);
    }
}