/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiWebClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 *
 * @author joanr
 */
public class ReactorEpisodeService implements EpisodeService
{

    @Value("${app.useSaveAllInRepository}")
    private boolean useSaveAllInRepository;

    @Value("10")
    private int defaultInt;

    private static final Logger logger = LoggerFactory.getLogger(ReactorEpisodeService.class);
    EpisodeApiWebClient episodeApiClient;
    CharacterFluxRepository characterRepository;
    private boolean errorInProgress = false;

    public ReactorEpisodeService(EpisodeApiWebClient episodeApiClient,
            CharacterFluxRepository characterRepository)
    {
        this.episodeApiClient = episodeApiClient;
        this.characterRepository = characterRepository;        
    }

    @Override
    public void logTitle()
    {
        logger.info("---- REACTOR SaveAll {} ----", useSaveAllInRepository);
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

    private boolean getAllandSaveInParallelInRepository(Flux<EpisodeCharactersIdData.CharacterId> charactersFlux)
    {
        charactersFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(character ->
                {
                    logger.info("-- {} | Character Id: {}", Thread.currentThread(), character.id());
                    return (episodeApiClient.getCharacterInfo(character.id()));
                })
                .flatMap(characterInfo -> characterRepository.save(characterInfo))
                .then()
                .doOnTerminate(() ->
                {
                    logger.info("Reactive flow completed.");
                })
                .block();

        return (true);
    }

    private boolean getAllAndSaveAllInRepository(Flux<EpisodeCharactersIdData.CharacterId> charactersFlux)
    {
        charactersFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(character ->
                {
                    logger.info("-- {} | Character Id: {}", Thread.currentThread(), character.id());
                    return (episodeApiClient.getCharacterInfo(character.id()));
                })
                .sequential()
                .collectList()
                .flatMap(characterInfos ->
                {
                    return characterRepository.saveAll(Flux.fromIterable(characterInfos))
                            .collectList();
                })
                .doFinally(signalType ->
                {
                    logger.info("Reactive flow completed with signal: {}", signalType);
                })
                .block();

        return (true);
    }

    public boolean getAndSaveCharacters(List<EpisodeCharactersIdData.CharacterId> episodeInfoDataList) throws Exception
    {
        Flux<EpisodeCharactersIdData.CharacterId> charactersFlux = Flux.fromIterable(episodeInfoDataList);

        try
        {
            if (useSaveAllInRepository)
            {
                getAllandSaveInParallelInRepository(charactersFlux);
            } else
            {
                getAllAndSaveAllInRepository(charactersFlux);
            }
        } catch (Exception e)
        {
            logger.error("Exception during getAndSaveCharacters: {}", e.getMessage());
            errorInProgress = true;
        }
        return true;
    }

    @Override
    public boolean getAndSaveAllCharacters(Integer episodeId) throws Exception
    {
        //
        List<EpisodeCharactersIdData.CharacterId> episodeInfoDataList = getEpisodeInfo(episodeId);
        logger.info("Characters Number: {}", episodeInfoDataList.size());

        return (getAndSaveCharacters(episodeInfoDataList));
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

}
