/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.client.EpisodeCharactersData;
import java.util.List;
import org.springframework.data.repository.Repository;

/**
 *
 * @author joanr
 */
public interface EpisodeService
{
    final long CHARACTERS_FETCH_TIMEOUT_IN_MILLISECONDS = 4000;

    public Integer getRandomEpisodeId();

    public List<EpisodeCharactersData.Character> getEpisodeInfo(Integer episodeId);

    public boolean getAndSaveCharacters(List<EpisodeCharactersData.Character> episodeInfoDataList) throws Exception;
    
    public boolean isErrorInProgress();
    
    public Repository getRepository();

}
