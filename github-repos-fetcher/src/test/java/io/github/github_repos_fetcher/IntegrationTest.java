package io.github.github_repos_fetcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest(properties = {
        "github.api-url=http://localhost:8089"
})
@AutoConfigureMockMvc
public class IntegrationTest {
    static WireMockServer wireMockServer;

    @Autowired
    MockMvc mockMvc;

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
        String username = "test";
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/test/repos"))
                .willReturn(aResponse()
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
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                        {
                            "name": "main",
                            "sha": "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"
                        }
                    ]
                """)));



        this.mockMvc.perform(get("/test/repos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ownerLogin").value(username))
                .andExpect(jsonPath("$[0].name").value("testRepo"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].sha").value("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"))
                .andExpect(jsonPath("$[0].fork").value(false));
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

        this.mockMvc.perform(get("/test/repos"))
                .andExpect(status().is(200));
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

        this.mockMvc.perform(get("/notExistingUser/repos"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message").value("Github user 'notExistingUser' not found"))
                .andExpect(jsonPath("$.status").value(404));

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
                                            "message": "API rate limit excedeed for 'some_ip'",
                                            "documentation_url": "https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"
                                        }
                                        """
                                )));

        this.mockMvc.perform(get("/test/repos"))
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").value("Github rate limit exceeded"));
    }


    @Test
    void shouldGetInternalServerErrorException() throws Exception {
        wireMockServer.stubFor(
                WireMock.get("/users/test/repos")
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")));

        this.mockMvc.perform(get("/test/repos"))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Github internal error"));
    }
}
