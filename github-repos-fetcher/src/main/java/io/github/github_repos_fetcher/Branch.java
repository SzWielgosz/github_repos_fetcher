package io.github.github_repos_fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Branch(String name, String sha) {
    public Branch(String name, @JsonProperty("commit") Map<String, Object> commit){
        this(name, (String) commit.get("sha"));
    }
}
