/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.config;

import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeApiWebClient;
import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.service.EpisodeService;
import com.elmoli.consolidando.vt.service.ExecutorsVTEpisodeService;
import com.elmoli.consolidando.vt.service.GQLEpisodeService;
import com.elmoli.consolidando.vt.service.ReactorEpisodeService;
import com.elmoli.consolidando.vt.service.ReactorGQLEpisodeServide;
import com.elmoli.consolidando.vt.service.SimpleVTEpisodeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EpisodeServiceConfig
{

    @Bean
    @ConditionalOnProperty(name = "app.episode-service", havingValue = "simple")
    public EpisodeService simpleEpisodiApi(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        return new SimpleVTEpisodeService(episodeApiClient, characterRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.episode-service", havingValue = "executors")
    public EpisodeService executorsEpisodiApi(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        return new ExecutorsVTEpisodeService(episodeApiClient, characterRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.episode-service", havingValue = "graphql")
    public EpisodeService graphqlEpisodiApi(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        return new GQLEpisodeService(episodeApiClient, characterRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.episode-service", havingValue = "reactor-gql")
    public EpisodeService reactorGQLEpisodiApi(EpisodeApiWebClient episodeApiClient,
            CharacterFluxRepository characterFluxRepository)
    {
        return new ReactorGQLEpisodeServide(episodeApiClient, characterFluxRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.episode-service", havingValue = "reactor")
    public EpisodeService reactorEpisodiApi(EpisodeApiWebClient episodeApiClient,
            CharacterFluxRepository characterFluxRepository)
    {
        return new ReactorEpisodeService(episodeApiClient, characterFluxRepository);
    }

}
