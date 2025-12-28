package io.github.github_repos_fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Repo {
    private String name;
    
    @JsonProperty("owner")
    private void unpackOwner(Map<String, Object> owner){
        this.ownerLogin = (String) owner.get("login");
    }

    private String ownerLogin;

    private boolean fork;

    private List<Branch> branches;

    public Repo() {
    }

    public Repo(String name, String ownerLogin, List<Branch> branches, boolean fork) {
        this.name = name;
        this.ownerLogin = ownerLogin;
        this.fork = fork;
        this.branches = branches;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }
}
