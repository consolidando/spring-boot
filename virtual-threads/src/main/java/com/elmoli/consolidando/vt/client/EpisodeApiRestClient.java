package com.elmoli.consolidando.vt.client;

import com.elmoli.consolidando.vt.repository.Character;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Client for interacting with the Rick and Morty API.
 */
@Service
@Profile(
{
    "simple", "executors", "graphql"
})
public class EpisodeApiRestClient
{

    private static final Logger logger = LoggerFactory.getLogger(EpisodeApiRestClient.class);
    private final RestClient restClient;

    public EpisodeApiRestClient(@Value("${app.rick-and-Morty-api-url}") String apiUrl)
    {
        this.restClient = RestClient.create(apiUrl);
    }

    public Integer getNumberOfEpisodes()
    {
        String graphqlQuery = "{ episodes { info { count } } }";

        return restClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .body(EpisodeCountData.class).data().episodes().info().count();
    }

    public List<EpisodeCharactersIdData.CharacterId> getEpisodeInfo(int episodeId)
    {
        String graphqlQuery = String.format("{ episode(id: %d) { id characters { id } } }", episodeId);

        EpisodeCharactersIdData episodeInfoData = restClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .body(EpisodeCharactersIdData.class);

        List<EpisodeCharactersIdData.CharacterId> characters = Optional.ofNullable(episodeInfoData)
                .map(data -> data.data().episode().characters())
                .orElse(Collections.emptyList());

        return characters;

    }

    public Character getCharacterInfo(int characterId)
    {
        return restClient.get()
                .uri("/api/character/{id}", characterId)
                .retrieve()
                .body(Character.class);
    }

    public List<Character> getCharactersEpisode(int episodeId)
    {
        String graphqlQuery
                = """
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

        EpisodeCharactersData episodeCharactersData = restClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .body(EpisodeCharactersData.class);

        List<Character> characters = Optional.ofNullable(episodeCharactersData)
                .map(data -> data.data().episode().characters().stream()
                .map(Character::of)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        return characters;
    }

}
