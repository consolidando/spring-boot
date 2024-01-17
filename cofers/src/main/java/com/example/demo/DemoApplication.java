package com.example.demo;

import com.example.demo.service.EpisodeInfoData;
import com.example.demo.domain.CharacterInfo;
import com.example.demo.domain.CharacterInfoRepository;
import com.example.demo.service.EpisodeApiClient;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner

{

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @Autowired
    private EpisodeApiClient episodeApiClient;

    @Autowired
    private CharacterInfoRepository characterRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * Executes the application logic: - Retrieves the total number of episodes
     * from the Rick and Morty API. - Selects a random episode. - Fetches
     * information about characters in the selected episode. - Saves character
     * information in the repository and logs their names.
     *
     * Note: This method is part of the application's initialization process.
     *
     * @param args Command-line arguments.
     * @throws Exception If an error occurs during the execution.
     */
    @Override
    public void run(String... args) throws Exception
    {
        try
        {
            Mono<Integer> numberOfEpisodesMono = episodeApiClient.getNumberOfEpisodes();
            int numberOfEpisodes = numberOfEpisodesMono.block();

            logger.info("Number of episodes: {}", numberOfEpisodes);

            Random random = new Random();
            int episodeId = random.nextInt(numberOfEpisodes) + 1;

            logger.info("Episode Number: {}", episodeId);

            Mono<List<EpisodeInfoData.Character>> episodeInfoMono = episodeApiClient.getEpisodeInfo(episodeId);
            List<EpisodeInfoData.Character> episodeInfoData = episodeInfoMono.block();

            if (episodeInfoData != null)
            {
                Flux<EpisodeInfoData.Character> charactersFlux = Flux.fromIterable(episodeInfoData);

                List<CharacterInfo> savedCharacters = charactersFlux
                        .parallel()
                        .runOn(Schedulers.parallel())
                        .flatMap(character -> episodeApiClient.getCharacterInfo(character.id()))
                        .flatMap(characterInfo -> Mono.fromCallable(() ->
                {
                    try
                    {
                        logger.info("Character Name: {}", characterInfo.getName());
                        characterRepository.save(characterInfo);
                        return characterInfo;
                    } catch (Exception e)
                    {
                        logger.error("Error saving in the repository: {}", e.getMessage());

                        return null;
                    }
                }).subscribeOn(Schedulers.parallel()))
                        .sequential()
                        .collectList()
                        .block();
            }
            
            logger.info("Database is ready");

        } catch (Exception e)
        {
            logger.error("API Request Error: {}", e.getMessage());
        }

    }

}
