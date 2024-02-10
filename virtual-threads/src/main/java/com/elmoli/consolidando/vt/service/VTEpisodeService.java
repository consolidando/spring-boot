/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.repository.Character;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import com.elmoli.consolidando.vt.client.EpisodeApiRestClient;
import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;

/**
 *
 * @author joanr
 */
public abstract class VTEpisodeService implements EpisodeService
{
    private static final Logger logger = LoggerFactory.getLogger(VTEpisodeService.class);
    EpisodeApiRestClient episodeApiClient;
    CharacterRepository characterRepository;
    private boolean errorInProgress = false;
    

    public VTEpisodeService(EpisodeApiRestClient episodeApiClient,
            CharacterRepository characterRepository)
    {
        this.characterRepository = characterRepository;
        this.episodeApiClient = episodeApiClient;
    }

    @Override
    public Integer getRandomEpisodeId()
    {
        Integer numberOfEpisodes = episodeApiClient.getNumberOfEpisodes();
        logger.info("Number of episodes: {}", numberOfEpisodes);
        return new Random().nextInt(numberOfEpisodes) + 1;
    }

    @Override
    public List<EpisodeCharactersIdData.CharacterId> getEpisodeInfo(Integer episodeId)
    {
        return (episodeApiClient.getEpisodeInfo(episodeId));
    }

    public Character getCharacter(Integer characterId)
    {
        logger.info("-- {} | Character Id: {}", Thread.currentThread(), characterId);
        return (episodeApiClient.getCharacterInfo(characterId));
    }

    public void getAndSaveCharacter(EpisodeCharactersIdData.CharacterId episodeInfoData)
    {
        try
        {
            Character character = getCharacter(episodeInfoData.id());            
            characterRepository.save(character);
        } catch (Exception e)
        {
            errorInProgress = true;
            logger.error("Error saving in the repository: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean isErrorInProgress()
    {
        return(errorInProgress);
    }       
    
    @Override
    public CharacterRepository getRepository()
    {
        return(characterRepository);
    }

}
