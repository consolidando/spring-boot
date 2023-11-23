package com.elmoli.consolidando;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoogleSignInApplicationTests
{

    private @Autowired
    WebTestClient webTestClient;

    final String invalidTokenString = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Test
    public void testRedirectionWhenAccessUsersWithoutIDToken()
    {
        webTestClient
                .get().uri("/users")
                .exchange()
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().valueMatches(HttpHeaders.LOCATION, ".*\\/index\\.html\\?v=2$");
    }

    @Test
    public void testInvalidIDToken()
    {

        webTestClient
                .get().uri("/users")
                .headers(http -> http.setBearerAuth(invalidTokenString))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().valueMatches(HttpHeaders.WWW_AUTHENTICATE, 
                        "Bearer error=\"invalid_token\",.*");
    }

}
