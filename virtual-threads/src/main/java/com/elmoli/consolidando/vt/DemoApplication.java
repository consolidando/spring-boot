package com.elmoli.consolidando.vt;

import com.elmoli.consolidando.vt.client.EpisodeCharactersData;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;
import com.elmoli.consolidando.vt.service.EpisodeService;

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
                                                
            logger.info("-- Running run in thread: {}", Thread.currentThread());
            long startTime = System.currentTimeMillis();

            // Rpisode Id
            if (args.length == 1)
            {
                episodeId = Integer.parseInt(args[0]);
            } else
            {
                episodeId = episodeApi.getRandomEpisodeId();
            }
            logger.info("Episode Number: {}", episodeId);

            //
            List<EpisodeCharactersData.Character> episodeInfoDataList = episodeApi.getEpisodeInfo(episodeId);
            logger.info("Characters Number: {}", episodeInfoDataList.size());

            //    
            if (episodeApi.getAndSaveCharacters(episodeInfoDataList))
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
