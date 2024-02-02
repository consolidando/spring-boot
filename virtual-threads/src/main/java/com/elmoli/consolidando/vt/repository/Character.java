package com.elmoli.consolidando.vt.repository;

/**
 * Represents character information retrieved from the Rick and Morty API,
 * specifically designed to be saved in the repository using JPA.
 */
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Embedded;

public record Character(
        @Id
        int id,
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
}
