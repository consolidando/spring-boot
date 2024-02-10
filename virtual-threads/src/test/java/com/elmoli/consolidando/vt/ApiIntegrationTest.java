package com.elmoli.consolidando.vt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles({"simple", "servlet"})
//@ActiveProfiles({"reactor", "flux", "test"})
class ApiIntegrationTest
{

//    @Autowired
//    private EpisodeService episodeApiClient;
//
//    @Autowired
//    private DemoApplication demoApplication;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    private void waitForDatabaseInitialization() throws InterruptedException
//    {
//        int maxWaitSeconds = 10;
//        int waitIntervalMillis = 1000;
//
//        for (int i = 0; i < maxWaitSeconds; i++)
//        {
//            if (demoApplication.getDatabaseState() == DemoApplication.DatabaseState.INITIALIZED)
//            {
//                break;
//            }
//            Thread.sleep(waitIntervalMillis);
//        }
//    }

    @Test
    void testDatabaseInitialization() throws Exception
    {
//        long count;
//        //
//        waitForDatabaseInitialization();
//
//        //
//        var appState = demoApplication.getDatabaseState();
//        assertEquals(DemoApplication.DatabaseState.INITIALIZED, appState);
//        int episodeId = demoApplication.getDatabaseCharactersEpisodeId();
//        assertTrue(episodeId > 0);
//
//        Repository respository = demoApplication.getRepository();
//
//        switch (respository) 
//        {
//            case CharacterRepository cr -> count = cr.count();               
//            case CharacterFluxRepository cfr -> count = cfr.count().block();
//            default -> throw new IllegalArgumentException("Unrecognized repository");
//        }
//
//        List<EpisodeCharactersIdData.CharacterId> episodeInfoList = episodeApiClient.getEpisodeInfo(episodeId);
//
//        assertEquals(count, episodeInfoList.size());
    }

    @Test
    void testGetEndpoint2() throws Exception
    {
//        webTestClient.get()
//                .uri("/apis/characters")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk();
    }

}
