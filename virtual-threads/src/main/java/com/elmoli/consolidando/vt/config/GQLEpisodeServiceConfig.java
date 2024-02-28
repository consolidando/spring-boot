/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.config;

import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.service.EpisodeService;
import com.elmoli.consolidando.vt.service.GQLEpisodeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author joanr
 */

@Configuration
@Profile("graphql")
public class GQLEpisodeServiceConfig
{
        @Bean
    public EpisodeService episodiApi(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        return new GQLEpisodeService(episodeApiClient, characterRepository);
    }
}
