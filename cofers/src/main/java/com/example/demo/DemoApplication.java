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

    @Override
    public void run(String... args) throws Exception
    {
        try
        {                        
            Mono<Integer> numberOfEpisodesMono = episodeApiClient.getNumberOfEpisodes();
            int numberOfEpisodes = numberOfEpisodesMono.block();

            System.out.println("Number of episodes: " + numberOfEpisodes);

            Random random = new Random();
            int episodeId = random.nextInt(numberOfEpisodes) + 1;

            System.out.println("Episode Number: " + episodeId);

            Mono<List<EpisodeInfoData.Character>> episodeInfoMono = episodeApiClient.getEpisodeInfo(episodeId);
            List<EpisodeInfoData.Character> episodeInfoData = episodeInfoMono.block();
                        
            if (episodeInfoData != null)
            {
                Flux<EpisodeInfoData.Character> charactersFlux = Flux.fromIterable(episodeInfoData);

                Flux<CharacterInfo> characterInfoFlux = charactersFlux
                        .flatMap(character -> episodeApiClient.getCharacterInfo(character.id()));

                characterInfoFlux.subscribe(characterInfo ->
                {
                    try
                    {
                        characterRepository.save(characterInfo);
                    } catch (Exception e)
                    {
                        logger.error("Error saving in the repository: {}", e.getMessage());
                    }

                    logger.info("Character Name: {}", characterInfo.getName());

                });
            }
                        
            logger.info("App has been intilised");

        } catch (Exception e)
        {
            logger.error("API Request Error: {}", e.getMessage());
        }

    }

}
