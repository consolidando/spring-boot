/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.service.VTEpisodeService;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersData;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joanr
 */

public class SimpleVTEpisodeService extends VTEpisodeService
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleVTEpisodeService.class);

    public SimpleVTEpisodeService(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        super(episodeApiClient, characterRepository);
        logger.info("---- VIRTUAL THREADS ----");        
    }


    @Override
    public boolean getAndSaveCharacters(List<EpisodeCharactersData.Character> episodeInfoDataList)
            throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(episodeInfoDataList.size());
        episodeInfoDataList.forEach(episodeInfoData ->
        {
            Thread.ofVirtual().start(() ->
            {
                getAndSaveCharacterInfo(episodeInfoData);
                latch.countDown();
            });
        });

        return(latch.await(CHARACTERS_FETCH_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS));  
        
    }



}
