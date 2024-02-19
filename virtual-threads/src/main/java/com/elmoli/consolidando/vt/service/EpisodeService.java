/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.service;

import com.elmoli.consolidando.vt.client.EpisodeCharactersIdData;
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
    
    public List<EpisodeCharactersIdData.CharacterId> getEpisodeInfo(Integer episodeId);
   
    public boolean getAndSaveAllCharacters(Integer episodeId) throws Exception;
    
    public boolean isErrorInProgress();
    
    public Repository getRepository();
    
    public void logTitle();               

}
