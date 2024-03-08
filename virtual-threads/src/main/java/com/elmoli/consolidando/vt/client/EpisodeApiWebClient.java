/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.client;

import com.elmoli.consolidando.vt.repository.CharacterFlux;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for interacting with the Rick and Morty API.
 */
@Service
@Profile(
        {
            "flux"
        })
public class EpisodeApiWebClient
{

    private static final Logger logger = LoggerFactory.getLogger(EpisodeApiWebClient.class);
    private final WebClient webClient;

    /**
     * Constructor that receives the base URL of the Rick and Morty API.
     *
     * @param apiUrl The base URL of the Rick and Morty API.
     */
    public EpisodeApiWebClient(@Value("${app.rick-and-Morty-api-url}") String apiUrl)
    {
        this.webClient = WebClient.create(apiUrl);
    }

    /**
     * Gets the number of episodes from the API.
     *
     * @return A Mono emitting the number of episodes.
     */
    public Mono<Integer> getNumberOfEpisodes()
    {
        String graphqlQuery = "{ episodes { info { count } } }";

        return webClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .bodyToMono(EpisodeCountData.class)
                .map(response -> response.data().episodes().info().count());
    }

    /**
     * Gets information about an episode from the API.
     *
     * @param episodeId The ID of the episode.
     * @return A Mono emitting a list of characters ids in the episode.
     */
    public Mono<List<EpisodeCharactersIdData.CharacterId>> getEpisodeInfo(int episodeId)
    {
        String graphqlQuery = String.format("{ episode(id: %d) { id characters { id } } }", episodeId);

        return webClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .bodyToMono(EpisodeCharactersIdData.class)
                .map(response -> response.data().episode().characters())
                .map(Collections::unmodifiableList);
    }

    /**
     * Gets information about multiple characters from the API.
     *
     * @param characterIds A list of character IDs.
     * @return A Flux emitting character information for each character ID.
     */
    public Flux<CharacterFlux> getCharacterInfo(List<String> characterIds)
    {
        return Flux.fromIterable(characterIds)
                .flatMap(characterId -> getCharacterInfo(Integer.parseInt(characterId)))
                .onErrorResume(error ->
                {
                    return Flux.empty();
                });
    }

    /**
     * Gets information about a character from the API.
     *
     * @param characterId The ID of the character.
     * @return A Mono emitting information about the character.
     */
    public Mono<CharacterFlux> getCharacterInfo(int characterId)
    {
        return webClient.get()
                .uri("/api/character/{id}", characterId)
                .retrieve()
                .bodyToMono(CharacterFlux.class)
                .onErrorMap(ex -> handleClientError(ex, characterId));
    }

    public Flux<CharacterFlux> getCharactersEpisode(int episodeId)
    {
        String graphqlQuery = """
        { episode(id: %d) 
          {
            characters {
              id
              name
              status
              species
              gender
              origin {
                name
                id
              }
              location {
                name
                id
              }
              image      
              episode {
                id
              }
              created
            }
          }
        }                      
        """.formatted(episodeId).replaceAll("\\n\\s*", " ");

        Mono<EpisodeCharactersData> episodeCharactersDataMono = webClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .bodyToMono(EpisodeCharactersData.class)
                .onErrorMap(ex ->
                {
                    if (ex instanceof WebClientResponseException webClientResponseException)
                    {                        
                        logger.error("Error fetching {}", webClientResponseException.getResponseBodyAsString());
                    }
                    logger.error("Error {} ", ex);
                    return ex;
                });
        

        return episodeCharactersDataMono.flatMapMany(data
                -> Flux.fromIterable(data != null ? data.data().episode().characters() : Collections.emptyList())
                        .map(CharacterFlux::of));
    }

    private WebClientResponseException handleClientError(Throwable ex, int characterId)
    {
        WebClientResponseException responseException;
        if (ex instanceof WebClientResponseException webClientResponseException)
        {
            responseException = webClientResponseException;
            logger.error("Error fetching character info for character {}: {}", characterId, responseException.getResponseBodyAsString(), ex);
        } else
        {

            responseException = WebClientResponseException
                    .create(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error", null, null, null);
            responseException.addSuppressed(ex);
        }
        return (responseException);
    }
}
