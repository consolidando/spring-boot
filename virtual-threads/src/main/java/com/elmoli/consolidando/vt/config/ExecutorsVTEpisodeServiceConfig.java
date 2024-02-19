/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
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
