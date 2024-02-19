/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.repository.Character;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author joanr
 */
public class ExecutorsVTEpisodeService extends VTEpisodeService
{

    private static final Logger logger = LoggerFactory.getLogger(ExecutorsVTEpisodeService.class);

    @Value("${app.useSaveAllInRepository}")
    private boolean useSaveAllInRepository;

    public ExecutorsVTEpisodeService(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        super(episodeApiClient, characterRepository);

    }

    @Override
    public void logTitle()
    {
        logger.info("---- VIRTUAL THREADS WITH EXECUTORS SaveAll {} ----", useSaveAllInRepository);
    }

    public boolean getAndSaveAllCharactersInParallell(List<EpisodeCharactersIdData.CharacterId> episodeInfoDataList) throws Exception
    {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor())
        {
            episodeInfoDataList.stream()
                    .map(episodeInfoData -> (Runnable) () -> getAndSaveCharacter(episodeInfoData))
                    .toList().forEach(executorService::submit);

            executorService.shutdown();

            return (executorService.awaitTermination(
                    CHARACTERS_FETCH_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS));
        }
    }

    public boolean getAllInParallellAndSaveAllCharacters(List<EpisodeCharactersIdData.CharacterId> episodeInfoDataList) throws Exception
    {
        List<Callable<Character>> tasks
                = episodeInfoDataList.stream()
                        .map(episodeInfoData -> (Callable<Character>) () -> getCharacter(episodeInfoData.id()))
                        .toList();

        List<Future<Character>> futures;
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor())
        {
            futures = executorService.invokeAll(tasks);
        }

        List<Character> character = new ArrayList<>();
        for (Future<Character> future : futures)
        {
            character.add(future.get());
        }

        getRepository().saveAll(character);

        return (true);
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
