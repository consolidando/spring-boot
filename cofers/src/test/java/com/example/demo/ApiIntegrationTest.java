package com.example.demo;

import com.example.demo.domain.CharacterInfoRepository;
import com.example.demo.service.EpisodeApiClient;
import com.example.demo.service.EpisodeInfoData;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest
{

    @Autowired
    private EpisodeApiClient episodeApiClient;

    @Autowired
    private CharacterInfoRepository characterRepository;

    @Autowired
    private DemoApplication demoApplication;

    @Autowired
    private WebTestClient webTestClient;

    private void waitForDatabaseInitialization() throws InterruptedException
    {
        int maxWaitSeconds = 10;
        int waitIntervalMillis = 1000;

        for (int i = 0; i < maxWaitSeconds; i++)
        {
            if (demoApplication.getDatabaseState() == DemoApplication.DatabaseState.INITIALIZED)
            {
                break;
            }
            Thread.sleep(waitIntervalMillis);
        }
    }

    @Test
    void testDatabaseInitialization() throws Exception
    {
        //
        waitForDatabaseInitialization();

        //
        var appState = demoApplication.getDatabaseState();
        assertEquals(DemoApplication.DatabaseState.INITIALIZED, appState);
        int episodeId = demoApplication.getDatabaseCharactersEpisodeId();
        assertTrue(episodeId > 0);

        long count = characterRepository.count();

        List<EpisodeInfoData.Character> episodeInfoList = episodeApiClient.getEpisodeInfo(episodeId).block();

        assertEquals(count, episodeInfoList.size());
    }

    @Test
    void testGetEndpoint2() throws Exception
    {
        webTestClient.get()
                .uri("/apis/characters")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testNotImplemented() throws Exception
    {
        // POST
        webTestClient.post()
                .uri("/apis/characters")
                .exchange()
                .expectStatus().isBadRequest();

        // PUT {ID}
        webTestClient.put()
                .uri("/apis/characters/1")
                .exchange()
                .expectStatus().isBadRequest();

        // GET {ID}
        webTestClient.put()
                .uri("/apis/characters/1")
                .exchange()
                .expectStatus().isBadRequest();

        // DELETE {ID}
        webTestClient.delete()
                .uri("/apis/characters/1")
                .exchange()
                .expectStatus().is4xxClientError();
    }

}
