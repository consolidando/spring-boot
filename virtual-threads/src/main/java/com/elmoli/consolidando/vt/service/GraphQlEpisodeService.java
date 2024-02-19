/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.repository.Character;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;

/**
 *
 * @author joanr
 */
public class GraphQlEpisodeService implements EpisodeService
{

    private static final Logger logger = LoggerFactory.getLogger(GraphQlEpisodeService.class);
    EpisodeApiRestClient episodeApiClient;
    CharacterRepository characterRepository;
    private boolean errorInProgress = false;

    public GraphQlEpisodeService(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        this.characterRepository = characterRepository;
        this.episodeApiClient = episodeApiClient;

    }

    @Override
    public void logTitle()
    {
        logger.info("---- BLOCKING GRAPHQL ONE REQUEST ----");
    }

    @Override
    public Integer getRandomEpisodeId()
    {
        Integer numberOfEpisodes = episodeApiClient.getNumberOfEpisodes();
        logger.info("Number of episodes: {}", numberOfEpisodes);
        return new Random().nextInt(numberOfEpisodes) + 1;
    }

    @Override
    public boolean getAndSaveAllCharacters(Integer episodeId) throws Exception
    {
        List<Character> characters
                = episodeApiClient.getCharactersEpisode(episodeId);
        characterRepository.saveAll(characters);
        return (true);
    }

    @Override
    public boolean isErrorInProgress()
    {
        return (errorInProgress);
    }

    @Override
    public Repository getRepository()
    {
        return (characterRepository);
    }

    @Override
    public List<EpisodeCharactersIdData.CharacterId> getEpisodeInfo(Integer episodeId)
    {
        return (episodeApiClient.getEpisodeInfo(episodeId));
    }

}
