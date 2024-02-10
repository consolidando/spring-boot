/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.config;

import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.service.EpisodeService;
import com.elmoli.consolidando.vt.service.GraphQlEpisodeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author joanr
 */

@Configuration
@Profile("graphql")
public class GraphQlEpisodeServiceConfig
{
        @Bean
    public EpisodeService episodiApi(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        return new GraphQlEpisodeService(episodeApiClient, characterRepository);
    }
}
