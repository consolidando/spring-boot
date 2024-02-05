/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiWebClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersData;
import com.elmoli.consolidando.vt.repository.CharacterFlux;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.elmoli.consolidando.vt.service.EpisodeService;

/**
 *
 * @author joanr
 */
public class ReactorEpisodeService implements EpisodeService
{

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
    public List<EpisodeCharactersData.Character> getEpisodeInfo(Integer episodeId)
    {
        // 3
        Mono<List<EpisodeCharactersData.Character>> episodeInfoMono = episodeApiClient.getEpisodeInfo(episodeId);
        List<EpisodeCharactersData.Character> episodeInfoData = episodeInfoMono.block();
        return (episodeInfoData);
    }

    @Override
    public boolean getAndSaveCharacters(List<EpisodeCharactersData.Character> episodeInfoDataList) throws Exception
    {
        Flux<EpisodeCharactersData.Character> charactersFlux = Flux.fromIterable(episodeInfoDataList);

        try
        {
            charactersFlux
                    .parallel()
                    .runOn(Schedulers.parallel())
                    .flatMap(character -> episodeApiClient.getCharacterInfo(character.id()))
                    .flatMap(characterInfo -> Mono
                    .fromCallable(() ->
                    {
                        // Log character info
                        logger.info("-- {} | Character Name: {}", Thread.currentThread(), characterInfo.getId());
                        // Save to repository        
                        //characterInfo.setId(0);
                        return (characterRepository.save(characterInfo));
                    })
                    .subscribeOn(Schedulers.parallel())
                    .onErrorResume(e ->
                    {
                        errorInProgress = true;
                        logger.error("Error saving in the repository: {}", e.getMessage());
                        return Mono.empty();
                    }))
                    .sequential()
                    .collectList()
                    .flatMap(characterInfos ->
                    {                                              
                        return Mono.just(characterInfos); 
                    })
                    .doFinally(signalType ->
                    {
                        logger.info("Reactive flow completed with signal: {}", signalType);
                    })
                    .block()
                    .forEach(Mono::block);
 
            return true;
        } catch (Exception e)
        {
            logger.error("Exception during getAndSaveCharacters: {}", e.getMessage());
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
}
