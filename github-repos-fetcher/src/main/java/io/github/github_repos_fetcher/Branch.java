package io.github.github_repos_fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Branch {
    private String name;

    @JsonProperty("commit")
    private void unpackOwner(Map<String, Object> commit){
        this.sha = (String) commit.get("sha");
    }

    private String sha;

    public Branch(String name, String sha) {
        this.name = name;
        this.sha = sha;
    }

    public Branch() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
}
