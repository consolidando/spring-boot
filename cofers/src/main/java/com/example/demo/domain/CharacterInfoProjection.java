package com.example.demo.domain;

import org.springframework.data.rest.core.config.Projection;


/**
 * Projection interface for character information from the Rick and Morty API,
 * stored in the repository. Used to return a subset of fields (name and status)
 * in the response of the collection GET /apis/character endpoint.
 */

@Projection(name = "characterInfoProjection", types = { CharacterInfo.class })
public interface CharacterInfoProjection
{
    String getName();
    String getStatus();
}
