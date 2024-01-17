package com.example.demo.service;

import com.example.demo.domain.CharacterInfo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EpisodeApiClient
{
    private static final String BASE_URL = "https://rickandmortyapi.com";
    private static final WebClient webClient = WebClient.create(BASE_URL);

    public Mono<Integer> getNumberOfEpisodes()
    {
        String graphqlQuery = "{ episodes { info { count } } }";

        return webClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .bodyToMono(EpisodeData.class)
                .map(response -> response.data().episodes().info().count());
    }

    public Mono<List<EpisodeInfoData.Character>> getEpisodeInfo(int episodeId)
    {
        String graphqlQuery = String.format("{ episode(id: %d) { id characters { id } } }", episodeId);

        return webClient.post()
                .uri("/graphql")
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{\"query\":\"" + graphqlQuery + "\"}")
                .retrieve()
                .bodyToMono(EpisodeInfoData.class)
                .map(response -> response.data().episode().characters())
                .map(characterList ->
                {
                    List<EpisodeInfoData.Character> characters = new ArrayList<>(characterList);
                    return characters;
                });
    }

    public Flux<CharacterInfo> getCharacterInfo(List<String> characterIds)
    {
        return Flux.fromIterable(characterIds)
                .flatMap(characterId -> getCharacterInfo(Integer.parseInt(characterId)));
    }

    public Mono<CharacterInfo> getCharacterInfo(int characterId)
    {
        return webClient.get()
                .uri("/api/character/{id}", characterId)
                .retrieve()
                .bodyToMono(CharacterInfo.class);
    }
}
