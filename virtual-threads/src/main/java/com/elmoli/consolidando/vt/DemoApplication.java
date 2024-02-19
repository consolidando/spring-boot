/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import com.elmoli.consolidando.vt.service.EpisodeService;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner
{    
    public enum DatabaseState
    {
        NOT_INITIALIZED,
        INITIALIZED,
        TIMEOUT,
        ERROR
    }
 
    @Value("${app.episode}")
    private int episodeId = 0;
    private DatabaseState dataBaseState = DatabaseState.NOT_INITIALIZED;

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    private final EpisodeService episodeApi;

    DemoApplication(EpisodeService episodeApi)
    {
        this.episodeApi = episodeApi;       
    }

    public static void main(String[] args)
    {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args)
    {

        try
        {
            episodeApi.logTitle();
            
            logger.info("-- Running run in thread: {}", Thread.currentThread());
            long startTime = System.currentTimeMillis();
           
            if (episodeId == 0)
            {
                episodeId = episodeApi.getRandomEpisodeId();
            }
            logger.info("Episode Number: {}", episodeId);
            

            //    
            if (episodeApi.getAndSaveAllCharacters(episodeId))
            {
                if (episodeApi.isErrorInProgress() == true)
                {
                    logger.error("Error in some thread");
                    this.dataBaseState = DatabaseState.ERROR;
                } else
                {
                    long endTime = System.currentTimeMillis();
                    logger.info("Database is ready in {} milliseconds", endTime - startTime);
                    this.dataBaseState = DemoApplication.DatabaseState.INITIALIZED;
                }
            } else
            {
                this.dataBaseState = DemoApplication.DatabaseState.TIMEOUT;
                logger.info("Timeout");
            }

        } catch (Exception e)
        {
            this.dataBaseState = DatabaseState.ERROR;
            logger.error("Error initializing Database: {}", e.getMessage(), e);
        }
    }

    /**
     * Get the current state of the database.
     *
     * @return The state of the database.
     */
    public DatabaseState getDatabaseState()
    {
        return this.dataBaseState;
    }

    /**
     * Get the ID of the episode for characters in the database.
     *
     * @return The episode ID.
     */
    public int getDatabaseCharactersEpisodeId()
    {
        return this.episodeId;
    }

    public Repository getRepository()
    {
        return (episodeApi.getRepository());
    }

}
