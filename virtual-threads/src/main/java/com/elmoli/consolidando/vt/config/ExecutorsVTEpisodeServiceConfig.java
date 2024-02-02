/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.config;

import com.elmoli.consolidando.vt.service.ExecutorsVTEpisodeService;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.elmoli.consolidando.vt.service.EpisodeService;

/**
 *
 * @author joanr
 */
@Configuration
@Profile("executors")
public class ExecutorsVTEpisodeServiceConfig
{
    @Bean
    public EpisodeService episodiApi(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        return new ExecutorsVTEpisodeService(episodeApiClient, characterRepository);
    }
}
