package io.github.github_repos_fetcher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GithubUserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleGithubUserNotFoundException(GithubUserNotFoundException e){
        return new ResponseEntity<>(
                Map.of("status", HttpStatus.NOT_FOUND.value(), "message", e.getMessage()), HttpStatus.NOT_FOUND
        );

    }
    @ExceptionHandler(GithubRateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleGithubRateLimitExceededException(GithubRateLimitExceededException e){
        return new ResponseEntity<>(
                Map.of("status", HttpStatus.TOO_MANY_REQUESTS.value(), "message", e.getMessage()), HttpStatus.TOO_MANY_REQUESTS
        );
    }

    @ExceptionHandler(GithubInternalError.class)
    public ResponseEntity<Map<String, Object>> handleGithubDown(
            GithubInternalError e
    ) {
        return new ResponseEntity<>(
                Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}

