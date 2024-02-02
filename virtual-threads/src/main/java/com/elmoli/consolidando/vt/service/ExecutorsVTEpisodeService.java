/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.service;


import com.elmoli.consolidando.vt.service.VTEpisodeService;
import com.elmoli.consolidando.vt.repository.Character;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersData;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joanr
 */
public class ExecutorsVTEpisodeService extends VTEpisodeService
{

    private static final Logger logger = LoggerFactory.getLogger(ExecutorsVTEpisodeService.class);

    public ExecutorsVTEpisodeService(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        super(episodeApiClient, characterRepository);
        logger.info("---- VIRTUAL THREADS WITH EXECUTORS ----");
    }

    @Override
    public boolean getAndSaveCharacters(List<EpisodeCharactersData.Character> episodeInfoDataList) throws Exception
    {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor())
        {
            episodeInfoDataList.stream()
                    .map(episodeInfoData -> (Runnable) () -> getAndSaveCharacterInfo(episodeInfoData))
                    .toList().forEach(executorService::submit);

            executorService.shutdown();

            return (executorService.awaitTermination(
                    CHARACTERS_FETCH_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS));
        }
    }



}
