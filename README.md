# github_repos_fetcher
This is a simple project about getting specific data from external github api and sharing them via Spring Boot API application.
## Requirements for the project
Docker installed
## How to open this project
Run ``docker compose up`` command to create app container.

After container creation the application is avaliable via localhost:8080.

## Avaliable endpoints
``/{username}/repos`` **GET** - returns list of all public repositories that are not forks

succesful response looks like this:
```json
  [
    {
      "name": "some_name",
      "ownerLogin": "some_owner",
      "branches": [
        {
          "name": "branch_name",
          "sha": "last_commit_sha"
        }
      ]
      "fork": false
    }
  ]
```

If there's an error then the response looks like this:
```json
{
    "status": "response_code"
    "message": "some_message"
}

```

## Extras
- github API provides 80 requests per hour for unauthenticated users
- If you exceed the limit then you can create a github secret and add ``.defaultHeader("Authorization", "Bearer {your_secret})"`` to **RestClientConfig** class and restart the container
- **WARNING:** do not share your secret key with anyone! Keep this data away from public repositories
