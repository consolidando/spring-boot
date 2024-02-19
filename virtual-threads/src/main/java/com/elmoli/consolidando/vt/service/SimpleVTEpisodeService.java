/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.repository.Character;
import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author joanr
 */
public class SimpleVTEpisodeService extends VTEpisodeService
{

    private static final Logger logger = LoggerFactory.getLogger(SimpleVTEpisodeService.class);

    @Value("${app.useSaveAllInRepository}")
    private boolean useSaveAllInRepository;

    public SimpleVTEpisodeService(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        super(episodeApiClient, characterRepository);
    }

    @Override
    public void logTitle()
    {
        logger.info("---- VIRTUAL THREADS SaveAll {} ---- ", useSaveAllInRepository);
    }

    public boolean getAndSaveAllCharactersInParallell(List<EpisodeCharactersIdData.CharacterId> episodeInfoDataList)
            throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(episodeInfoDataList.size());
        episodeInfoDataList.forEach(episodeInfoData ->
        {
            Thread.ofVirtual().start(() ->
            {
                getAndSaveCharacter(episodeInfoData);
                latch.countDown();
            });
        });

        return (latch.await(CHARACTERS_FETCH_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS));

    }

    public boolean getAllInParallellAndSaveAllCharacters(List<EpisodeCharactersIdData.CharacterId> episodeCharacterIds)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        List<CompletableFuture<Character>> futures = episodeCharacterIds.stream()
                .map(characterId ->
                {
                    CompletableFuture<Character> future = new CompletableFuture<>();
                    Runnable task = () ->
                    {
                        try
                        {
                            Character character = getCharacter(characterId.id());
                            future.complete(character);
                        } catch (Exception e)
                        {
                            future.completeExceptionally(e);
                        }
                    };
                    Thread.ofVirtual().start(task);
                    return future;
                })
                .collect(Collectors.toList());

        //CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        //boolean allCompleted = allOfFutures.get(CHARACTERS_FETCH_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS) != null;
        boolean allCompleted = true;
        if (allCompleted)
        {
            List<Character> characters = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            getRepository().saveAll(characters);
        }

        return allCompleted;
    }

    @Override
    public boolean getAndSaveAllCharacters(Integer episodeId) throws Exception
    {
        //
        List<EpisodeCharactersIdData.CharacterId> episodeInfoDataList = getEpisodeInfo(episodeId);
        logger.info("Characters Number: {}", episodeInfoDataList.size());

        if (useSaveAllInRepository)
        {
            return (getAllInParallellAndSaveAllCharacters(episodeInfoDataList));
        } else
        {
            return (getAndSaveAllCharactersInParallell(episodeInfoDataList));
        }
    }

}
