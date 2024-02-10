package com.elmoli.consolidando.vt.repository;

/**
 * Represents character information retrieved from the Rick and Morty API,
 * specifically designed to be saved in the repository using JPA.
 */
import com.elmoli.consolidando.vt.client.EpisodeCharactersData;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("character")
public record Character(
        @Id
        Integer id,
        @Version
        Integer version, // version = 0 => object new => allows to set a specific id
        String name,
        String status,
        String species,
        String type,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "origin_")
        OriginInfo origin,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "location_")
        LocationInfo location,
        String image,
        List<String> episode,
        String url,
        Instant created)
        {

    public record OriginInfo(
            String name,
            String url)
            {

    }

    public record LocationInfo(
            String name,
            String url)
            {

    }
    
    public static Character of (EpisodeCharactersData.Character character)
    {        
        List<String> episodeIds = character.episode().stream()
                .map(map->map.id())
                .collect(Collectors.toList());
        
        Character entity = new Character
        (
                character.id(),
                null,
                character.name(),
                character.status(),
                character.species(),
                character.gender(),
                new OriginInfo(
                        character.origin().name(),
                        character.origin().id()),
                new LocationInfo(
                        character.location().name(),
                        character.location().id()),
                character.image(),
                episodeIds, 
                String.valueOf(character.id()),
                character.created()                
        );
        
        return entity;
    }
}
