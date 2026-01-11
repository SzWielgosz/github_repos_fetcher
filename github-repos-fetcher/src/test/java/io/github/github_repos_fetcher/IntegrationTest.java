package io.github.github_repos_fetcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "github.api-url=http://localhost:8089"
)
@AutoConfigureWebTestClient
public class IntegrationTest {
    static WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void startWireMock(){
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock(){
        wireMockServer.stop();
    }

    @Test
    void shouldGetGithubUserRepositories() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/test/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                        {
                            "name": "testRepo",
                            "owner": {
                                "login": "test"
                            },
                            "fork": false
                        },
                        {
                            "name": "testRepo2",
                            "owner": {
                                "login": "test"
                            },
                            "fork": true
                        }
                    ]
                """)));


        wireMockServer.stubFor(WireMock.get(urlEqualTo("/repos/test/testRepo/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                        {
                            "name": "main",
                            "commit": {
                                "sha": "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"
                            }
                        }
                    ]
                """)));


        this.webTestClient.get()
                .uri("/test/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("testRepo")
                .jsonPath("$[0].ownerLogin").isEqualTo("test")
                .jsonPath("$[0].branches[0].name").isEqualTo("main")
                .jsonPath("$[0].branches[0].sha").isEqualTo("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3")
                .jsonPath("$[0].fork").isEqualTo(false);
    }


    @Test
    void shouldGetOkWithoutRepos() throws Exception {
        wireMockServer.stubFor(
                WireMock.get("/users/test/repos")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                        []
                                        """
                                )));

        this.webTestClient.get()
                .uri("/test/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }


    @Test
    void shouldGetNotFoundException() throws Exception {
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/users/notExistingUser/repos"))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                            {
                                                "message": "Not Found",
                                                "documentation_url": "https://docs.github.com/rest/repos/repos#list-repositories-for-a-user",
                                                "status": "404"
                                            }
                                        """
                                )));

        this.webTestClient.get()
                .uri("/notExistingUser/repos")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Github user 'notExistingUser' not found")
                .jsonPath("$.status").isEqualTo(404);

    }


    @Test
    void shouldGetRateLimitException() throws Exception {
        wireMockServer.stubFor(
                WireMock.get("/users/test/repos")
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                        {
                                            "message": "API rate limit exceeded for 'some_ip'",
                                            "documentation_url": "https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"
                                        }
                                        """
                                )));

        this.webTestClient.get()
                .uri("/test/repos")
                .exchange()
                .expectStatus().isEqualTo(429)
                .expectBody()
                .jsonPath("$.status").isEqualTo(429)
                .jsonPath("$.message").isEqualTo("Github rate limit exceeded");
    }


    @Test
    void shouldGetInternalServerErrorException() throws Exception {
        wireMockServer.stubFor(
                WireMock.get("/users/test/repos")
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")));

        this.webTestClient.get()
                .uri("/test/repos")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("Github internal error");
    }
}
