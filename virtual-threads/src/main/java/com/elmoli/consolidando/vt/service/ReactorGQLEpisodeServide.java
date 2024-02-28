/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.client.EpisodeApiWebClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
import com.elmoli.consolidando.vt.repository.CharacterFlux;
import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author joanr
 */
public class ReactorGQLEpisodeServide implements EpisodeService
{

    private static final Logger logger = LoggerFactory.getLogger(ReactorGQLEpisodeServide.class);
    EpisodeApiWebClient episodeApiClient;
    CharacterFluxRepository characterRepository;
    private boolean errorInProgress = false;

    public ReactorGQLEpisodeServide(EpisodeApiWebClient episodeApiClient,
            CharacterFluxRepository characterRepository)
    {
        this.episodeApiClient = episodeApiClient;
        this.characterRepository = characterRepository;
    }

    @Override
    public Integer getRandomEpisodeId()
    {
        // 1
        Mono<Integer> numberOfEpisodesMono = episodeApiClient.getNumberOfEpisodes();
        int numberOfEpisodes = numberOfEpisodesMono.block();

        logger.info("Number of episodes: {}", numberOfEpisodes);

        // 2
        Random random = new Random();
        return (random.nextInt(numberOfEpisodes) + 1);
    }

    @Override
    public List<EpisodeCharactersIdData.CharacterId> getEpisodeInfo(Integer episodeId)
    {
        // 3
        Mono<List<EpisodeCharactersIdData.CharacterId>> episodeInfoMono = episodeApiClient.getEpisodeInfo(episodeId);
        List<EpisodeCharactersIdData.CharacterId> episodeInfoData = episodeInfoMono.block();
        return (episodeInfoData);
    }

    @Override
    public boolean getAndSaveAllCharacters(Integer episodeId) throws Exception
    {
        try
        {
            Flux<CharacterFlux> characters = episodeApiClient.getCharactersEpisode(episodeId);
            characterRepository.saveAll(characters).blockLast();
            return true;
        } catch (Exception e)
        {

            return false;
        }
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
    public void logTitle()
    {
        logger.info("---- REACTOR GraphQL  ----");
    }

}
